package com.proper.enterprise.isj.context;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface NavInfoIdsContext<T> extends BusinessContext<T> {

    Collection<String> getNavInfoIds();

    void setNavInfoIds(Collection<String> entity);
}
