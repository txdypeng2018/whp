package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface PayChannelIdContext<T> extends BusinessContext<T> {
    String getPayChannelId();

    void setPayChannelId(String payChannelId);
}
