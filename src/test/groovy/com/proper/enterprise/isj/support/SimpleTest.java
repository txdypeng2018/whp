package com.proper.enterprise.isj.support;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.proper.enterprise.platform.core.business.DefaultGroupBusiness;

public class SimpleTest {

    @SuppressWarnings("unchecked")
    //@Test
    public void test() throws Throwable {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "com/proper/enterprise/isj/support/DefaultGroupBusiness.xml");

        DefaultGroupBusiness<Object> biz = (DefaultGroupBusiness<Object>) ctx.getBean("testBusiness");
        TestContext t = new TestContext();
        biz.process(t);
        biz.process(t);
        ctx.close();
    }
}
