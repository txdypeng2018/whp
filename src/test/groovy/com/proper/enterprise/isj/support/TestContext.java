package com.proper.enterprise.isj.support;

import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

public class TestContext implements ModifiedResultBusinessContext<Object> {

    private Object result;
    private String step1;
    private String step2;

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    public void setStep1(String step1) {
        this.step1 = step1;
    }

    public String getStep1() {
        return step1;
    }

    public void setStep2(String step2) {
        this.step2 = step2;
    }

    public String getStep2() {
        return step2;
    }

}
