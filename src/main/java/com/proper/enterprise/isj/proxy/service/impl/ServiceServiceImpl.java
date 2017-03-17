package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BaseInfoEntityContext;
import com.proper.enterprise.isj.context.FeedbackContext;
import com.proper.enterprise.isj.context.FeedbackStatusContext;
import com.proper.enterprise.isj.context.OpinionContext;
import com.proper.enterprise.isj.context.OpinionIdContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.ServiceUserOpinionDocumentContext;
import com.proper.enterprise.isj.context.StatusCodeContext;
import com.proper.enterprise.isj.context.UserIdContext;
import com.proper.enterprise.isj.context.UserNameContext;
import com.proper.enterprise.isj.context.UserTelContext;
import com.proper.enterprise.isj.function.opinion.FindByUserIdAndFeedbackStatusFunction;
import com.proper.enterprise.isj.proxy.business.customerservice.FetchFeedBackInfoBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.FetchServicePhoneNumBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.SaveBaseinfoBusiness;
import com.proper.enterprise.isj.proxy.business.opinion.FetchAllOpinionsBusiness;
import com.proper.enterprise.isj.proxy.business.opinion.FetchByIdBusiness;
import com.proper.enterprise.isj.proxy.business.opinion.FetchByUserIdBusiness;
import com.proper.enterprise.isj.proxy.business.opinion.FindUserOpinionByFeedbackStatusBusiness;
import com.proper.enterprise.isj.proxy.business.opinion.SaveOpinionBusiness;
import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.isj.support.service.AbstractService;

@Service
public class ServiceServiceImpl extends AbstractService implements ServiceService {


    @Override
    public String getPhoneNum() {
        return toolkit.execute(FetchServicePhoneNumBusiness.class, (c) -> {
        });
    }

    @Override
    public void saveBaseInfo(BaseInfoEntity baseInfo) {
        toolkit.execute(SaveBaseinfoBusiness.class, (c) -> {
            ((BaseInfoEntityContext<?>) c).setBasicInfoEntity(baseInfo);
        });
    }

    @Override
    public void saveOpinion(ServiceUserOpinionDocument opinionDocument) {
        toolkit.execute(SaveOpinionBusiness.class, (c) -> {
            ((ServiceUserOpinionDocumentContext<?>) c).setOpinionDocment(opinionDocument);
        });
    }

    @Override
    public List<ServiceUserOpinionDocument> getAll() {
        return toolkit.execute(FetchAllOpinionsBusiness.class, (c) -> {
        });
    }

    @Override
    public List<ServiceUserOpinionDocument> findByFeedbackStatus(String feedbackStatus) {
        return toolkit.execute(FindUserOpinionByFeedbackStatusBusiness.class, (c) -> {
            ((FeedbackStatusContext<?>) c).setFeedbackStatus(feedbackStatus);
        });
    }

    @Override
    public List<ServiceUserOpinionDocument> getByUserId(String userId) {
        return toolkit.execute(FetchByUserIdBusiness.class, (c) -> {
            ((UserIdContext<?>) c).setUserId(userId);
        });
    }

    @Override
    public List<ServiceUserOpinionDocument> getByUserIdAndFeedbackStatus(String userId, String feedbackStatus) {
        return toolkit.execute(FindByUserIdAndFeedbackStatusFunction.class, (c) -> {
            ((UserIdContext<?>) c).setUserId(userId);
            ((FeedbackStatusContext<?>) c).setFeedbackStatus(feedbackStatus);
        });
    }

    @Override
    public ServiceUserOpinionDocument getById(String id) {
        return toolkit.execute(FetchByIdBusiness.class, (c) -> {
            ((OpinionIdContext<?>) c).setOpinionId(id);
        });
    }

    @Override
    public ServiceFeedbackDocument getFeedBackInfo(String userName, String userTel, String statusCode, String opinion,
            String feedback, String pageNo, String pageSize) throws Exception {
        
        return toolkit.execute(FetchFeedBackInfoBusiness.class, (c) -> {
            ((UserNameContext<?>) c).setUserName(userName);
            ((UserTelContext<?>) c).setUserTel(userTel);
            ((StatusCodeContext<?>) c).setStatusCode(statusCode);
            ((OpinionContext<?>) c).setOpinion(opinion);
            ((FeedbackContext<?>) c).setFeedback(feedback);
            ((PageNoContext<?>) c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>) c).setPageSize(Integer.parseInt(pageSize));
        });
        
    }
}
