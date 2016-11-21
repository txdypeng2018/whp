package com.proper.enterprise.isj.webservices.repository;

import com.proper.enterprise.isj.webservices.document.WSLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WSLogRepository extends MongoRepository<WSLogDocument, String> {

}
