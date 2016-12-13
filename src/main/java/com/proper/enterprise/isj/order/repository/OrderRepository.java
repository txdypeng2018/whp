package com.proper.enterprise.isj.order.repository;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.platform.core.annotation.CacheQuery;
import com.proper.enterprise.platform.core.repository.BaseRepository;

import java.util.List;

public interface OrderRepository extends BaseRepository<OrderEntity, String> {

    @CacheQuery
    OrderEntity findByOrderNo(String orderNo);

    @CacheQuery
    OrderEntity getByFormId(String formId);

    List<OrderEntity> findByOrderNoLike(String orderNo);
}
