package com.proper.enterprise.platform.utils.container;

public class AbstractManagedBean<K> implements ManagedBean<K> {

    /**
     * 用于识别转换器的关键字.
     *
     * @since 0.1.0
     */
    private K key;

    @Override
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }
}
