package com.proper.enterprise.isj.proxy.repository;


import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 消息
 */
public interface MessagesRepository extends MongoRepository<MessagesDocument, String> {
    List<MessagesDocument> findByUserIdOrderByCreateTimeDesc(String userId);
}
