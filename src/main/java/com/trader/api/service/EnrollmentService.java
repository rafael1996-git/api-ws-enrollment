package com.trader.api.service;

import com.trader.api.domain.request.CertificateUser;
import com.trader.api.domain.request.CertificateUrl;
import com.trader.api.domain.request.PersonalInformation;
import com.trader.api.models.User;
import com.trader.core.models.ResponseModel;
import org.springframework.web.multipart.MultipartFile;

public interface EnrollmentService {
    ResponseModel searchInfoBank(String bankKey);
    ResponseModel savePersonalInformation(PersonalInformation data);
    ResponseModel savePersonalDocuments(PersonalInformation data);
    ResponseModel saveCredentials(MultipartFile fileCer, MultipartFile fileKey, User data);
    ResponseModel validateCertificate(Integer fcUser);
    ResponseModel getTaxData(User data);
    ResponseModel getDetail(Long fcUser);
    ResponseModel saveCertificateUrls(CertificateUrl data);
    ResponseModel saveCred(CertificateUser data);
}
