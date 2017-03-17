package com.proper.enterprise.isj.support;

import com.proper.enterprise.platform.core.api.IBusiness;

public class StepTrueEx implements IBusiness<Object, TestContext> {

    @Override
    public void process(TestContext ctx) throws Exception {
        String step1 = ctx.getStep1();
        ctx.setStep1(ctx.getStep2());
        ctx.setStep2(step1);
    }

}
