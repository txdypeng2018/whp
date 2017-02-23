package com.proper.enterprise.isj.payment.logger.entity;

import com.proper.enterprise.isj.payment.logger.PayLogConstrants;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;

/**
 * 支付所在步骤和状态.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface PayStep {

    /**
     * 支付所在步骤.
     *
     * @return 支付所在步骤.
     * @see PayStepEnum
     * @since 0.1.0
     */
    int getStep();

    /**
     * 支付所在步骤
     *
     * @param step 支付所在步骤.
     * @see PayStepEnum
     * @since 0.1.0
     */
    void setStep(int step);

    /**
     * 当前步骤所处状态.
     *
     * @return 当前步骤所处状态.
     * @see {@link PayLogConstrants#STATUS_MASK_START},
     * {@link PayLogConstrants#STATUS_MASK_SUCCESS},
     * {@link PayLogConstrants#STATUS_MASK_EXCEPTION},
     * {@link PayLogConstrants#STATUS_MASK_FAIL}.
     * @since 0.1.0
     */
    int getStepStatus();

    /**
     * 当前步骤所处状态.
     *
     * @param status 当前步骤所处状态.
     * @see {@link PayLogConstrants#STATUS_MASK_START},
     * {@link PayLogConstrants#STATUS_MASK_SUCCESS},
     * {@link PayLogConstrants#STATUS_MASK_EXCEPTION},
     * {@link PayLogConstrants#STATUS_MASK_FAIL}.
     * @since 0.1.0
     */
    void setStepStatus(int status);

    int getCause();

    void setCause(int cause);
}
