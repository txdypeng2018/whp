package com.proper.enterprise.isj.proxy.repository;

import com.proper.enterprise.isj.proxy.document.ServicePhoneDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServicePhoneRepository extends MongoRepository<ServicePhoneDocument, String> {

    ServicePhoneDocument findByPhoneType(String phoneType);
}
