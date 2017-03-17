package com.proper.enterprise.isj.function.opinion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.repository.ServiceUserOpinionRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class FindByUserIdAndFeedbackStatusFunction implements IFunction<List<ServiceUserOpinionDocument>> {

    @Autowired
    ServiceUserOpinionRepository opinionRepo;

    @Override
    public List<ServiceUserOpinionDocument> execute(Object... params) throws Exception {
        return getByUserIdAndFeedbackStatus((String)params[0], (String)params[1]);
    }

    public List<ServiceUserOpinionDocument> getByUserIdAndFeedbackStatus(String userId, String feedbackStatus) {
        return opinionRepo.findByUserIdAndStatusCodeOrderByOpinionTimeDescCreateTimeDesc(userId, feedbackStatus);
    }

}