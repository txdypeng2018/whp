package com.proper.enterprise.isj.push.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.message.PushInfoFunction;
import com.proper.enterprise.isj.push.PushService;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.mobile.pushtools.PushMessage;
import com.proper.mobile.pushtools.PusherApp;

/**
 * 推送ServiceImpl
 */
@Service
public class PushServiceImpl extends AbstractService implements PushService, ILoggable {

    /**
     * 推送反馈意见消息
     *
     * @param pushContent
     *        推送消息内容
     * @param pushType
     *        推送消息类别
     * @param userNameList
     *        推送人列表(手机号)
     * @param paramList
     *        推送参数列表
     * @throws Exception
     */
    @Override
    public void pushInfo(String pushContent, String pushType, List<String> userNameList, List<Map<String, String>> paramList) throws Exception {
        debug("Push info: {}, {}, {}, {}", pushContent, pushType, userNameList, paramList);

        // 获取推送相关参数
        String appkey= ConfCenter.get("isj.push.properpushAppkey");
        String secureKey = ConfCenter.get("isj.push.properpushSecert");
        String pushUrl = ConfCenter.get("isj.push.pushUrl");
        PusherApp app = new PusherApp(pushUrl, appkey, secureKey);
        app.setAsync(true);
        // 拼接推送消息
        PushMessage msg = new PushMessage();
        msg.setTitle("掌上盛京医院");
        msg.setContent(pushContent);
        msg.addCustomData("pageUrl", pushType);
        // 推送参数
        String paramKey = null;
        String paramValue = null;
        for(Map<String, String> pushMap : paramList) {
            Iterator<Map.Entry<String, String>> paraIter = pushMap.entrySet().iterator();
            while(paraIter.hasNext()) {
                Map.Entry<String, String> entry = paraIter.next();
                paramKey = entry.getKey();
                paramValue = entry.getValue();
                msg.addCustomData(paramKey, paramValue);
            }
        }

        debug("Invoke PusherAPP to push {} to {}", msg, userNameList);
        // 推送消息
        app.pushMessageToUsers(msg, userNameList);



        toolkit.executeFunction(PushInfoFunction.class, pushContent, pushType, userNameList, paramList);


    }
}
