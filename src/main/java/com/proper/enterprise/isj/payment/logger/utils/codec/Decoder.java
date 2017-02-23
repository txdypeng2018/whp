package com.proper.enterprise.isj.payment.logger.utils.codec;

/**
 * 解码器.
 *
 * @param <S> 输入类型.
 * @param <T> 输出类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface Decoder<S, T> {

    /**
     * 解码.
     *
     * @param source 需解码内容.
     * @return 解码后内容.
     * @since 0.1.0
     */
    T decode(S source);
}
