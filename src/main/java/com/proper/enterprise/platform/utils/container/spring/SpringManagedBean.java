package com.proper.enterprise.platform.utils.container.spring;

import org.springframework.beans.factory.InitializingBean;

import com.proper.enterprise.platform.utils.container.ManagedBean;

public interface SpringManagedBean<K> extends ManagedBean<K>, InitializingBean{

}
