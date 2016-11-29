package com.proper.enterprise.isj.pay.cmb.repository;

import com.proper.enterprise.isj.pay.cmb.document.CmbProtocolDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 招商银行用户协议Repository
 */
public interface CmbProtocolRepository extends MongoRepository<CmbProtocolDocument, String> {

    CmbProtocolDocument findByUserId(String userId);

}
