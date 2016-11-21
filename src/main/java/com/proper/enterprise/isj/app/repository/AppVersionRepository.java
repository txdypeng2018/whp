package com.proper.enterprise.isj.app.repository;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppVersionRepository extends MongoRepository<AppVersionDocument, String> {

    AppVersionDocument findTopByOrderByVerDesc();

    AppVersionDocument findByVer(int version);

}
