package com.trader.api.dao;

import com.trader.api.domain.request.PersonalInformation;
import com.trader.api.domain.responce.CerResponse;
import com.trader.api.domain.responce.ListBank;
import com.trader.api.models.Certificate;
import com.trader.api.models.User;
import com.trader.api.models.ValidateCert;

public interface EnrollmentDao {
    ListBank getinfoBank(String keyBank) throws Exception;
    boolean savePersonalInformation(PersonalInformation data) throws Exception;
    boolean savePersonalDocuments(PersonalInformation data) throws Exception;
    ValidateCert validateCertificate(Integer fcUser) throws Exception;
    boolean saveCertificate(Long idUser,String password,String bucket,String urlCer,String urlKey, Certificate cer) throws Exception;
    Certificate getdetail(Long idUser) throws Exception;
    PersonalInformation getTaxData(User data) throws Exception;
    Long validateUser(Long fcUser) throws Exception;
    CerResponse getdetailCertificate(Long idUser) throws Exception;
}
