package com.proper.enterprise.isj.pay.weixin.repository;

import com.proper.enterprise.isj.pay.weixin.entity.WeixinRefundEntity;
import com.proper.enterprise.platform.core.annotation.CacheQuery;
import com.proper.enterprise.platform.core.repository.BaseRepository;

/**
 * 微信退款Repository
 *
 */
public interface WeixinRefundRepository extends BaseRepository<WeixinRefundEntity, String> {

    @CacheQuery
    WeixinRefundEntity findByOutTradeNo(String outTradeNo);
}
