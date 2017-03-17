/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/GroupHandlerObserver.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler.observer;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.GroupHandler;
import com.proper.enterprise.platform.core.api.HandlerObserver;
import com.proper.enterprise.platform.core.api.IHandler;

/**
 * .
 * <p>
 * 描述该类功能介绍.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 *          0.2.0 2017-03-03 迁移到平台
 */

public interface GroupHandlerObserver<H extends IHandler, C extends Collection<H>, G extends GroupHandler<H, C>> extends HandlerObserver<H> {

    /**
     * 在整个处理过程全部执行完毕时运行此方法.
     * 
     * @param handlers
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onStop(G handlers);

    /**
     * 所有处理过程执行之前运行此方法.
     * 
     * @param handlers
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onStart(G handlers);

}
