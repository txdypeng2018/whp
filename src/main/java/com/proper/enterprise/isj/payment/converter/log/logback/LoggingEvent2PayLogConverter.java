package com.proper.enterprise.isj.payment.converter.log.logback;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.proper.enterprise.isj.payment.converter.AbstractWithTargetFactoryConverter;
import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.isj.payment.logger.utils.codec.PayLogDecoder;
import org.springframework.beans.BeanUtils;

/**
 * .
 *
 * @param <T> .
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class LoggingEvent2PayLogConverter<T> extends AbstractWithTargetFactoryConverter<LoggingEvent, PayLog<T>> {

    /**
     * .
     *
     * @since 0.1.0
     */
    private PayLogDecoder<T, PayLog<T>> decoder;

    /**
     * .
     *
     * @return .
     * @since 0.1.0
     */
    public PayLogDecoder<T, PayLog<T>> getDecoder() {
        return decoder;
    }

    /**
     * .
     *
     * @param decoder .
     * @since 0.1.0
     */
    public void setDecoder(PayLogDecoder<T, PayLog<T>> decoder) {
        this.decoder = decoder;
    }


    @Override
    protected void doConvert(LoggingEvent source, PayLog<T> target) {
        PayLog<T> src = decoder.decode(source.getMessage());
        BeanUtils.copyProperties(src, target);
    }

}
