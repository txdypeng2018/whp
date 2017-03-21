/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IFunction.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;


/**
 * 功能接口.
 * <p>{@link IFunction}和{@link IBusiness}都是可执行的单元，它们都集成了{@link IHandler}接口.
 * 它们的区别在于不同的实现层次。{@link IFunction}是一个功能，但是不能代表一个完整的业务逻辑；
 * 而{@link IBusiness}是一个完整的业务逻辑，无论是复合的{@code IBusiness}还是原子的{@code IBusiness}，
 * 都是一个完整的业务过程。在调用层面上，{@link IFunction}的执行方法{@link IFunction#execute(Object...)}
 * 适合不同参数列表;{@link Business}的执行方法{@link IBusiness#process(BusinessContext)}的执行参数必须
 * 为{@link BusinessContext}的子类型.</p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 */

public interface IFunction<T> extends IHandler{

    /**
     * 功能的执行方法.
     * @param params 参数列表.
     * @return 返回值.
     * @throws Exception 可能抛出异常.
     */
    T execute(Object... params) throws Exception;
    
}
