package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FeedbackStatusContext;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class GetUserOpinionBusiness<M extends FeedbackStatusContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Autowired
    ServiceService serviceService;

    @Autowired
    UserService userService;

    @Override
    public void process(M ctx) throws Exception {
        String feedbackStatus = ctx.getFeedbackStatus();
        boolean isStatus = !StringUtil.isNull(feedbackStatus);
        User currentUser = userService.getCurrentUser();
        String currentUserId = currentUser.getId();
        List<ServiceUserOpinionDocument> opinionList;
        if (isStatus) {
            opinionList = serviceService.getByUserIdAndFeedbackStatus(currentUserId, feedbackStatus);
        } else {
            opinionList = serviceService.getByUserId(currentUserId);
        }
        ctx.setResult(opinionList);
    }
}
