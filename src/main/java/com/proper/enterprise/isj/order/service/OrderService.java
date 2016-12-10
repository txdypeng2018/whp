package com.proper.enterprise.isj.order.service;

import com.proper.enterprise.isj.order.model.Order;

public interface OrderService {

	Order save(Order order);

	Order findByOrderNo(String orderNo);

	Order getByFormId(String formId);

	void cancelRegistration(String orderNo) throws Exception;

	/**
	 * 根据挂号单生成订单,订单号与挂号单号相同
	 * 
	 * @param formOrder
	 *            挂号单/缴费单
	 * @return 订单
	 */
	Order saveCreateOrder(Object formOrder);

	void deleteOrder(Order order);


	/**
     * 查询订单是否已支付
     * 
     * @param payChannelId
     * @param orderNum
     * @return
     */
    boolean checkOrderIsPay(String payChannelId, String orderNum) throws Exception;
}
