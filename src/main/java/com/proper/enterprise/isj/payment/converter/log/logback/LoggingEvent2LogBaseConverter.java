package com.proper.enterprise.isj.payment.converter.log.logback;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.proper.enterprise.isj.payment.converter.AbstractWithTargetFactoryConverter;
import com.proper.enterprise.isj.payment.logger.entity.LogBase;
import com.proper.enterprise.platform.core.utils.DateUtil;

import java.util.Date;

/**
 * .
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class LoggingEvent2LogBaseConverter extends AbstractWithTargetFactoryConverter<LoggingEvent, LogBase> {

    @Override
    protected void doConvert(LoggingEvent source, LogBase target) {
        target.setLogCtxBirthTm(DateUtil.toTimestamp(new Date(source.getContextBirthTime()), true));
        target.setLogTm(DateUtil.toTimestamp(new Date(source.getTimeStamp()), true));
        target.setThread(source.getThreadName());
        target.setWriteTm(DateUtil.getTimestamp(true));
    }

}
