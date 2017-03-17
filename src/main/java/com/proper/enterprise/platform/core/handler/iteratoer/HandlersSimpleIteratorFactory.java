/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/HandlersSimpleIteratorFactory.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler.iteratoer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.proper.enterprise.platform.core.api.IHandler;

/**
 * {@link HandlersSimpleIterator}的工厂类.
 * <p>使用new操作实例化.</p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 *          0.2.0 2017-03-03 迁移到平台
 */

public class HandlersSimpleIteratorFactory<T extends IHandler> implements HandlersIteratorFactory<T, HandlersSimpleIterator<T>> {

    /**
     * @see #newInstance(Collection) 更良好的接口.
     */
    @SuppressWarnings("unchecked")
    @Override
    public HandlersSimpleIterator<T> create(Object... params) {
        int len = params.length;
        Collection<T> tmp = len<10?new LinkedList<T>():new ArrayList<T>(len);
        return newInstance(tmp);
    }

    /* (non-Javadoc)
     * @see com.proper.enterprise.platform.utils.handler.HandlersIteratorFactory#newInstance(java.util.Collection)
     */
    @Override
    public HandlersSimpleIterator<T> newInstance(Collection<T> handlers) {
        return new HandlersSimpleIterator<>(handlers);
    }

}
