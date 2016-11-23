package com.proper.enterprise.isj.pay.ali.repository;

import com.proper.enterprise.isj.pay.ali.entity.AliRefundEntity;
import com.proper.enterprise.isj.pay.ali.model.AliRefund;
import com.proper.enterprise.platform.core.repository.BaseRepository;

/**
 * 支付宝退款Repository
 */
public interface AliRefundRepository extends BaseRepository<AliRefundEntity, String> {

    // @CacheQuery
    AliRefund findByOutTradeNo(String outTradeNo);
}
