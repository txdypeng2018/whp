package com.proper.enterprise.isj.business;

import com.proper.enterprise.platform.core.api.IFunction;

public interface FunctionToolkit {

    @SuppressWarnings("rawtypes")
    <T> T executeFunction(Class functionClz, Object... params);

    <T> T executeFunction(IFunction<T> function, Object... params);

}