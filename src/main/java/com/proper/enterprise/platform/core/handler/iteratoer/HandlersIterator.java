/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/HandlersIterator.java
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
 * 处理器迭代器.
 * <p>
 * 迭代{@link #pushHandlers(Collection)}设置给本对象的处理器集合.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建;
 *          0.2.0 2017-03-03 迁移到平台.
 *          
 * @since 0.1.0
 */
public interface HandlersIterator<T extends IHandler> extends Iterator<T> {

    /**
     * 设置迭代器的迭代目标.
     * 
     * @param handlers 处理器集合.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void pushHandlers(Collection<T> handlers);

}
