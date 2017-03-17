/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IFactory.java
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
 * 工厂类.
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 新建;
 * @version 0.2.0 2017-02-28 移植到平台.
 * 
 * @since 0.1.0
 */
public interface IFactory {

    /**
     * 获得对象实例.
     * 
     * @param <T> 输出对象的类型.
     * @param params 创建对象时的输入参数.
     * @return 输出对象实例.
     * @since 0.1.0
     */
    <T> T create(Object... params);

}
