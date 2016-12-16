package com.proper.enterprise.isj.pay.cmb.repository;

import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.platform.core.repository.BaseRepository;

/**
 * 招商银行支付结果异步通知Repository
 */
public interface CmbPayNoticeRepository extends BaseRepository<CmbPayEntity, String> {

    CmbPayEntity findByMsg(String msg);

    CmbPayEntity findByBillNoAndDate(String billNo, String date);

}
