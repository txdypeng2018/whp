/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/ModifiedGroupHandler.java
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

import com.proper.enterprise.platform.core.api.GroupHandler;
import com.proper.enterprise.platform.core.api.IHandler;

/**
 * 可修改复合处理过程.
 *
 * @param <H> 子处理过程的类型.
 * @param <C> 处理过程组的集合类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建;
 *          0.2.0 2017-03-03 迁移到平台.
 */
public interface ModifiedGroupHandler<H extends IHandler, C extends Collection<H>> extends GroupHandler<H, C> {

    /**
     * 设置子处理过程.
     * 
     * @param handlers
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void setHandlers(Collection<H> handlers);

    /**
     * 向现有处理过程子集中添加一个新的处理过程.
     * 
     * @param handler 要被添加的处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void addHandler(H handler);

    /**
     * 从现有处理过程子集中移除一个处理过程.
     * 
     * @param handler 要被移除的处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void removeHandler(H handler);

}
