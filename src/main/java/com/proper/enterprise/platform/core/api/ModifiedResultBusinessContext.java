/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/ResultModifiedBusinessContext.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年3月4日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.api;

/**
 * 可以修改结果的业务上下文.
 * 
 * @param <T> 业务结果类型.
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.2.0 新建.
 * @since 0.2.0
 */
public interface ModifiedResultBusinessContext<T> extends BusinessContext<T> {

    /**
     * 更新业务结果.
     * @param result 新的业务结果.
     */
    void setResult(T result);
    
}
