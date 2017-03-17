/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IHandler.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月28日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

/**
 * 处理过程接口.
 * <p>此接口仅仅标识实现类是一个可执行的类型，并未规定如何执行。
 * 没有限制执行方法名称、参数类型，以及有几个执行方法等，这些都可以
 * 在具体的实现类中指定，例如：{@link IBusiness}和{@link IFunction}。</p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-21 新建;
 * @version 0.2.0 2017-02-28 迁移到平台.
 * @since 0.1.0
 */
public interface IHandler {
    
    /**
     * 返回对象的描述字符串.
     * <p>
     * 此方法的声明需要保留在此接口定义文件中.避免CheckStyle检查
     * 因为接口未定义方法，而认为接口定义没有意义.<br/>处理过程的
     * 组合处理，以及策略执行等，均依赖此接口。同时，为了给不同业务
     * 保留不同的执行方法，所以没有定义统一的执行方法。
     * <br/>参见{@link Object#toString()}.
     * </p>
     * @return 描述字符串.
     */
    String toString();
}
