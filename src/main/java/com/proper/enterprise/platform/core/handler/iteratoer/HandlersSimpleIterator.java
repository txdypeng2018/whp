/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/HandlersSimpleIterator.java
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
import java.util.Iterator;

import com.proper.enterprise.platform.core.api.IHandler;

/**
 * 处理过程简单迭代器.
 * <p>直接适配传入的Handler实例的{@linkplain Collection#iterator() iterator()}获得到的迭代器.</p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 *          0.2.0 2017-03-03 迁移到平台
 */

public class HandlersSimpleIterator<T extends IHandler> implements HandlersIterator<T> {

    /**
     * 被包装的迭代器.
     */
    private Iterator<T> itr;
    
    /**
     * HandlersSimpleIterator的构造函数.
     *
     */
    public HandlersSimpleIterator(){
        
    }
    
    /**
     * HandlersSimpleIterator的构造函数.
     *
     * @param handlers 初始化处理过程集合.
     */
    public HandlersSimpleIterator(Collection<T> handlers){
        this();
        pushHandlers(handlers);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        return itr.next();
    }

    /* (non-Javadoc)
     * @see com.proper.enterprise.platform.utils.handler.HandlersIterator#pushHandlers(java.util.Collection)
     */
    @Override
    public void pushHandlers(Collection<T> handlers) {
        this.itr = handlers.iterator();
    }

    public void remove() {
        this.itr.remove();
    }

}
