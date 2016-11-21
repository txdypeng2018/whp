package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.ScheduleMistakeLogDocument;

import java.util.List;

/**
 * Created by think on 2016/10/25 0025.
 */
public interface ScheduleMistakeLogRepository extends MongoRepository<ScheduleMistakeLogDocument, String> {

    List<ScheduleMistakeLogDocument> findByDoctorIdAndMistakeCode(String doctorId, String mistakeCode);
}
