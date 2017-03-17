/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/KeyHandler.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年3月2日
 * </p>
 * @author: 王浩鹏<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.api;

/**
 * 带有唯一标识的处理过程.
 * <p>
 * 在运行环境中可以通过key值找到当前处理器实例。
 * </p>
 * 
 * @param <K> 标识的类型.
 * 
 * @author 王浩鹏<wanghaopeng@propersoft.cn>
 *  
 * @version 0.2.0 新建;
 * @since 0.2.0
 *
 */
public interface KeyHandler<K> extends IHandler {

    /**
     * 获得处理器的唯一标识.
     * @return 处理器的唯一标识.
     * 
     * @since 0.2.0
     */
    K getKey();
    
}
