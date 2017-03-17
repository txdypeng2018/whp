package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;

public interface PayChannelContext<T> extends BusinessContext<T> {

    PayChannel getPayChannel();

    void setPayChannel(PayChannel paychannel);
}
