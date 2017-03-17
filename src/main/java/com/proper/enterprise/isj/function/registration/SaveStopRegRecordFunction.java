package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;
import com.proper.enterprise.isj.proxy.repository.StopRegRecordRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SaveStopRegRecordFunction implements IFunction<StopRegRecordDocument> {

    @Autowired
    StopRegRecordRepository stopRegRecordRepository;
    
    @Override
    public StopRegRecordDocument execute(Object... params) throws Exception {
        saveStopRegRecord((StopRegRecordDocument)params[0]);
        return null;
    }
    
    public void saveStopRegRecord(StopRegRecordDocument record) {
        if (record != null) {
            stopRegRecordRepository.save(record);
        }
    }

}
