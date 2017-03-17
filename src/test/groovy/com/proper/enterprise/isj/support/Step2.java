package com.proper.enterprise.isj.support;

import com.proper.enterprise.platform.core.api.IBusiness;

public class Step2 implements IBusiness<Object, TestContext> {

    @Override
    public void process(TestContext ctx) throws Exception {
        ctx.setStep2("step2");
    }

}
