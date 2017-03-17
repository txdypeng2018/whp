package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface CategoryContext<T> extends BusinessContext<T> {
    String getCategory();

    void setCategory(String category);
}
