package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface FeedbackContext<T> extends BusinessContext<T> {
    String getFeedback();

    void setFeedback(String feedback);
}
