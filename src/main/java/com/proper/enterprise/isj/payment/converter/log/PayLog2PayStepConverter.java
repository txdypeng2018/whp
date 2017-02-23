package com.proper.enterprise.isj.payment.converter.log;

import com.proper.enterprise.isj.payment.converter.AbstractWithTargetFactoryConverter;
import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.isj.payment.logger.entity.PayStep;

import static com.proper.enterprise.isj.payment.logger.PayLogConstrants.STATUS_FIELD_MASK;
import static com.proper.enterprise.isj.payment.logger.PayLogConstrants.STEP_FIELD_MASK;

/**
 * .
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class PayLog2PayStepConverter extends AbstractWithTargetFactoryConverter<PayLog<?>, PayStep> {

    @Override
    protected void doConvert(PayLog<?> source, PayStep target) {
        int step = source.getStep();
        target.setStep(step & STEP_FIELD_MASK);
        target.setStepStatus(step & STATUS_FIELD_MASK);
        target.setCause(source.getCause());
    }

}
