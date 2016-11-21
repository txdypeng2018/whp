package com.proper.enterprise.isj.proxy.service;


import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;

import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;

import java.util.List;

public interface ServiceService {

    String getPhoneNum();

    void saveBaseInfo(BaseInfoEntity baseInfo);

    void saveOpinion(ServiceUserOpinionDocument opinionDocument);

    List<ServiceUserOpinionDocument> getAll();

    List<ServiceUserOpinionDocument> findByFeedbackStatus(String feedbackStatus);

    List<ServiceUserOpinionDocument> getByUserId(String userId);

    List<ServiceUserOpinionDocument> getByUserIdAndFeedbackStatus(String userId, String feedbackStatus);

    ServiceUserOpinionDocument getById(String id);

    ServiceFeedbackDocument getFeedBackInfo(String userName, String userTel, String statusCode,
                   String opinion, String feedback, String pageNo,  String pageSize) throws Exception;
}
