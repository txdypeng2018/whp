package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ServiceUserOpinionDocumentContext;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.enums.FeedbackEnum;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.isj.push.PushService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveFeedbackOpinionBusiness<M extends ServiceUserOpinionDocumentContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {
    @Autowired
    ServiceService serviceService;

    @Autowired
    PushService pushService;

    @Override
    public void process(M ctx) throws Exception {
        ServiceUserOpinionDocument opinionDocment = ctx.getOpinionDocment();
        String retValue = "";
        if (StringUtil.isNotNull(opinionDocment.getId())) {
            debug("request_id:" + opinionDocment.getId());
            debug("request_feedback:" + opinionDocment.getFeedback());
            opinionDocment.setFeedbackTime(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
            opinionDocment.setStatus(CenterFunctionUtils.REPLAYED);
            opinionDocment.setStatusCode(FeedbackEnum.REPLAYED.getValue());
            serviceService.saveOpinion(opinionDocment);

            List<String> userNameList = new ArrayList<>();
            userNameList.add(opinionDocment.getUserTel());
            String pushContent = "您有一条意见反馈消息!";
            String pushType = "feedback";
            List<Map<String, String>> paramMapList = new ArrayList<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("opinionId", opinionDocment.getId());
            paramMapList.add(paramMap);
            pushService.pushInfo(pushContent, pushType, userNameList, paramMapList);
            debug("推送反馈意见信息!");
            debug("反馈意见ID:" + opinionDocment.getId());
        }
        ctx.setResult(retValue);
    }
}
