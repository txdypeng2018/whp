/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IDecide.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年3月2日
 * </p>
 * @author: 王浩鹏<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.api;

/**
 * 决策接口，用于标识if或switch类的判断或选择过程.
 * <p>
 * 一般用于实现if条件或者switch中的分类.
 * </p>
 * 
 * @param <T> 输入条件的类型.
 * @param <R> 返回结果的类型.
 * @author 王浩鹏<wanghaopeng@propersoft.cn>
 * @version 0.2.0 新建;
 * @since 0.2.0
 */
public interface IDecide<T, R> extends IHandler {
    /**
     * 根据输入条件，进行决策，并返回决策结果.
     * 
     * @param source 输入条件.
     * @return 决策结果.
     * @since 0.2.0
     */
    R decide(T source);
}
