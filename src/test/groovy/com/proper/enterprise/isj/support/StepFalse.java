package com.proper.enterprise.isj.support;

import com.proper.enterprise.platform.core.api.IBusiness;

public class StepFalse implements IBusiness<Object, TestContext> {

    @Override
    public void process(TestContext ctx) throws Exception {
        ctx.setResult(false);
    }

}
