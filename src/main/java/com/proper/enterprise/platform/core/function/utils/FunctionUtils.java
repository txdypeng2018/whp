/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/function/utils/FunctionUtils.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */
package com.proper.enterprise.platform.core.function.utils;

import com.proper.enterprise.platform.core.api.IFunction;

/**
 * {@link IFunction}的工具类.
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-03-01 迁移到平台.
 */
public final class FunctionUtils {

    /**
     * 调用功能.
     * <p>
     * 异常会被封装为一个{@link RuntimeException}.
     * </p>
     * 
     * @param func 被调用的功能.
     * @param params 参数列表.
     * @return 执行结果.
     */
    public static <T> T invoke(IFunction<T> func, Object... params) {
        try {
            return (T) func.execute(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
