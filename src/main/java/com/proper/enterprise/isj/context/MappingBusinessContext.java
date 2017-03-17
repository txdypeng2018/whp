package com.proper.enterprise.isj.context;

import java.util.HashMap;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

public class MappingBusinessContext<T> extends HashMap<String, Object> implements ModifiedResultBusinessContext<T> {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    protected static final String KEY_RESULT = "com.proper.enterprise.isj.business.context.MappingBusinessContext<T>::result";

    @SuppressWarnings("unchecked")
    @Override
    public T getResult() {
        return (T) this.get(KEY_RESULT);
    }

    @Override
    public void setResult(T result) {
        this.put(KEY_RESULT, result);
    }

}
