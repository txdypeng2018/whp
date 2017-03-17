/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/GroupHandler.java
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * date: 2017年2月21日
 * author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

import java.util.Collection;

/**
 * 复合处理过程类.
 * <p>
 * 定义一个由多个子过程组成的处理过程.此接口是{@link IHandler}接口的组合模式.
 * 用于复杂处理过程的内部解耦，优化递归或分级数据。 
 * </p>
 *
 * @param <H>
 *            子处理过程的类型.
 * @param <C>
 *            迭代器集合的类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 */
public interface GroupHandler<H extends IHandler, C extends Collection<H>>
        extends IHandler {

    /**
     * 获得子处理过程集合.
     * 
     * @return 子处理过程集合.
     * @author 王东石<wangdongshi@propersoft.cn>
     * @since 0.1.0
     */
    C getHandlers();

}
