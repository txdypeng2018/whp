package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface FeedbackStatusContext<T> extends BusinessContext<T> {
    String getFeedbackStatus();

    void setFeedbackStatus(String feedbackStatus);
}
