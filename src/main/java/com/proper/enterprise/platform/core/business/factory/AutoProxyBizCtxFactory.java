/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/business/factory/AutoProxyBizCtxFactory.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年3月4日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.business.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.IFactory;
import com.proper.enterprise.platform.core.api.ILoggable;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

/**
 * 自动构建业务上下文工厂类.
 * <p>
 * 使用说明参见：{@link #createBizContext(Class)}。
 * </p>
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.2.0 新建.
 * @since 0.2.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class AutoProxyBizCtxFactory implements IFactory, ILoggable {
    /**
     * 泛型参数的前缀字符.
     */
    private static final String KEY_WORD_TYPE_PARAMETERS_START_SYMBOL = "<";
    /**
     * 接口类型的Type获得字面值时的前缀.
     */
    private static final String KEY_WORD_CLASS = "class ";
    /**
     * 接口类型的Type获得字面值前缀的长度.
     */
    private static final int KEY_WORD_CLASS_LENGTH = 6;

    /**
     * boolean型属性getter操作方法名前缀.
     */
    private static final String KEY_WORD_GET_PROPERTIES_METHOD_PREFIX_IS = "is";
    /**
     * 一般类型属性getter操作方法名前缀.
     */
    private static final String KEY_WORD_GET_PROPERTIES_METHOD_PREFIX_GET = "get";
    /**
     * 一般类型属性setter操作方法名前缀.
     */
    private static final String KEY_WORD_SET_PROPERTIES_METHOD_PREFIX_SET = "set";
    /**
     * 一般类型属性操作方法名前缀长度.
     */
    private static final int KEY_WORD_PROPERTIES_OPERATE_NORMAL_METHOD_PREFIX_LENGTH = 3;

    /**
     * 从操作属性的方法名获得属性名.
     * 
     * @param methodName 操作属性的方法名.
     * @param prefixLength 前缀长度.
     * @return 属性名.
     */
    private static String toPropertyName(String methodName, int prefixLength) {
        StringBuffer buf = new StringBuffer(methodName);
        buf.delete(0, prefixLength);
        buf.setCharAt(0, Character.toLowerCase(buf.charAt(0)));
        return buf.toString();
    }

    /**
     * 获得泛型参数类型type对应的类.
     * 
     * @param type 泛型类型.
     * @return 泛型参数类型type对应的类.
     * @throws ClassNotFoundException 类不存在.
     */
    private static Class convType2Class(Type type) throws ClassNotFoundException {
        String typename = type.toString();
        int ignoreidx = typename.indexOf(KEY_WORD_TYPE_PARAMETERS_START_SYMBOL);
        if (ignoreidx >= 0) {
            typename = typename.substring(0, ignoreidx);
        }
        if (typename.startsWith(KEY_WORD_CLASS)) {
            typename = typename.substring(KEY_WORD_CLASS_LENGTH);
        }
        return Class.forName(typename);
    }

    /**
     * 向类型集合中添加类实例.
     * <p>
     * 无重复.
     * </p>
     * 
     * @param col 类型集合.
     * @param clz 要添加的实例》
     */
    private static void add2Collection(Collection<Class> col, Class clz) {
        if (!col.contains(clz)) {
            col.add(clz);
        }
    }

    /**
     * 递归参数类所有接口，所有父类，以及所有接口的父接口，找到其中泛型参数中是{@link BusinessContext}接口的参数并返回对应的类型的集合.
     * 
     * @param clz 目标类.
     * @return 与目标类有关的BusinessContext的子类型的集合.
     */
    private static Collection<Class> findContextTypes(Class clz) {
        Collection<Class> res = new LinkedList<Class>();
        if (clz != null) {
            if (!clz.equals(Object.class)) {
                for (Class cur : findContextTypes(clz.getSuperclass())) {
                    add2Collection(res, cur);
                }
                for (Class curClz : clz.getInterfaces()) {
                    for (Class cur : findContextTypes(curClz)) {
                        add2Collection(res, cur);
                    }
                }
            }
            TypeVariable[] types = clz.getTypeParameters();
            for (TypeVariable type : types) {
                for (Type b : type.getBounds()) {
                    try {
                        Class target = convType2Class(b);
                        target.asSubclass(BusinessContext.class);
                        add2Collection(res, target);
                    } catch (ClassNotFoundException | ClassCastException e) {
                        continue;
                    }
                }
            }
        }
        return res;
    }

    /**
     * 获得业务上下文实例.
     * 
     * @param <T> 上下文结果值类型.
     * @param <M> 返回上下文所需满足的接口复合类型.
     * @param <B> 输入参数对应的业务类类型.
     * @param bizClass 使用返回结果的业务对象的类型.
     *            <p>
     *            {@code bizClass}参数对应的类型必须为{@link IBusiness}的子类型,并且该类型必须有一个泛型参数，用于指定实现父接口的
     *            {@link BusinessContext}子类泛型参数的类型。例如：
     * 
     *            <pre>
     *            class Demo1<M extends BusinessContext<Object>> implements IBusiness<Object, M> {
     *                // 实现接口中定义的方法...
     *            }
     *            </pre>
     * 
     *            上面 Demo1类可以作为参数使用;
     * 
     *            <pre>
     *            class Demo2 implements IBusiness<Object, BusinessContext<Object>> {
     *                // 实现接口中定义的方法...
     *            }
     *            </pre>
     *            </p>
     *            而Demo2类则无法作为参数使用,因为无法获得Demo2中泛型参数的限制类型.
     * @return 业务上下文实例.
     */
    public <T, M extends BusinessContext<T>, B extends IBusiness<T, M>> M createBizContext(Class<B> bizClass) {
        Collection<Class> cc = findContextTypes(bizClass);
        Class[] interfaces = cc.toArray(new Class[cc.size()]);

        // 生成代理对象.
        M res = (M) Proxy.newProxyInstance(bizClass.getClassLoader(), interfaces, new InvocationHandler() {
            private Map<String, Object> ctxMap = new HashMap<>();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object res = null;
                String methodName = method.getName();

                if (methodName.startsWith(KEY_WORD_GET_PROPERTIES_METHOD_PREFIX_GET)) {
                    res = ctxMap
                            .get(toPropertyName(methodName, KEY_WORD_PROPERTIES_OPERATE_NORMAL_METHOD_PREFIX_LENGTH));
                } else if (methodName.startsWith(KEY_WORD_GET_PROPERTIES_METHOD_PREFIX_IS)) {
                    res = ctxMap.get(toPropertyName(methodName, 2));
                } else if (methodName.startsWith(KEY_WORD_SET_PROPERTIES_METHOD_PREFIX_SET)) {
                    if (args != null) {
                        ctxMap.put(toPropertyName(methodName, KEY_WORD_PROPERTIES_OPERATE_NORMAL_METHOD_PREFIX_LENGTH),
                                args.length == 1 ? args[0] : args);
                    }
                } else {
                    Class[] types = method.getParameterTypes();
                    Method objMethod = null;
                    try {
                        objMethod = ctxMap.getClass().getDeclaredMethod(methodName, types);
                    } catch (Exception e) {
                        LOGGER.info(e.getMessage(), e);
                    }
                    if (objMethod == null) {
                        objMethod = ctxMap.getClass().getMethod(methodName, types);
                    }
                    res = objMethod.invoke(ctxMap, args);

                }

                return res;
            }
        });
        return res;
    }

    /**
     * @see #createBizContext(Class)
     */
    @Override
    public <T> T create(Object... params) {
        return (T) createBizContext((Class) params[0]);
    }

}
