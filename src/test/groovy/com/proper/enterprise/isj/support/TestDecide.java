package com.proper.enterprise.isj.support;

import com.proper.enterprise.platform.core.api.ContextDecide;

public class TestDecide implements ContextDecide<Object, Boolean, TestContext> {

    private int value;

    @Override
    public Boolean decide(TestContext source) {
        return value++ % 2 == 0;
    }

}
