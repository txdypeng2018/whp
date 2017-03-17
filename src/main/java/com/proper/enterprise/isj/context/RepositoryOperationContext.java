package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RepositoryOperationContext<T, R> extends BusinessContext<T> {

    Object[] getRepositoryOperateParams();

    void setRepositoryOperateParams(Object... params);

    Object getRepository();

    void setRepository(Object repository);

    @SuppressWarnings("rawtypes")
    Class getRepositoryType();

    @SuppressWarnings("rawtypes")
    void setRepositoryType(Class repositoryType);

    String getMethodName();

    void setMethodName(String method);

    R getRepositoryOperationResult();

    void setRepositoryOperationResult(R resullt);

}
