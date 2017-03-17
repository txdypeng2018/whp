package com.proper.enterprise.isj.support.function;

import com.proper.enterprise.platform.core.api.IFunction;

public final class FunctionUtils {

    public static <T> T invoke(IFunction<T> func, Object... params) {
        try {
            return (T) func.execute(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
