package com.proper.enterprise.isj.log.service;

import java.util.Map;

import com.proper.enterprise.isj.log.document.WSLogDocument;
import com.proper.enterprise.platform.core.entity.DataTrunk;

public interface WSLogService {

    void persistLog(String methodName, Map<String, Object> param, String req, Object obj, long duration);

    DataTrunk<WSLogDocument> getWsLogList(int pageNo, int pageSize, String search, String startDate, String endDate,
            String methodName) throws Exception;

}