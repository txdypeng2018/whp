package com.proper.enterprise.isj.order.model;

import com.proper.enterprise.platform.core.api.IBase;

public interface Order extends IBase {

	String getOrderNo();

	void setOrderNo(String orderNo);

	int getPaymentStatus();

	void setPaymentStatus(int paymentStatus);

	int getIsdel();

	void setIsdel(int isdel);

	String getOrderStatus();

	void setOrderStatus(String orderStatus);

	String getCancelRemark();

	void setCancelRemark(String cancelRemark);

	String getCancelDate();

	void setCancelDate(String cancelDate);

	String getOrderAmount();

	void setOrderAmount(String orderAmount);

	String getPayWay();

	void setPayWay(String payWay);

	String getFormClassInstance();

	void setFormClassInstance(String formClassInstance);

	String getFormId();

	void setFormId(String formId);

}
