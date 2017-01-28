package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;

import java.util.List;

/**
 * 终止挂号记录服务.
 * Created by think on 2016/10/7 0007.
 */
public interface StopRegRecordService {

    void saveStopRegRecord(StopRegRecordDocument record);

    /**
     * 查询终止挂号记录.
     * @param stopDate 日期.
     * @return 结果.
     */
    List<StopRegRecordDocument> findStopRegRecordDocument(String stopDate);


    StopRegRecordDocument getByStopReg(String stopReg);
}
