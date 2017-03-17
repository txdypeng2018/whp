/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/business/strategy/DefaultGroupBusinessProcessStrategy.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.business.strategy;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIterator;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIteratorFactory;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersSimpleIteratorFactory;
import com.proper.enterprise.platform.core.handler.strategy.AbstractGroupHandlerProcessStrategy;

/**
 * 复合业务执行策略的默认实现.
 * 
 * <p>该执行策略默认设置了一个Handler简单迭代器工厂。在默认情况下会顺序执行复合业务的所有子业务.</p>
 * 
 * @param <T> 业务执行结果类型.
 * @param <M> 业务上下文类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 */
public class DefaultGroupBusinessProcessStrategy<T, M extends BusinessContext<T>>
        extends AbstractGroupHandlerProcessStrategy implements GroupBusinessProcessStrategy<T, M> {

    /**
     * 处理过程迭代器工厂.
     * <p>不同的迭代器的会影响子业务是否执行，执行顺序等。</p>
     */
    @SuppressWarnings("rawtypes")
    private HandlersIteratorFactory factory = new HandlersSimpleIteratorFactory<IHandler>();

    /**
     * 业务执行时所需要的业务上下文.
     */
    private M businessContext;

    @Override public M getBusinessContext() {
        return businessContext;
    }

    @Override
    public void setBusinessContext(M ctx) {
        this.businessContext = ctx;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <H extends IHandler, B extends HandlersIterator<H>> HandlersIteratorFactory<H, B> getIteratorFactory() {
        return (HandlersIteratorFactory<H, B>) factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <H extends IHandler> void process(H handler) throws Throwable {
        IBusiness<T, BusinessContext<T>> biz = (IBusiness<T, BusinessContext<T>>)handler;
        biz.process(this.getBusinessContext());
    }

}
