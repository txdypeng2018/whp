package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IFunction;

public interface SimpleFunctionInvokeContext<T, R> extends BusinessContext<T> {

    Object[] getSimpleFunctionParams();

    void setSimpleFunctionParams(Object... params);

    IFunction<R> getSimpleFunctionObject();

    void setSimpleFunctionObject(IFunction<R> func);

    @SuppressWarnings("rawtypes")
    Class getSimpleFunctionType();

    @SuppressWarnings("rawtypes")
    void setSimpleFunctionType(Class funcType);

    R getSimpleFunctionResult();

    void setSimpleFunctionResult(R resullt);
}
