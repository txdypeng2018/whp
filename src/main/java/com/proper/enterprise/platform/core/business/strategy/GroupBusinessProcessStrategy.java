/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/business/strategy/GroupBusinessProcessStrategy.java
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
import com.proper.enterprise.platform.core.handler.strategy.GroupHandlerProcessStrategy;

/**
 * 复合业务执行策略接口.
 * 
 * <p>该接口在复合处理器执行策略接口({@link GroupHandlerProcessStrategy})的基础上定义了执行策略操作业务上下文的接口.</p>
 * 
 * @param <T> 业务执行结果类型.
 * @param <M> 业务上下文类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 */
public interface GroupBusinessProcessStrategy<T, M extends BusinessContext<T>> extends GroupHandlerProcessStrategy {
    /**
     * 获得业务执行过程中所需要的业务上下文.
     * @return 业务上下文.
     */
    M getBusinessContext();
    /**
     * 设置业务执行过程中所需要的业务上下文.
     * @param ctx 业务上下文.
     */
    void setBusinessContext(M ctx);

}
