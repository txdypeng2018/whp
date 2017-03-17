/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/ContextDecide.java
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
 * 根据业务上下文{@link BusinessContext}的内容进行决策.
 * <p>
 * 用于在业务流程中控制流程分支时的判断.
 * </p>
 * 
 * @param <T> 业务上下文的返回类型.
 * @param <R> 决策的返回类型.
 * @param <M> 业务上下文的类型.
 * @author 王浩鹏<wanghaopeng@propersoft.cn>
 * @version 0.2.0 新建;
 * @since 0.2.0
 */
public interface ContextDecide<T, R, M extends BusinessContext<T>> extends IDecide<M, R> {

}
