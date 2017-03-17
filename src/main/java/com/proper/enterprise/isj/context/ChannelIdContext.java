package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface ChannelIdContext<T> extends BusinessContext<T> {

    String getChannelId();

    void setChannelId(String channelId);
}
