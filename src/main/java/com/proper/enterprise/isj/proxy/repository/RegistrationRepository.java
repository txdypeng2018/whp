package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;

import java.util.List;

/**
 * Created by think on 2016/9/4 0004.
 */
public interface RegistrationRepository extends MongoRepository<RegistrationDocument, String> {

    List<RegistrationDocument> findByCreateUserIdAndStatusCodeAndIsAppointment(String patientId, String statusCode, String isAppointment);

    RegistrationDocument findByNum(String num);

    List<RegistrationDocument> findRegistrationDocumentByCreateUserIdAndPatientIdCard(String createUserId,
            String patientIdCard);

}
