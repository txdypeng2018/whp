package com.proper.enterprise.isj.proxy.repository;

import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceUserOpinionRepository extends MongoRepository<ServiceUserOpinionDocument, String> {

    List<ServiceUserOpinionDocument> findAll();

    List<ServiceUserOpinionDocument> findByStatusCodeOrderByOpinionTimeDescCreateTimeDesc(String feedbackStatus);

    List<ServiceUserOpinionDocument> findByUserIdOrderByOpinionTimeDescCreateTimeDesc(String userId);

    List<ServiceUserOpinionDocument> findByUserIdAndStatusCodeOrderByOpinionTimeDescCreateTimeDesc(String userId, String feedbackStatus);

    ServiceUserOpinionDocument findById(String id);
}
