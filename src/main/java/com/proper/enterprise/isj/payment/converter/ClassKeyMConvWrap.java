package com.proper.enterprise.isj.payment.converter;

import com.proper.enterprise.platform.utils.container.BeanContainer;
import com.proper.enterprise.platform.utils.container.spring.KeyAsClassSpringManagedBeanWrap;

@SuppressWarnings("rawtypes")
public class ClassKeyMConvWrap<S, T> extends KeyAsClassSpringManagedBeanWrap<ManagedConverter<Class, S, T>, BeanContainer<Class>>
        implements ManagedConverter<Class, S, T> {

    @Override
    public T convert(S source, T target) {
        return this.getWrappered().convert(source, target);
    }

}
