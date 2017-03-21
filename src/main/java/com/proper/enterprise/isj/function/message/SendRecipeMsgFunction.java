package com.proper.enterprise.isj.function.message;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;

/**
 * old:com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.sendRecipeMsg(RecipeOrderDocument,
 * SendPushMsgEnum, Object)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */

@Service
public class SendRecipeMsgFunction implements IFunction<Object>, ILoggable {

    @Autowired
    MessagesService messagesService;

    @Override
    public Object execute(Object... params) throws Exception {
        this.sendRecipeMsg((RecipeOrderDocument) params[0], (SendPushMsgEnum) params[1], (Object) params[2]);
        return null;
    }

    /**
     * 推送挂号相关信息
     *
     * @param recipeInfo 缴费信息.
     * @param pushMsgType 推送消息类别.
     * @param pushObj 推送消息对象.
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private void sendRecipeMsg(RecipeOrderDocument recipeInfo, SendPushMsgEnum pushMsgType, Object pushObj)
            throws Exception {
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushMsgType, pushObj));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(recipeInfo.getCreateUserId());
        regMsg.setUserName(recipeInfo.getOperatorPhone());
        debug(JSONUtil.toJSON(regMsg));
        messagesService.saveMessage(regMsg);
    }

}
