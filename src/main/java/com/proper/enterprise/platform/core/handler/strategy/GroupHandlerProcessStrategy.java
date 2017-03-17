/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/strategy/GroupHandlerProcessStrategy.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler.strategy;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.GroupHandler;
import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.api.IStrategy;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIterator;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIteratorFactory;

/**
 * 集合处理器执行策略.
 * <p>
 * 运行一个集合处理器.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建;
 *          0.2.0 2017-03-03 迁移到平台.
 */
public interface GroupHandlerProcessStrategy extends IStrategy {

    /**
     * 获得处理器集合迭代器的工厂.
     * <p>在复杂的业务中， {@link GroupHandler}中的子处理过程的执行顺序实际上是通过本方法返回的工厂类所构建的迭代器控制。
     * 这个迭代器可以过滤子处理过程，控制子处理过程的迭代顺序，以及其他功能。
     * </p>
     * @param <H> 集合中的处理器类型.
     * @param <T> 处理器集合的迭代器类型.
     * @return 迭代器工厂.
     */
    <H extends IHandler, T extends HandlersIterator<H>> HandlersIteratorFactory<H, T> getIteratorFactory();

    /**
     * 执行一个集合处理器.
     * 
     * @param <H> 集合中的处理器类型.
     * @param <C> 集合处理器中用于保存处理器的集合类.
     * @param groupHandler 集合处理器.
     * @throws Exception 执行过程中可能抛出异常.
     */
    <H extends IHandler, C extends Collection<H>> void process(GroupHandler<H, C> groupHandler) throws Throwable;

    /**
     * 执行集合处理器中的一个处理器.
     * 
     * @param <H> 集合中的处理器类型.
     * @param handler 被执行处理器.
     * @throws Exception 执行过程中可能抛出异常.
     */
    <H extends IHandler> void process(H handler) throws Throwable;

}
