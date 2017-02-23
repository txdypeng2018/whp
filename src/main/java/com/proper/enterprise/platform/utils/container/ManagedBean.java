package com.proper.enterprise.platform.utils.container;

public interface ManagedBean<K> {

    /**
     * 获得受管转换器的Key值.
     * 
     * @return 受管转换器的Key值.
     * 
     * @since 0.1.0
     */
    K getKey();
}
