package com.proper.enterprise.isj.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.ILoggable;

public class ContextUtils implements ILoggable {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T getProperty(BusinessContext ctx, String propName) {
        T res = null;
        if (ctx instanceof Map) {
            return (T) ((Map) ctx).get(propName);
        } else {
            try {
                res = (T) BeanUtils.getPropertyDescriptor(ctx.getClass(), propName).getReadMethod().invoke(ctx,
                        new Object[0]);
            } catch (BeansException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                res = null;
            }
        }
        return res;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> void setProperty(BusinessContext ctx, String propName, T value) {
        if (ctx instanceof Map) {
            ((Map) ctx).put(propName, value);
        } else {
            try {
                BeanUtils.getPropertyDescriptor(ctx.getClass(), propName).getWriteMethod().invoke(ctx,
                        new Object[] {value });
            } catch (BeansException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
