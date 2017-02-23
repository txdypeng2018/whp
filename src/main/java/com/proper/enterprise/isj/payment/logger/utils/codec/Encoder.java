package com.proper.enterprise.isj.payment.logger.utils.codec;

/**
 * 编码器.
 *
 * @param <S> 输入类型.
 * @param <T> 输出类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface Encoder<S, T> {

    /**
     * 编码.
     *
     * @param source 编码前内容.
     * @return 编码后内容.
     * @since 0.1.0
     */
    T encode(S source);
}
