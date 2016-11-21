package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;

import java.util.List;

/**
 * Created by think on 2016/10/7 0007.
 */
public interface StopRegRecordRepository extends MongoRepository<StopRegRecordDocument, String> {

    StopRegRecordDocument getByStopReg(String stopReg);

    List<StopRegRecordDocument> findBystopDateAfter(String stopDate);

}
