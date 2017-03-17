/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/AbstractGroupHandler.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.api.ILoggable;

import javassist.Modifier;

/**
 * 抽象的复合处理过程.
 * <p>
 * </p>
 *
 * @param <C> 存储子过程的集合类类型.
 *            <p>
 *            当此类型访问级别为public,并且有默认构造函数的时候，尝试实例化C的对象，并设置为{@link #handlers}属性的值.
 *            </p>
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 *          0.2.0 2017-03-03 迁移到平台
 */
public abstract class AbstractGroupHandler<H extends IHandler, C extends Collection<H>>
        implements ModifiedGroupHandler<H, C>, ILoggable {

    /**
     * 子过程.
     */
    private C handlers= autoInitializedHandlers();

    /**
     * AbstractGroupHandler的构造函数.
     * <p>
     * 尝试自动初始化{@link #handlers}属性.
     * </p>
     */
    protected AbstractGroupHandler() {

    }

    /**
     * 此方法在子类中运行的时候会根据泛型参数中的集合类型C尝试自动实例化本对象的{@link #handlers}属性.
     * <p>
     * 如果泛型参数的实际类型无法通过默认构造函数实例化则{@link #handlers}属性依然为空.
     * </p>
     * <p>
     * 提示:通过spring配置文件创建的实例{@link #handlers}属性总是为空,需要在配置中指定{@link #handlers}属性的值.
     * </p>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private C autoInitializedHandlers() {
        C result = null;
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] args = pt.getActualTypeArguments();
        Type type = args[args.length-1];
        // java8中无法将type强转为Class类型.
        String typeName = type.toString();
        int idx = typeName.indexOf("<");
        if (idx < 0) {
            idx = typeName.length();
        }
        typeName = typeName.substring(0, idx);

        try {
            Class clz = Class.forName(typeName);

            if (!Modifier.isAbstract(clz.getModifiers())) {
                Constructor constr = clz.getConstructor(new Class[0]);
                result = (C)constr.newInstance();
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            trace("c.p.e.p.c.h.AbstractGroupHandler.autoInitializedHandlers() init fail.", e);
        }
        return result;

    }

    @SuppressWarnings("unchecked")
    @Override
    public void setHandlers(Collection<H> handlers) {
        this.handlers = (C) handlers;
    }

    @Override
    public C getHandlers() {
        return handlers;
    }

    @Override
    public void addHandler(H handler) {
        this.handlers.add(handler);
    }

    @Override
    public void removeHandler(H handler) {
        this.handlers.remove(handler);
    }

}
