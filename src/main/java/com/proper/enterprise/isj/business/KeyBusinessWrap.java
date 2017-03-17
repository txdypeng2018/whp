package com.proper.enterprise.isj.business;

import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.handler.KeyHandlerWrap;
import com.proper.enterprise.platform.core.api.BusinessContext;

public class KeyBusinessWrap<K, T, C extends BusinessContext<T>> extends KeyHandlerWrap<K> implements IBusiness<T, C> {

    public KeyBusinessWrap(K key, IBusiness<T, C> inner) {
        super(key, inner);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(C ctx) throws Throwable {
        ((IBusiness<T, C>) this.getInner()).process(ctx);
    }

}
