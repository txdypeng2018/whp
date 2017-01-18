package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;
import com.proper.enterprise.isj.proxy.repository.StopRegRecordRepository;
import com.proper.enterprise.isj.proxy.service.StopRegRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 停止挂号服务.
 * Created by think on 2016/10/7 0007.
 */
@Service
public class StopRegRecordServiceImpl implements StopRegRecordService {

    @Autowired
    StopRegRecordRepository stopRegRecordRepository;

    @Override
    public void saveStopRegRecord(StopRegRecordDocument record) {
        if (record != null) {
            stopRegRecordRepository.save(record);
        }

    }

    @Override
    public List<StopRegRecordDocument> findStopRegRecordDocument(String stopDate) {
        return stopRegRecordRepository.findBystopDateAfter(stopDate);
    }

    @Override
    public StopRegRecordDocument getByStopReg(String stopReg) {
        return stopRegRecordRepository.getByStopReg(stopReg);
    }
}
