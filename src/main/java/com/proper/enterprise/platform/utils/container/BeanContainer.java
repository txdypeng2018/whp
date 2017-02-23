package com.proper.enterprise.platform.utils.container;

public interface BeanContainer<K> {


    void register(ManagedBean<K> conv);


    <B extends ManagedBean<K>> B findBean(K key);

}
