/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/business/AbstractGroupBusiness.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.business;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.business.strategy.DefaultGroupBusinessProcessStrategy;
import com.proper.enterprise.platform.core.business.strategy.GroupBusinessProcessStrategy;
import com.proper.enterprise.platform.core.handler.AbstractStrategyGroupHandler;

/**
 * 复合业务抽象实现.
 * 
 * @param <T> 业务执行结果类型.
 * @param <M> 业务上下文类型.
 * @param <C> 子业务集合类类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 */
public class AbstractGroupBusiness<T, M extends BusinessContext<T>, C extends Collection<IBusiness<T, M>>>
        extends AbstractStrategyGroupHandler<IBusiness<T, M>, C> implements IBusiness<T, M> {

    /**
     * 默认构造函数.
     * <p>初始化自己的执行策略为 {@link DefaultGroupBusinessProcessStrategy}, 即顺序执行策略.</p>
     */
    protected AbstractGroupBusiness() {
        this.setStrategy(new DefaultGroupBusinessProcessStrategy<>());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void process(M ctx) throws Throwable {
        GroupBusinessProcessStrategy strategy = (GroupBusinessProcessStrategy<T, BusinessContext<T>>) this
                .getStrategy();
        strategy.setBusinessContext(ctx);
        strategy.process(this);
    }
}
