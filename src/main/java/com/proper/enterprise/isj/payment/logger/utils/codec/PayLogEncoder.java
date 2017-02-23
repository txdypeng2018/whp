package com.proper.enterprise.isj.payment.logger.utils.codec;

import com.proper.enterprise.isj.payment.logger.PayLog;

/**
 * 支付日志编码器.
 *
 * @param <T> 编码前数据类型.
 * @param <L> 编码后获得的支付日志类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface PayLogEncoder<T, L extends PayLog<T>> extends Encoder<L, String> {

}
