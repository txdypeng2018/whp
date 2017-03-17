/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/BusinessContext.java
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * date: 2017年2月22日
 * author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

/**
 * 业务上下文.
 * <p>{@link IBusiness}接口执行时用于保存运行环境的上下文.</p>
 * 
 * @param <T> 业务结果的类型.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 移植到平台.
 */

public interface BusinessContext<T> extends IContext{

    /**
     * 获得业务执行的结果.
     * @return 执行结果.
     */
    T getResult();
    
}
