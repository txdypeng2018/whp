package com.proper.enterprise.isj.payment.logger.utils.codec;

import java.io.IOException;

import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.platform.core.utils.JSONUtil;

/**
 * 与{@link SimplePayLogEncoder}匹配的解码器.
 *
 * @param <T> 解码前数据类型.
 * @param <L> 解码后获得的支付日志类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class SimplePayLogDecoder<T, L extends PayLog<T>> extends WithSeparatorWrapper implements PayLogDecoder<T, L> {

    /**
     * 创建返回值实例的工厂对象.
     *
     * @since 0.1.0
     */
    Factory<L> factory;

    /**
     * 获得返回值工厂.
     *
     * @return 返回值工厂实例.
     * @since 0.1.0
     */
    protected Factory<L> getFactory() {
        return factory;
    }

    /**
     * 设置返回值工厂.
     *
     * @param factory 返回值工厂实例.
     * @since 0.1.0
     */
    public void setFactory(Factory<L> factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public L decode(String source) {
        L res = this.getFactory().newInstance();
        String sep = this.getSeparator();
        int sepLen = sep.length();
        res.setStep(Integer.parseInt(source.substring(0, 4), 16));
        res.setCause(Integer.parseInt(source.substring(4, 12), 16));
        int field2Start = 12 + sepLen;
        int field2End = source.indexOf(sep, field2Start);
        String type = source.substring(field2Start, field2End);
        Class<T> clz;
        try {
            clz = (Class<T>) Class.forName(type);
            res.setBusinessObject(JSONUtil.parse(source.substring(field2End + sepLen), clz));
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

}
