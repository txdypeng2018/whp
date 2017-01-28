package com.proper.enterprise.isj.log.repository;

import com.proper.enterprise.isj.log.document.WSLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WSLogRepository extends MongoRepository<WSLogDocument, String> {

}
