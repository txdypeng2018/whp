package com.proper.enterprise.isj.function.message;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.mobile.pushtools.PushMessage;
import com.proper.mobile.pushtools.PusherApp;

@Service
public class PushInfoFunction implements IFunction<Object> {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        pushInfo((String) params[0], (String) params[1], (List<String>) params[2],
                (List<Map<String, String>>) params[3]);
        return null;
    }

    public void pushInfo(String pushContent, String pushType, List<String> userNameList,
            List<Map<String, String>> paramList) throws Exception {

        // 获取推送相关参数
        String appkey = ConfCenter.get("isj.push.properpushAppkey");
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
        for (Map<String, String> pushMap : paramList) {
            Iterator<Map.Entry<String, String>> paraIter = pushMap.entrySet().iterator();
            while (paraIter.hasNext()) {
                Map.Entry<String, String> entry = paraIter.next();
                paramKey = entry.getKey();
                paramValue = entry.getValue();
                msg.addCustomData(paramKey, paramValue);
            }
        }
        // 推送消息
        app.pushMessageToUsers(msg, userNameList);
    }

}
