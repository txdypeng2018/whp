/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/AbstractStrategyGroupHandler.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.handler.strategy.GroupHandlerProcessStrategy;

/**
 * 附有执行策略的集合处理过程的抽象实现.
 * <p>
 * 实现了保存策略的属性.
 * </p>
 *
 * @param <H>
 *            处理过程的公共基类或接口.
 * @param <C>
 *            子处理过程集合类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 *          0.2.0 2017-03-03 迁移到平台
 */
public class AbstractStrategyGroupHandler<H extends IHandler, C extends Collection<H>>
        extends AbstractGroupHandler<H, C>
        implements StrategyGroupHandler<H, C> {

    private GroupHandlerProcessStrategy strategy;

    @Override
    public GroupHandlerProcessStrategy getStrategy() {
        return strategy;
    }

    /**
     * 设置执行策略.
     * 
     * @param strategy
     *            执行策略.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    protected void setStrategy(GroupHandlerProcessStrategy strategy) {
        this.strategy = strategy;
    }

}
