package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryBusiness;
import com.proper.enterprise.isj.context.RepositoryOperationContext;
import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;
import com.proper.enterprise.isj.proxy.repository.StopRegRecordRepository;
import com.proper.enterprise.isj.proxy.service.StopRegRecordService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * 停止挂号服务.
 * Created by think on 2016/10/7 0007.
 */
@Service
public class StopRegRecordServiceImpl extends AbstractService implements StopRegRecordService {

    @Autowired
    RepositoryBusiness<?, Object, ? extends RepositoryOperationContext<?, Object>> biz;

    @Override
    public void saveStopRegRecord(StopRegRecordDocument record) {
        toolkit.executeRepositoryFunction(StopRegRecordRepository.class, "save", record);
    }

    @Override
    public List<StopRegRecordDocument> findStopRegRecordDocument(String stopDate) {
        return toolkit.executeRepositoryFunction(StopRegRecordRepository.class, "findBystopDateAfter", stopDate);
    }

    @Override
    public StopRegRecordDocument getByStopReg(String stopReg) {
        return toolkit.executeRepositoryFunction(StopRegRecordRepository.class, "getByStopReg", stopReg);
    }

}
