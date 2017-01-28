package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.ScheduleMistakeLogDocument;

import java.util.List;

public interface ScheduleMistakeLogRepository extends MongoRepository<ScheduleMistakeLogDocument, String> {

    List<ScheduleMistakeLogDocument> findByDoctorIdAndMistakeCode(String doctorId, String mistakeCode);
}
