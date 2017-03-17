/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/business/DefaultGroupBusiness.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.business;

import java.util.ArrayList;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.handler.strategy.GroupHandlerProcessStrategy;

/**
 * 复合业务默认实现.
 * 
 * @param <T> 业务执行结果类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-03-02 新建;
 * @version 0.2.0 2017-03-03 迁移到平台.
 */
public class DefaultGroupBusiness<T>
        extends AbstractGroupBusiness<T, BusinessContext<T>, ArrayList<IBusiness<T, BusinessContext<T>>>> {

    @Override
    public void setStrategy(GroupHandlerProcessStrategy strategy) {
        super.setStrategy(strategy);
    }
}
