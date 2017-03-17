package com.proper.enterprise.isj.function.message;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;

@Service
public class SaveSingleDetailMessageFunction implements IFunction<Object>, ILoggable{
    
    @Autowired
    FunctionToolkit toolkit;
    
    @Override
    public Object execute(Object... params) throws Exception {
        sendRecipeMsg((RecipeOrderDocument)params[0], (SendPushMsgEnum)params[1], params[2]);
        return null;
    }
    
    
    public void sendRecipeMsg(RecipeOrderDocument recipeInfo, SendPushMsgEnum pushMsgType, Object pushObj)
            throws Exception {
            MessagesDocument regMsg = new MessagesDocument();
            regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushMsgType, pushObj));
            regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
            regMsg.setUserId(recipeInfo.getCreateUserId());
            regMsg.setUserName(recipeInfo.getOperatorPhone());
            debug(JSONUtil.toJSON(regMsg));
            toolkit.executeFunction(SaveSingleMessageFunction.class, regMsg);
    }

}
