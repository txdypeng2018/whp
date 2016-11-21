package com.proper.enterprise.isj.proxy.enums;

/**
 * Created by think on 2016/10/8 0008.
 */
public enum SendPushMsgEnum {

//    /**
//     * 挂号
//     */
//    REG_SUCCESS,
    /**
     * 挂号交费
     */
    REG_PAY_SUCCESS,
    /**
     * 超时退号
     */
    REG_OVERTIME_CANCEL,
//    /**
//     * 手动退号
//     */
//    REG_HANDLE_CANCEL,
    /**
     * 停诊
     */
    STOP_REG_PLATFORM,
    /**
     * 缴费
     */
    RECIPE_PAY_SUCCESS,
    /**
     * 挂号退费
     */
    REG_REFUND_SUCCESS,
    /**
     * 缴费退费成功
     */
    RECIPE_REFUND_SUCCESS,

    /**
     * 缴费退费失败
     */
    RECIPE_REFUND_FAIL,

    /**
     * 缴费退费失败
     */
    RECIPE_PAID_REFUND_FAIL,

    /**
     * 缴费失败退费
     */
    RECIPE_PAID_FAIL,
    /**
     * 当日挂号失败(HIS提示信息)
     */
    REG_PAY_HIS_RETURNMSG,

    /**
     * 当日挂号未缴费(提示信息)
     */
    REG_TODAY_NOT_PAY_HIS_MSG,

    /**
     * 当日挂号退费失败
     */
    REG_PAY_HIS_RETURNERR_MSG

}
