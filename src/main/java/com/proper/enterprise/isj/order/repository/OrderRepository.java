package com.proper.enterprise.isj.order.repository;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.platform.core.annotation.CacheQuery;
import com.proper.enterprise.platform.core.repository.BaseRepository;

public interface OrderRepository extends BaseRepository<OrderEntity, String> {

	@CacheQuery
	OrderEntity findByOrderNo(String orderNo);

	@CacheQuery
	OrderEntity getByFormId(String formId);
}
