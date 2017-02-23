package com.proper.enterprise.isj.payment.logger.service.impl;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.proper.enterprise.isj.payment.converter.Converter;
import com.proper.enterprise.isj.payment.logger.entity.DefaultPayLogRecordEntity;
import com.proper.enterprise.isj.payment.logger.repository.PayLogRecordRepository;
import com.proper.enterprise.isj.payment.logger.service.PayLoggerService;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 记录支付日志的服务类.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
@Service
public class PayLoggerServiceImpl implements PayLoggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayLoggerServiceImpl.class);

    @Autowired
    PayLogRecordRepository repo;

    /**
     * 支付消息转换器.
     * <p>
     * 从上下文中获取id为"loggingEvent2PayLogRecordConverter"的转换器.<br/>
     * 见配置文件:<br/>
     * {@link classpath:spring/isj/delayRefund/applicationContext-converters.xml}
     * </p>
     *
     * @since 0.1.0
     */
    @Autowired
    @Qualifier("loggingEvent2PayLogRecordConverter")
    Converter<Object, Object> converter;

    @Override
    public <E extends LoggingEvent> void savePayLog(E eventObject) {
        DefaultPayLogRecordEntity record = null;
        try {
            record = (DefaultPayLogRecordEntity) converter.convert(eventObject, null);
            repo.saveAndFlush(record);
        } catch (Exception e) {
            try {
                LOGGER.error(JSONUtil.toJSON(eventObject), e);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

}
