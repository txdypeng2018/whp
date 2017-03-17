package com.proper.enterprise.isj.business;

import com.proper.enterprise.isj.context.BusinessContextFactory;
import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IFunction;

public interface BusinessToolkit extends RepositoryFunctionToolkit {

    public static interface ContextOperator<T extends BusinessContext<Object>> {
        void operate(T ctx);
    }

    public static interface OnThrow<T extends Throwable> {
        void handle(T ex) throws Throwable;
    }

    BusinessContextFactory getCtxFactory();

    @SuppressWarnings("rawtypes")
    <T> T execute(Class bizClass, BusinessContext<T> ctx);

    @SuppressWarnings("rawtypes")
    <T> T execute(Class bizClass, BusinessContext<T> ctx, OnThrow onThrow) throws Throwable;
    
    <T> T execute(String id, BusinessContext<T> ctx);

    @SuppressWarnings("rawtypes")
    <T> T execute(Class bizClass, ContextOperator operator);

    @SuppressWarnings("rawtypes")
    <T> T executeOnThrow(Class bizClass, OnThrow onThrow) throws Throwable;

    @SuppressWarnings("rawtypes")
    <T> T execute(Class bizClass, ContextOperator operator, OnThrow onThrow) throws Throwable;

    @SuppressWarnings("rawtypes")
    <T> T executeSimpleFunction(Class functionClz, Object... params);

    <T> T executeSimpleFunction(IFunction<T> function, Object... params);
}