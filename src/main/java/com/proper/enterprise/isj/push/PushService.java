package com.proper.enterprise.isj.push;

import java.util.List;
import java.util.Map;

/**
 * 推送Service
 */
public interface PushService {

    void pushInfo(String pushContent, String pushType, List<String> userNameList, List<Map<String, String>> paramList) throws Exception;

}
