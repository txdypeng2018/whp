package com.proper.enterprise.isj.user.repository;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by think on 2016/8/12 0012.
 *
 */
public interface UserInfoRepository extends MongoRepository<UserInfoDocument, String> {

    UserInfoDocument getByUserId(String userId);

    UserInfoDocument getByPhone(String phone);

    List<UserInfoDocument> findByNameAndMedicalNumAndPhone(String name, String medicalNum, String phone);

    List<UserInfoDocument> findByNameAndMedicalNumAndIdCard(String name, String medicalNum, String idCard);
}
