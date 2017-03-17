/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/business/strategy/ConditionDecideFilterProcessStrategy.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.business.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.core.api.ContextDecide;
import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.api.KeyHandler;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIterator;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersIteratorFactory;
import com.proper.enterprise.platform.core.handler.iteratoer.HandlersSimpleIteratorFactory;

/**
 * 检查上下文，根据检查结果，执行不同处理的执行策略.
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 * @param <T> 业务结果类型.
 * @param <R> 检查结果类型.
 * @param <M> 业务上下文类型.
 */
@Service
@Scope("prototype")
public class ConditionDecideFilterProcessStrategy<T, R, M extends BusinessContext<T>>
        extends DefaultGroupBusinessProcessStrategy<T, M> implements GroupBusinessProcessStrategy<T, M> {
    private ContextDecide<T, R, M> decide;

    /**
     * 检查业务上下文,并返回结果.
     * @param ctx 被检查的业务上下文.
     * @return 检查结果.
     */
    protected R checkContext(M ctx) {
        return decide.decide(ctx);
    }

    /**
     * 设置一个对上下文的判断.
     * @param decide 上下文的判断.
     */
    public void setDecide(ContextDecide<T, R, M> decide) {
        this.decide = decide;
    }

    /**
     * 获得对上下文的判断.
     * @return 上下文判断
     */
    public ContextDecide<T, R, M> getDecide() {
        return decide;
    }

    @Override
    public <H extends IHandler, I extends HandlersIterator<H>> HandlersIteratorFactory<H, I> getIteratorFactory() {

        return new HandlersIteratorFactory<H, I>() {

            @SuppressWarnings("unchecked")
            @Override
            public I create(Object... params) {
                List<H> tmp = new ArrayList<H>(params.length);
                for (Object cur : params) {
                    tmp.add((H) cur);
                }
                return newInstance(tmp);
            }

            @SuppressWarnings("unchecked")
            @Override
            public I newInstance(Collection<H> handlers) {
                int length = handlers.size()/2;
                List<H> tmp = new ArrayList<H>(length);
                R flag = checkContext(getBusinessContext());
                for (H cur : handlers) {
                    KeyHandler<R> handler = (KeyHandler<R>) cur;
                    if (flag.equals(handler.getKey())) {
                        tmp.add(cur);
                    }
                }
                return (I) new HandlersSimpleIteratorFactory<H>().newInstance(tmp);
            }
        };
    }

}
