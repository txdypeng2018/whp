package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;

import java.util.List;

/**
 * Created by think on 2016/11/9 0009.
 */
public interface RegistrationRefundLogRepository extends MongoRepository<RegistrationRefundLogDocument, String> {

    List<RegistrationRefundLogDocument> findByNum(String num);
}
