package com.trader.api.controller;

import com.trader.api.domain.request.CertificateUser;
import com.trader.api.domain.request.CertificateUrl;
import com.trader.api.domain.request.PersonalInformation;
import com.trader.api.models.User;
import com.trader.api.service.EnrollmentService;
import com.trader.api.utils.SecurityUtils;
import com.trader.core.controllers.TraderBaseController;
import com.trader.core.domain.request.Request;
import com.trader.core.utils.ConstantsUtils;
import com.trader.core.utils.JsonConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;

@CrossOrigin(origins = "*", methods = {RequestMethod.POST})
@EnableWebMvc
@RestController
@RequestMapping("/enrollment")
public class MainController extends TraderBaseController {
    @Autowired
    private EnrollmentService service;

    @PostMapping(value = "/get-info-bank",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getinfoBank(@RequestBody final Request request){
        System.out.println(request);
        Object object = SecurityUtils.decrypt(request.getData(), PersonalInformation.class);

        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final PersonalInformation data = (PersonalInformation) object;
        return SecurityUtils.parseResponse(service.searchInfoBank(data.getBankKey()),false);
    }

    @PostMapping(value = "/save-personal-information",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object savePersonalInformation(@RequestBody final Request request){
        Object object = SecurityUtils.decrypt(request.getData(), PersonalInformation.class);

        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final PersonalInformation data = (PersonalInformation) object;
        System.out.println(data);
        return SecurityUtils.parseResponse(service.savePersonalInformation(data),false);
    }

    @PostMapping(value = "/save-personal-document",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object savePersonalDocument(@RequestBody final Request request){
        Object object = SecurityUtils.decrypt(request.getData(), PersonalInformation.class);

        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final PersonalInformation data = (PersonalInformation) object;
        return SecurityUtils.parseResponse(service.savePersonalDocuments(data),false);
    }

    @PostMapping(value = "/save-certificate",consumes = "multipart/form-data")
    public Object saveCredentials(@RequestPart(value = "cer") MultipartFile cer,
                                  @RequestPart(value = "key") MultipartFile key,
                                  @RequestPart(value = "data") String data){
        Object object = SecurityUtils.decrypt(data, User.class);
        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        User user = (User) object;
        return SecurityUtils.parseResponse(service.saveCredentials(cer,key, user),false);
    }


    @PostMapping(value = "/save-cert",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object saveCredentials(@RequestBody final Request request){
        Object object = SecurityUtils.decrypt(request.getData(), CertificateUser.class);
        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final CertificateUser data = (CertificateUser) object;
        return SecurityUtils.parseResponse(service.saveCred(data),false);
    }

    @PostMapping(value = "/validate-certificate",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object validateCertificate(@RequestBody final Request request){
        Object object = SecurityUtils.decrypt(request.getData(), PersonalInformation.class);

        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final PersonalInformation data = (PersonalInformation) object;
        return SecurityUtils.parseResponse(service.validateCertificate(data.getFcUser()),false);
    }

    @PostMapping(value = "/get-tax-data",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getTaxData(@RequestBody final Request request){

        Object object = SecurityUtils.decrypt(request.getData(), User.class);

        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final User data = (User) object;
        return SecurityUtils.parseResponse(service.getTaxData(data),false);
    }

    @PostMapping(value = "/detail-certificate",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getDetail(@RequestBody final Request request){
        Object object = SecurityUtils.decrypt(request.getData(), User.class);

        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        final User data = (User) object;
        return SecurityUtils.parseResponse(service.getDetail(data.getFcUser()),false);
    }

    @PostMapping(value = "/save-files-sat",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object saveCredentialURLs(@RequestBody final Request request){
        Object object = SecurityUtils.decrypt(request.getData(), CertificateUrl.class);
        if (object == null) {
            object = errorParsingRequest();
        }
        if (object instanceof HashMap) {
            return object;
        }
        CertificateUrl data = (CertificateUrl) object;
        System.out.println(data);
        return SecurityUtils.parseResponse(service.saveCertificateUrls(data),false);
    }

    @Override
    @RequestMapping(ConstantsUtils.ENDPOINT_HEALTH_CHECK)
    public HashMap healthCheck() {
        return getStatus(true);
    }

    @Override
    public HashMap getStatus(boolean withDB) {
        HashMap map = new HashMap();
        map.put(JsonConstantsUtils.PROPERTY_NAME_SUCCESS, Boolean.TRUE);
        map.put(JsonConstantsUtils.PROPERTY_NAME_MESSAGE, ConstantsUtils.HEALTH_CHECK_OK);
        return map;
    }
}
