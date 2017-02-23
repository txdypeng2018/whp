package com.proper.enterprise.isj.payment.logger.utils.codec;

import com.proper.enterprise.isj.payment.logger.PayLog;

/**
 * 支付日志解码器.
 *
 * @param <T> 解码前数据类型.
 * @param <L> 解码后获得的支付日志类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface PayLogDecoder<T, L extends PayLog<T>> extends Decoder<String, L> {

}
