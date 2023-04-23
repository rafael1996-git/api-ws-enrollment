package com.trader.api.dao.impl;

import com.trader.api.dao.EnrollmentDao;
import com.trader.api.domain.request.PersonalInformation;
import com.trader.api.domain.responce.CerResponse;
import com.trader.api.domain.responce.ListBank;
import com.trader.api.models.*;
import com.trader.api.security.Credentials;
import com.trader.api.utils.Constantes;
import com.trader.api.utils.ConstantesSQL;
import com.trader.api.utils.SecurityUtils;
import com.trader.core.db.oracle.OracleDBPool;
import com.trader.core.security.TraderEncriptorKey;
import com.trader.core.utils.GsonParserUtils;
import oracle.jdbc.OracleTypes;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EnrollmentDaoImpl implements EnrollmentDao {
    @Override
    public ListBank getinfoBank(String keyBank) throws Exception {

        List<Bank> list = new ArrayList();
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNSEARCHBANKBYINITKEY).prepareCall(ConstantesSQL.FNSEARCHBANKBYINITKEY)){
            statement.registerOutParameter(1, OracleTypes.CURSOR);
            statement.setString(2,keyBank);
            statement.execute();
            ResultSet resultSet = (ResultSet) statement.getObject(1);
            while (resultSet.next()){
                list.add(new Bank(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                ));
            }
            System.out.println(GsonParserUtils.getGson().toJson(list));
            statement.close();
            return new ListBank(list);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EnrollmentDaoImpl.Exception - getinfoBank: " + e.getMessage());
            throw new Exception(e.toString());
        }
    }

    @Override
    public boolean savePersonalInformation(PersonalInformation data) throws Exception {
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNINSERTPERSONALINFO).prepareCall(ConstantesSQL.FNINSERTPERSONALINFO)){
            statement.registerOutParameter(1,OracleTypes.NUMBER);
            statement.setInt(2,data.getFcUser());
            statement.setString(3,data.getCurp());
            statement.setString(4,data.getRfc());
            statement.setString(5,data.getTaxResidence());
            statement.setString(6,data.getTaxRegime());
            statement.setString(7,data.getBankKey());
            statement.setInt(8,data.getIdBank());
            statement.executeUpdate();
            System.out.println("STATUS DE REGISTRO INF:: " + statement.getInt(1));
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("EnrollmentDaoImpl.Exception - savePersonalInformation: " + e.getMessage());
            throw new Exception(e.toString());
        }
    }

    @Override
    public boolean savePersonalDocuments(PersonalInformation data) throws Exception {
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNINSERTPERSONALDOC).prepareCall(ConstantesSQL.FNINSERTPERSONALDOC)){
            statement.registerOutParameter(1,OracleTypes.NUMBER);
            statement.setInt(2,data.getFcUser());
            statement.setString(3,data.getIneUrl());
            statement.setString(4,data.getCurpUrl());
            statement.setString(5,data.getBankStatementUrl());
            statement.setString(6,data.getFiscalSituationUrl());
            statement.executeUpdate();
            System.out.println("STATUS DE REGISTRO DOC " + statement.getInt(1));
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("EnrollmentDaoImpl.Exception - savePersonalDocuments: " + e.getMessage());
            throw new Exception(e.toString());
        }
    }


    @Override
    public ValidateCert validateCertificate(Integer fcUser) throws Exception {
        System.out.println("DAO");
        ValidateCert info = new ValidateCert();
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNVALIDATECERTIFICATE).prepareCall(ConstantesSQL.FNVALIDATECERTIFICATE)){
            statement.registerOutParameter(1, OracleTypes.CURSOR);
            statement.setLong(2,fcUser);
            statement.execute();
            System.out.println("aqui si llego");
            ResultSet rs = (ResultSet) statement.getObject(1);
            if (rs.next()) {
                info.setIdCert(rs.getInt(1));
                info.setStatus(rs.getInt(2));
            }
            System.out.println(info);
            return info;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(" EnrollmentDaoImpl.Exception - validateCertificate: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean saveCertificate(Long idUser, String password, String bucket, String urlCer, String urlKey, Certificate cerInfo) throws Exception {
        System.out.println("DAO");
        System.out.println(idUser + " " + password + " " + bucket + " " + urlCer  + " " + urlKey);
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNINSERTCERTIFICATE).prepareCall(ConstantesSQL.FNINSERTCERTIFICATE)){
            statement.registerOutParameter(1,OracleTypes.NUMBER);
            statement.setLong(2,idUser);
            if (urlCer == null || urlCer.isEmpty()){
                statement.setString(3,cerInfo.getCer());
            }else{
                statement.setString(3,urlCer);
            }
            if (urlKey == null || urlKey.isEmpty()){
                statement.setString(4,cerInfo.getKey());
            }else{
                statement.setString(4,urlKey);
            }
            statement.setString(5, TraderEncriptorKey.encode(password));
            statement.setString(6,bucket);
            statement.executeUpdate();
            System.out.println("STATUS REGISTRO: " + statement.getInt(1));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(" EnrollmentDaoImpl.Exception - saveCertificate: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Certificate getdetail(Long idUser) throws Exception {

        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNGETCERTIFICATE)
                .prepareCall(ConstantesSQL.FNGETCERTIFICATE)){
            statement.registerOutParameter(1, OracleTypes.CURSOR);
            statement.setLong(2,idUser);
            statement.execute();
            ResultSet rs = (ResultSet) statement.getObject(1);
            Certificate cer = new Certificate();
            if(rs.next()){
                cer.setId(rs.getLong(1));
                cer.setKey(rs.getString(2));
                cer.setCer(rs.getString(3));
                cer.setPassword(rs.getString(4));
            }
            return cer;
        }catch (Exception e){
            System.out.println(" CertificateDaoImpl.Exception - getdetail: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public PersonalInformation getTaxData(User data) throws Exception {
        PersonalInformation taxData = new PersonalInformation();
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNGETTAXDATA).prepareCall(ConstantesSQL.FNGETTAXDATA)){
            statement.registerOutParameter(1,OracleTypes.CURSOR);
            statement.setLong(2, data.getFcUser());
            statement.execute();
            ResultSet resultSet = (ResultSet) statement.getObject(1);

            if(resultSet.next()){
                switch (data.getTypeData()){
                    case 1:
                        // PERSONAL INFORMATION
                        System.out.println("DATA TYPE: " + data.getTypeData());
                        taxData.setCurp(resultSet.getString(1));
                        taxData.setRfc(resultSet.getString(2));
                        taxData.setTaxRegime(resultSet.getString(3));
                        taxData.setTaxResidence(resultSet.getString(4));
                        taxData.setBankKey(resultSet.getString(5));
                        taxData.setNameBank(resultSet.getString(6));
                        taxData.setIdBank(resultSet.getInt(7));
                        break;
                    case 2:
                        // PERSONAL DOCUMENTS
                        System.out.println("DATA TYPE: " + data.getTypeData());
                        taxData.setIneUrl(resultSet.getString(8));
                        taxData.setCurpUrl(resultSet.getString(9));
                        taxData.setBankStatementUrl(resultSet.getString(10));
                        taxData.setFiscalSituationUrl(resultSet.getString(11));
                        break;
                    default:
                        throw new Exception("The typeData does not exist in the catalog: " + data.getTypeData());
                }
            }
            System.out.println("RESULTS BD: " + taxData);
            return taxData;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println(" EnrollmentDaoImpl.Exception - getDataPersonal: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Long validateUser(Long fcUser) throws Exception {
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNVALIDATEUSEREXIST)
                .prepareCall(ConstantesSQL.FNVALIDATEUSEREXIST)){
            statement.registerOutParameter(1, OracleTypes.NUMBER);
            statement.setLong(2,fcUser);
            statement.execute();
            System.out.println("ID USER: " + statement.getLong(1));
            return statement.getLong(1);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println(" UserDaoImpl.Exception - validateUser: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public CerResponse getdetailCertificate(Long idUser) throws Exception {
        CerResponse cr = new CerResponse();
        boolean key = true;
        boolean cer = true;
        try (CallableStatement statement = OracleDBPool.getSingletonConnection(ConstantesSQL.DB_TIMEOUT,ConstantesSQL.FNGETCERTIFICATE)
                .prepareCall(ConstantesSQL.FNGETCERTIFICATE)){
            statement.registerOutParameter(1, OracleTypes.CURSOR);
            statement.setLong(2,idUser);
            statement.execute();
            ResultSet rs = (ResultSet) statement.getObject(1);
            if(rs.next()){
                if (rs.getString(2) == null){
                    key = false;
                }
                if (rs.getString(3) == null){
                    cer = false;
                }
                cr.setCertificate(new CertificateDetail(
                        rs.getLong(1),
                        key,
                        cer,
                        SecurityUtils.decryptText(rs.getString(4))
                ));
            }
            return cr;
        }catch (Exception e){
            System.out.println(" CertificateDaoImpl.Exception - getdetail: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    static {
        try {
            if (Credentials.DB_CONFIG == null) {
                throw new com.trader.api.utils.Exceptions.TradeException(
                        Constantes.FAILED_GET_DB_CONFIG,
                        Constantes.CODIGO_FAILED_GET_DB_CONFIG
                );
            }
            OracleDBPool.initSingletonConnectionCredentials(
                    Credentials.DB_CONFIG.getUrl(),
                    Credentials.DB_CONFIG.getUser(),
                    Credentials.DB_CONFIG.getPass()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
