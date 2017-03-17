package com.proper.enterprise.isj.proxy.business.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FeedbackContext;
import com.proper.enterprise.isj.context.OpinionContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.StatusCodeContext;
import com.proper.enterprise.isj.context.UserNameContext;
import com.proper.enterprise.isj.context.UserTelContext;
import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class ServiceFeedbackOpinionBusiness<T, M extends UserNameContext<ServiceFeedbackDocument> & UserTelContext<ServiceFeedbackDocument> & StatusCodeContext<ServiceFeedbackDocument> & FeedbackContext<ServiceFeedbackDocument> & OpinionContext<ServiceFeedbackDocument> & PageNoContext<ServiceFeedbackDocument> & PageSizeContext<ServiceFeedbackDocument> & ModifiedResultBusinessContext<ServiceFeedbackDocument>>
        implements IBusiness<ServiceFeedbackDocument, M> {
    @Autowired
    ServiceService serviceService;

    @Override
    public void process(M ctx) throws Exception {
        String userName = ctx.getUserName();
        String userTel = ctx.getUserTel();
        String statusCode = ctx.getStatusCode();
        String opinion = ctx.getOpinion();
        String feedback = ctx.getFeedback();
        String pageNo = String.valueOf(ctx.getPageNo());
        String pageSize = String.valueOf(ctx.getPageSize());
        // 取得反馈意见列表
        ServiceFeedbackDocument feedbackInfo = serviceService.getFeedBackInfo(userName, userTel, statusCode, opinion,
                feedback, pageNo, pageSize);
        ctx.setResult(feedbackInfo);
    }
}
