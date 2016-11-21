package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;

import java.util.List;

/**
 * Created by think on 2016/10/7 0007.
 */
public interface StopRegRecordService {

    void saveStopRegRecord(StopRegRecordDocument record);

    /**
     *
     * @param stopDate
     * @return
     */
    List<StopRegRecordDocument> findStopRegRecordDocument(String stopDate);


    StopRegRecordDocument getByStopReg(String stopReg);
}
