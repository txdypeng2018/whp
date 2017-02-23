package com.proper.enterprise.isj.payment.logger;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.proper.enterprise.platform.core.utils.JSONUtil;

/**
 * 交易日志接口的默认实现.
 * 
 * @param <T>
 *            业务对象的类型.
 * 
 * @since 0.1.0
 * @author 王东石<wangdongshi@propersoft.cn>
 * @see PayLog
 */
@Component
@Scope("prototype")
@Lazy
public class DefaultPayLog<T> implements PayLog<T> {

    /**
     * 保存步骤的属性.
     * 
     * @since 0.1.0
     */
    private int step;
    /**
     * 保存业务对象的属性.
     * 
     * @since 0.1.0
     */
    private T businessObject;

    private int cause = DEFAULT_CAUSE;

    @Override
    public int getStep() {
        return step;
    }

    @Override
    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public T getBusinessObject() {
        return businessObject;
    }

    @Override
    public void setBusinessObject(T bo) {
        this.businessObject = bo;
    }

    @Override
    public String businessObject2String() {
        try {
            return JSONUtil.toJSON(this.businessObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBusinessObjectType() {
        return businessObject == null ? "" : businessObject.getClass().getName();
    }

    @Override
    public int getCause() {
        return cause;
    }

    @Override
    public void setCause(int cause) {
        this.cause = cause;
    }

}
