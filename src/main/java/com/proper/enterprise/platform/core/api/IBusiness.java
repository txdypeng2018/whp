/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IBusiness.java
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * date: 2017年2月22日
 * author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

/**
 * 定义业务过程.
 * <p></p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 * 
 * @see IFunction
 */

public interface IBusiness<T, C extends BusinessContext<T>> extends IHandler {
    
    /**
     * 执行业务过程.
     * @param ctx 业务执行的上下文.
     * @throws Throwable 可能发生的异常.
     * <p>请在子类型和实现类中决定是否抛出异常,以及抛出何种异常.</p>
     */
    void process(C ctx) throws Throwable;

}
