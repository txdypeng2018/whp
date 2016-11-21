package com.proper.enterprise.isj.pay.weixin.repository;


import com.proper.enterprise.isj.pay.weixin.entity.WeixinEntity;
import com.proper.enterprise.platform.core.annotation.CacheQuery;
import com.proper.enterprise.platform.core.repository.BaseRepository;

/**
 * 微信Repository
 */
public interface WeixinRepository extends BaseRepository<WeixinEntity, String> {

    @CacheQuery
    WeixinEntity findByOutTradeNo(String outTradeNo);
}
