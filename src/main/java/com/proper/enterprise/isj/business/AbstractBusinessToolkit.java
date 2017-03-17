package com.proper.enterprise.isj.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import com.proper.enterprise.isj.context.BusinessContextFactory;
import com.proper.enterprise.isj.context.RepositoryOperationContext;
import com.proper.enterprise.isj.context.SimpleFunctionInvokeContext;
import com.proper.enterprise.isj.exception.IHosException;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.exception.ErrMsgException;

public abstract class AbstractBusinessToolkit implements BusinessToolkit, ILoggable {

    @Autowired
    protected BusinessContextFactory ctxFact;
    @Autowired
    protected WebApplicationContext wac;

    public AbstractBusinessToolkit() {
        super();
    }

    public BusinessContextFactory getCtxFactory() {
        return (BusinessContextFactory) ctxFact;
    }

    protected void setCtxFactory(BusinessContextFactory ctxFact) {
        this.ctxFact = ctxFact;
    }

    protected WebApplicationContext getWac() {
        return wac;
    }

    protected void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T execute(Class bizClass, BusinessContext<T> ctx, OnThrow onThrow) throws Throwable {
        try {
            ((IBusiness<T, BusinessContext<T>>) wac.getBean(bizClass)).process(ctx);
        } catch (Throwable e) {
            if (onThrow != null) {
                onThrow.handle(e);
            } else {
                throw e;
            }
        }
        return (T) ctx.getResult();
    }

    /*
     * (non-Javadoc)
     * @see com.proper.enterprise.isj.business.BusinessToolkit#execute(java.lang.Class,
     * com.proper.enterprise.isj.support.business.BusinessContext)
     */
    @Override
    @SuppressWarnings({ "rawtypes" })
    public <T> T execute(Class bizClass, BusinessContext<T> ctx) {
        try {
            return execute(bizClass, ctx, null);
        } catch (IHosException e) {
            throw new ErrMsgException(e.getMessage());
        } catch (ErrMsgException e) {
            throw e;
        } catch (Throwable e) {

            Throwable cause = e.getCause();
            if (cause != null && (cause instanceof IHosException || cause instanceof ErrMsgException)) {
                throw new ErrMsgException(cause.getMessage());
            } else {
                error(e.getMessage(), e);
                throw new ErrMsgException(CenterFunctionUtils.APP_SYSTEM_ERR);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> T execute(Class bizClass, ContextOperator operator) {
        BusinessContextFactory fac = this.getCtxFactory();
        BusinessContext<Object> ctx = (BusinessContext<Object>) fac.create(bizClass);
        operator.operate(ctx);
        return (T) execute(bizClass, ctx);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T> T executeOnThrow(Class bizClass, OnThrow onThrow) throws Throwable {
        return execute(bizClass, e -> {
        }, onThrow);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> T execute(Class bizClass, ContextOperator operator, OnThrow onThrow) throws Throwable {
        BusinessContextFactory fac = this.getCtxFactory();
        BusinessContext<Object> ctx = (BusinessContext<Object>) fac.create(bizClass);
        if (operator != null) {
            operator.operate(ctx);
        }
        return (T) execute(bizClass, ctx, onThrow);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> T execRepositoryFunction(String methodName, ContextOperator operator, Object... params) {
        BusinessContextFactory fac = this.getCtxFactory();
        RepositoryOperationContext<?, T> ctx = (RepositoryOperationContext<?, T>) fac.create(RepositoryBusiness.class);
        operator.operate((BusinessContext<Object>) ctx);
        ctx.setMethodName(methodName);
        ctx.setRepositoryOperateParams(params);
        ctx.setRepositoryOperationResult(null);
        try {
            ((RepositoryBusiness) wac.getBean(RepositoryBusiness.class)).process(ctx);
        } catch (Exception e) {
            RuntimeException t = null;
            if (e instanceof RuntimeException) {
                t = (RuntimeException) e;
            } else {
                t = new RuntimeException(e);
            }
            throw t;
        }
        return (T) ctx.getRepositoryOperationResult();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T executeRepositoryFunction(Class repositoryType, String methodName, Object... params) {
        return execRepositoryFunction(methodName,
                c -> ((RepositoryOperationContext<?, T>) c).setRepositoryType(repositoryType), params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T executeRepositoryFunction(Object repository, String methodName, Object... params) {
        return execRepositoryFunction(methodName, c -> ((RepositoryOperationContext<?, T>) c).setRepository(repository),
                params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T executeSimpleFunction(Class repositoryType, Object... params) {
        return executeSimpleFunction(c -> {
            ((SimpleFunctionInvokeContext<?, T>) c).setSimpleFunctionType(repositoryType);
        }, params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> T executeSimpleFunction(ContextOperator operator, Object... params) {
        BusinessContextFactory fac = this.getCtxFactory();
        SimpleFunctionInvokeContext<?, T> ctx = (SimpleFunctionInvokeContext<?, T>) fac
                .<T>create(SimpleFunctionBusiness.class);
        operator.operate((BusinessContext<Object>) ctx);
        ctx.setSimpleFunctionParams(params);
        ctx.setSimpleFunctionResult(null);
        try {
            ((SimpleFunctionBusiness) wac.getBean(SimpleFunctionBusiness.class)).process(ctx);
        } catch (Exception e) {
            RuntimeException t = null;
            if (e instanceof RuntimeException) {
                t = (RuntimeException) e;
            } else {
                t = new RuntimeException(e);
            }
            throw t;
        }
        return (T) ctx.getSimpleFunctionResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T executeSimpleFunction(IFunction<T> repository, Object... params) {
        return executeSimpleFunction(c -> {
            ((SimpleFunctionInvokeContext<?, T>) c).setSimpleFunctionObject(repository);
        }, params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T executeFunction(Class clz, Object... params) {
        return executeFunction((IFunction<T>) wac.getBean(clz), params);
    }

    public <T> T executeFunction(IFunction<T> function, Object... params) {
        try {
            return function.execute(params);
        } catch (Exception e) {
            RuntimeException t = null;
            if (e instanceof RuntimeException) {
                t = (RuntimeException) e;
            } else {
                t = new RuntimeException(e);
            }
            throw t;
        }
    }

}