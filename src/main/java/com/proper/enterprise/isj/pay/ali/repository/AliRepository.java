package com.proper.enterprise.isj.pay.ali.repository;

import com.proper.enterprise.isj.pay.ali.entity.AliEntity;
import com.proper.enterprise.isj.pay.ali.model.Ali;
import com.proper.enterprise.platform.core.annotation.CacheQuery;
import com.proper.enterprise.platform.core.repository.BaseRepository;

/**
 * 支付宝Repository
 */

public interface AliRepository extends BaseRepository<AliEntity, String> {

    @CacheQuery
    Ali findByOutTradeNo(String outTradeNo);

}
