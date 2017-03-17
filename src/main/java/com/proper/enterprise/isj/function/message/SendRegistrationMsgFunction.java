package com.proper.enterprise.isj.function.message;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.sendRegistrationMsg(SendPushMsgEnum,
 * RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SendRegistrationMsgFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Override
    public Object execute(Object... params) throws Exception {
        RegistrationDocument doc;
        if (params[1] instanceof RegistrationDocument) {
            doc = (RegistrationDocument) params[1];
        } else {
            doc = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                    String.valueOf(params[1]));
        }
        sendRegistrationMsg((SendPushMsgEnum) params[0], doc);
        return null;
    }

    /**
     * 发送挂号推送消息.
     *
     * @param pushType 推送消息类别.
     * @param updateReg 推送消息对象.
     * @throws Exception 异常.
     */
    public void sendRegistrationMsg(SendPushMsgEnum pushType, RegistrationDocument updateReg) throws Exception {
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushType, updateReg));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(updateReg.getCreateUserId());
        regMsg.setUserName(updateReg.getOperatorPhone());
        toolkitx.executeFunction(SaveSingleMessageFunction.class, regMsg);
    }

}
