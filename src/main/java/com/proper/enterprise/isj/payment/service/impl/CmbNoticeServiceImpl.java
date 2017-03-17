package com.proper.enterprise.isj.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.function.payment.CmbSaveNoticeProcessFunction;
import com.proper.enterprise.platform.api.pay.service.NoticeService;
import com.proper.enterprise.platform.pay.cmb.entity.CmbPayEntity;

/**
 * 一网通异步通知业务处理类.
 */
@Service("pay_notice_cmb")
public class CmbNoticeServiceImpl implements NoticeService<CmbPayEntity> {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    protected FunctionToolkit toolkitx;
    
    /**
     * 异步通知业务处理代码
     *
     * @param cmbInfo 一网通异步通知处理参数
     */
    @Override
    public void saveNoticeProcess(CmbPayEntity  cmbInfo) throws Exception {
        toolkitx.executeFunction(CmbSaveNoticeProcessFunction.class, cmbInfo);
    }
}
