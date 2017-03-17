/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/HandlerObserver.java
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * date: 2017年2月21日
 * author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

/**
 * 处理器观察者.
 * <p>
 * 处理器执行过程中的观察者，允许在处理器执行的生命周期中检查处理器状态.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建
 * @version 0.2.0 2017-03-03 迁移到平台
 */

public interface HandlerObserver<H extends IHandler> {

    /**
     * 正常结束时执行此方法.
     * <p>
     * {@link #onError(Handler, Throwable)}抛出异常时会跳过此方法;<br/>
     * 此方法在 {@link #onFinish(Handler)}之后执行.
     * </p>
     * 
     * @param cur 当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onTerminal(H cur);

    /**
     * 在处理正常或异常时均执行的结束方法.
     * 
     * @param cur 当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onFinish(H cur);

    /**
     * 处理正常结束之后执行此方法.
     * <p>
     * 在 {@link #onError(Handler, Throwable)}、 {@link #onFinish(Handler)}和
     * {@link #onTerminal(Handler)}之前.
     * </p>
     * 
     * @param cur 当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onSuccess(H cur);

    /**
     * 当前处理过程执行之前.
     * <p>
     * 观察者中的方法不应该抛出异常，影响执行过程.
     * </p>
     * 
     * @param cur 当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onBefore(H cur);

    /**
     * 当前处理过程抛出异常时执行.
     * <p>
     * 观察者中的方法不应该抛出异常，影响执行过程.
     * </p>
     * 
     * @param cur 当前处理过程.
     * @param t 当前异常.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    void onError(H cur, Throwable t);
}
