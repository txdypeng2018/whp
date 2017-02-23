package com.proper.enterprise.isj.payment.converter;

import java.util.Collection;

import com.proper.enterprise.platform.core.utils.CollectionUtil;

/**
 * 转换器组.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class GroupConverter extends AbstractGroupConverter<Object, Object, Object, Object>
        implements Converter<Object, Object> {

    /**
     * 执行一组转换器.
     * <p>
     * 转换器组的默认调用方式是以前一个转换器的输出作为下一个转换器的
     * 输入，并在最后一个转换器执行时， 如果target参数不为空，则作为
     * 最后一次转换的输出对象参数.
     * </p>
     */
    @Override
    public Object convert(Object source, Object target) {
        Object res = target;
        Collection<Converter<Object, Object>> convs = getConvs();
        if (CollectionUtil.isNotEmpty(convs)) {
            int i = 0;
            int maxIdx = convs.size() - 1;
            for (Converter<Object, Object> conv : convs) {
                // 最后一次转换时如果target参数非空使用target参数
                res = conv.convert(source, target != null && i == maxIdx ? target : null);
                source = res;
                i++;
            }
        }
        return res;
    }

}
