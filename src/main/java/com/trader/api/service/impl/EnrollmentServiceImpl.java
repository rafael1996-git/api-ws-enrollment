package com.trader.api.service.impl;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.trader.api.dao.EnrollmentDao;
import com.trader.api.domain.request.CertificateUrl;
import com.trader.api.domain.request.CertificateUser;
import com.trader.api.domain.request.PersonalInformation;
import com.trader.api.domain.responce.CerResponse;
import com.trader.api.domain.responce.ListBank;
import com.trader.api.domain.responce.StatusCert;
import com.trader.api.models.*;
import com.trader.api.service.EnrollmentService;
import com.trader.api.utils.S3Utils;
import com.trader.api.utils.UGson;
import com.trader.api.utils.enums.ContentTypeEnum;
import com.trader.core.enums.ResponseEnum;
import com.trader.core.models.ResponseModel;
import com.trader.core.utils.EnvironmentData;
import com.trader.core.utils.GsonParserUtils;
import com.trader.core.utils.ResponseUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentDao dao;

    @Override
    public ResponseModel searchInfoBank(String bankKey) {
        ResponseModel response;
        try {
            if (bankKey == null || bankKey.length() < 3){
                throw new Exception("Minimum 3 digits required");
            }
            ListBank infoBank = dao.getinfoBank(bankKey);
            response = ResponseUtils.createResponse(Collections.singletonList(infoBank.toJsonObject()),ResponseEnum.EXITO);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println(new StringBuilder("EnrollmentServiceImpl.DBException - savePersonalDocument: ").append(e.getMessage()));
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel savePersonalInformation(PersonalInformation data) {
        ResponseModel response;
        Message message = new Message();
        System.out.println(GsonParserUtils.getGson().toJson(data));
        try {
            boolean status = dao.savePersonalInformation(data);
            if (status) {
                message.setMessage("Data saved successfully");
                response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.EXITO);
            }else {
                message.setMessage("Save failed");
                response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("EnrollmentServiceImpl.DBException - savePersonalDocument: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel savePersonalDocuments(PersonalInformation data) {
        ResponseModel response;
        Message message = new Message();
        try {
            validateUrlDocument(data);
            boolean status = dao.savePersonalDocuments(data);
            if (status) {
                message.setMessage("Data saved successfully");
                response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.EXITO);
            }else {
                message.setMessage("Save failed");
                response = ResponseUtils.createResponse(Collections.singletonList(message.toJsonObject()),ResponseEnum.ERROR);
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("EnrollmentServiceImpl.DBException - savePersonalDocument: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.NO_SE_ENCONTRARON_DATOS, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel saveCredentials(MultipartFile fileCer, MultipartFile fileKey, User data) {
        SendSQS dataSQS = new SendSQS();
        System.out.println("SERVICE DATA " + data);
        ResponseModel response;
        Message message = new Message();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        date.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        String timeStamp = date.format(new Date());
        String extCer = FilenameUtils.getExtension(fileCer.getOriginalFilename());
        String extKey = FilenameUtils.getExtension(fileKey.getOriginalFilename());
        System.out.println("extCer: "+ extCer);
        System.out.println("extKey: "+ extKey);

        try {
            /*if (!Objects.equals(extCer, "cer"))
                throw new Exception("The format: " + FilenameUtils.getExtension(fileCer.getOriginalFilename()) +" is not supported");
            if (!Objects.equals(extKey, "key") )
                throw new Exception("The format: " + FilenameUtils.getExtension(fileKey.getOriginalFilename()) +" is not supported");*/

            String bucket = EnvironmentData.getPropertyValue("BucketName");
            String keyCer = "";
            String keyKey = "";

            if (!extCer.equals("")){
                if (!Objects.equals(extCer, "cer"))
                    throw new Exception("The format: " + FilenameUtils.getExtension(fileCer.getOriginalFilename()) +" is not supported");

                keyCer = "Credentials/" + data.getFcUser() + "/" +timeStamp +"/" + fileCer.getOriginalFilename();
                System.out.println("if cer");
                S3Utils.uploadFileToS3(bucket,
                        keyCer,
                        fileCer.getInputStream(),
                        ContentTypeEnum.APPLICATION_PKIX_CERT,
                        CannedAccessControlList.PublicRead);
            }
            if (!extKey.equals("")){
                if (!Objects.equals(extKey, "key") )
                    throw new Exception("The format: " + FilenameUtils.getExtension(fileKey.getOriginalFilename()) +" is not supported");

                keyKey = "Credentials/" + data.getFcUser() + "/" +timeStamp +"/" + fileKey.getOriginalFilename();
                System.out.println("if key");
                S3Utils.uploadFileToS3(bucket,
                        keyKey,
                        fileKey.getInputStream(),
                        ContentTypeEnum.APPLICATION_VND_KEYNOTE,
                        CannedAccessControlList.PublicRead);
            }
            System.out.println(bucket);
            System.out.println(keyCer);
            System.out.println(keyKey);
            Long existUser = dao.validateUser(data.getFcUser());
            if (existUser == 0){
                throw new Exception("No user exists or is not active");
            }
            Certificate cer = dao.getdetail(existUser);
            boolean statusRegister = dao.saveCertificate(data.getFcUser(),
                    data.getPassword(),
                    bucket,
                    keyCer,
                    keyKey,
                    cer);
            System.out.println(statusRegister);
            if (statusRegister){
                String sqsValidateCerts = EnvironmentData.getPropertyValue("SQSValidateCerts");

                byte[] bytesCer = IOUtils.toByteArray(fileCer.getInputStream());
                byte[] bytesKey = IOUtils.toByteArray(fileKey.getInputStream());
                dataSQS.setIdTrader(existUser);

                dataSQS.setBase64Cer(Base64.getEncoder().encodeToString(bytesCer));
                dataSQS.setBase64Key(Base64.getEncoder().encodeToString(bytesKey));

                sendSQS(new Gson().toJson(dataSQS),sqsValidateCerts);
                message.setMessage("Data saved successfully");
                System.out.println(message);
                response = ResponseUtils.createResponse(List.of(message.toJsonObject()),ResponseEnum.EXITO);
            }else {
                message.setMessage("An error occurred while saving");
                response = ResponseUtils.createResponse(List.of(message.toJsonObject()),ResponseEnum.ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EnrollmentServiceImpl.DBException - saveCredentials: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.ERROR, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel validateCertificate(Integer fcUser) {
        ResponseModel response;
        StatusCert result = new StatusCert();
        try {
            if (fcUser == null) {
                throw new Exception("The fc user is required");
            }
                ValidateCert info = dao.validateCertificate(fcUser);
                boolean status = info.getIdCert() != null;
                result.setStatus(status);
                result.setIdStatus(info.getStatus());
                response = ResponseUtils.createResponse(UGson.toJsonObjectList(result),ResponseEnum.EXITO);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EnrollmentServiceImpl.DBException - validateCertificate: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.ERROR, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel getTaxData(User data) {
        ResponseModel response;
        try {
            if (data.getFcUser() == null || data.getFcUser() < 0)
                throw new Exception("The fc user is required");
            PersonalInformation results = dao.getTaxData(data);
            response = ResponseUtils.createResponse(UGson.toJsonObjectList(results),ResponseEnum.EXITO);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EnrollmentServiceImpl.DBException - getDataPersonal: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.ERROR, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel getDetail(Long fcUser) {
        ResponseModel response;
        try {
            if (fcUser == null || fcUser < 0) {
                throw new Exception("The fc user is required");
            }
            Long existUser = dao.validateUser(fcUser);
            if (existUser != 0){
                CerResponse cer = dao.getdetailCertificate(existUser);
                response = ResponseUtils.createResponse(UGson.toJsonObjectList(cer),ResponseEnum.EXITO);
            }else{
                throw new Exception("User does not exist or is not active");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("CertificateServiceImpl.DBException - getDetail: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.ERROR, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel saveCertificateUrls(CertificateUrl data) {
        ResponseModel response;
        Message message = new Message();
        Map<String,Long> dataSQS = new HashMap<>();
        try {
            String sqsValidateCerts = EnvironmentData.getPropertyValue("SQSValidateCerts");
            String bucket = EnvironmentData.getPropertyValue("BucketName");
            Long existUser = dao.validateUser(data.getFcUser());
            Certificate cerInfo = dao.getdetail(existUser);
            boolean statusRegister = dao.saveCertificate(data.getFcUser(),
                    data.getPassword(),
                    bucket,
                    data.getCerUrl(),
                    data.getKeyUrl(),
                    cerInfo);
            if (statusRegister){
                dataSQS.put("idTrader",existUser);
                sendSQS(new Gson().toJson(dataSQS),sqsValidateCerts);
                message.setMessage("Data saved successfully");
                System.out.println(message);
                response = ResponseUtils.createResponse(List.of(message.toJsonObject()),ResponseEnum.EXITO);
            }else {
                message.setMessage("An error occurred while saving");
                response = ResponseUtils.createResponse(List.of(message.toJsonObject()),ResponseEnum.ERROR);
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("CertificateServiceImpl.DBException - saveCertificateUrls: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.ERROR, e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseModel saveCred(CertificateUser data) {
        SendSQS dataSQS = new SendSQS();
        System.out.println("SERVICE DATA " + data);
        ResponseModel response;
        Message message = new Message();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        date.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        String timeStamp = date.format(new Date());
        System.out.println("extCer: "+ data.getCerBase64());
        System.out.println("extKey: "+ data.getKeyBase64());

        try {
            /*if (!Objects.equals(extCer, "cer"))
                throw new Exception("The format: " + FilenameUtils.getExtension(fileCer.getOriginalFilename()) +" is not supported");
            if (!Objects.equals(extKey, "key") )
                throw new Exception("The format: " + FilenameUtils.getExtension(fileKey.getOriginalFilename()) +" is not supported");*/

            String bucket = EnvironmentData.getPropertyValue("BucketName");
            String keyCer = "Info";
            String keyKey = "Info";

            Long existUser = dao.validateUser(data.getFcUser());
            if (existUser == 0){
                throw new Exception("No user exists or is not active");
            }
            Certificate cer = dao.getdetail(existUser);
            boolean statusRegister = dao.saveCertificate(data.getFcUser(),
                    data.getPassword(),
                    bucket,
                    keyCer,
                    keyKey,
                    cer);
            System.out.println(statusRegister);
            if (statusRegister){
                String sqsValidateCerts = EnvironmentData.getPropertyValue("SQSValidateCerts");

                dataSQS.setIdTrader(existUser);

                dataSQS.setBase64Cer(data.getCerBase64());
                dataSQS.setBase64Key(data.getKeyBase64());

                sendSQS(new Gson().toJson(dataSQS),sqsValidateCerts);
                message.setMessage("Data saved successfully");
                System.out.println(message);
                response = ResponseUtils.createResponse(List.of(message.toJsonObject()),ResponseEnum.EXITO);
            }else {
                message.setMessage("An error occurred while saving");
                response = ResponseUtils.createResponse(List.of(message.toJsonObject()),ResponseEnum.ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EnrollmentServiceImpl.DBException - saveCredentials: " + e.getMessage());
            response = ResponseUtils.createResponseWithMessage(null, ResponseEnum.ERROR, e.getMessage());
        }
        return response;
    }

    public void validateUrlDocument(PersonalInformation data) throws Exception {
        if (data.getFcUser() == null || data.getFcUser() <= 0)
            throw new Exception("The fcUser is required");
        if (data.getIneUrl().isEmpty() || data.getIneUrl() == null)
            throw new Exception("The ineUrl is required");
        if (data.getCurpUrl().isEmpty() || data.getCurpUrl() == null)
            throw new Exception("The curpUrl is required");
        if (data.getBankStatementUrl().isEmpty() || data.getBankStatementUrl() == null)
            throw new Exception("The bankStatementUrl is required");
        if (data.getFiscalSituationUrl().isEmpty() || data.getFiscalSituationUrl() == null)
            throw new Exception("The fiscalSituationUrl is required");
    }

    public static void sendSQS(String data,String sqsName){

        System.out.println("Message sent to sqs");
        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        System.out.println("data: "+ data + " SQS " + sqsName);
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(sqsName)
                .withMessageBody(data)
                .withDelaySeconds(0);
        sqs.sendMessage(send_msg_request);
    }
}
