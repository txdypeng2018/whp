/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/HandlersIteratorFactory.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler.iteratoer;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.IFactory;
import com.proper.enterprise.platform.core.api.IHandler;


/**
 * Handler迭代器工厂类.
 * <p>
 * 获得Handler迭代器.
 * </p>
 *
 * @param <T> 迭代器类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 *          0.2.0 2017-03-03 迁移到平台
 */
public interface HandlersIteratorFactory<H extends IHandler, T extends HandlersIterator<H>> extends IFactory {

    /**
     * 创建迭代器.
     * 
     * @param handlers 需要迭代的处理过程.
     * @return 迭代器.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    T newInstance(Collection<H> handlers);
}
