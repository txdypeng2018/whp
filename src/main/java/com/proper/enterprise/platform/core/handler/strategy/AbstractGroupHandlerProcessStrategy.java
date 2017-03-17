/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/AbstractGroupHandlerProcessStrategy.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler.strategy;

import java.util.Collection;
import java.util.logging.Handler;

import com.proper.enterprise.platform.core.api.GroupHandler;
import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIterator;
import com.proper.enterprise.platform.core.handler.observer.GroupHandlerObserver;

/**
 * 抽象集合处理过程执行策略.
 * <p>
 * 定义执行过程.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建;
 *          0.2.0 2017-03-03 迁移到平台.
 */
public abstract class AbstractGroupHandlerProcessStrategy implements GroupHandlerProcessStrategy {

    @SuppressWarnings("rawtypes")
    private GroupHandlerObserver observer;

    @SuppressWarnings("rawtypes")
    protected GroupHandlerObserver getObserver() {
        return observer;
    }

    @SuppressWarnings("rawtypes")
    protected void setObserver(GroupHandlerObserver observer) {
        this.observer = observer;
    }

    @Override
    public <H extends IHandler, C extends Collection<H>> void process(GroupHandler<H, C> handlers) throws Throwable {
        @SuppressWarnings("unchecked")
        HandlersIterator<IHandler> it = this.getIteratorFactory()
                .newInstance((Collection<IHandler>) handlers.getHandlers());

        onStart(handlers);
        IHandler cur;
        while (it.hasNext()) {
            cur = it.next();
            try {
                onBefore(cur);
                process(cur);
                onSuccess(cur);
            } catch (Throwable t) {
                onError(cur, t);
            } finally {
                onFinish(cur);
            }
            onTerminal(cur);
        }
        onStop(handlers);
    }

    /**
     * 在整个处理过程全部执行完毕时运行此方法.
     * 
     * @param handlers
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected <H extends IHandler, C extends Collection<H>, G extends GroupHandler<H, C>> void onStop(G handlers) {
        if (observer != null) {
            observer.onStop(handlers);
        }
    }

    /**
     * 所有处理过程执行之前运行此方法.
     * 
     * @param handlers
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected <H extends IHandler, C extends Collection<H>, G extends GroupHandler<H, C>> void onStart(G handlers) {
        if (observer != null) {
            observer.onStart(handlers);
        }
    }

    /**
     * 正常结束时执行此方法.
     * <p>
     * {@link #onError(Handler, Throwable)}抛出异常时会跳过此方法;<br/>
     * 此方法在 {@link #onFinish(Handler)}之后执行.
     * </p>
     * 
     * @param cur
     *            当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected void onTerminal(IHandler cur) {
        if (observer != null) {
            observer.onTerminal(cur);
        }
    }

    /**
     * 在处理正常或异常时均执行的结束方法.
     * 
     * @param cur
     *            当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected void onFinish(IHandler cur) {
        if (observer != null) {
            observer.onFinish(cur);
        }
    }

    /**
     * 处理正常结束之后执行此方法.
     * <p>
     * 在 {@link #onError(Handler, Throwable)}、 {@link #onFinish(Handler)}和
     * {@link #onTerminal(Handler)}之前.
     * </p>
     * 
     * @param cur
     *            当前处理过程.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected void onSuccess(IHandler cur) {
        if (observer != null) {
            observer.onSuccess(cur);
        }
    }

    /**
     * 当前处理过程执行之前.
     * 
     * @param cur
     *            当前处理过程.
     * @throws Exception
     *             会导致跳过当前处理过程的执行,并进入到{@link #onError(Handler, Throwable)}中。
     *             <p>
     *             {@link #onFinish(Handler)}和
     *             {@link #onTerminal(Handler)}均会被执行.
     *             </p>
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected void onBefore(IHandler cur) throws Throwable {
        if (observer != null) {
            observer.onBefore(cur);
        }
    }

    /**
     * 当前处理过程抛出异常时执行.
     * 
     * @param cur
     *            当前处理过程.
     * @param t
     *            当前异常.
     * @throws Exception
     *             重新抛出的异常.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    @SuppressWarnings("unchecked")
    protected void onError(IHandler cur, Throwable t) throws Throwable {
        if (observer != null) {
            observer.onError(cur, t);
        } else {
            throw t;
        }
    }

}
