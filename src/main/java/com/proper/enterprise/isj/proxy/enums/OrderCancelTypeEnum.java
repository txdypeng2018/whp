package com.proper.enterprise.isj.proxy.enums;

/**
 * Created by think on 2016/10/1 0001.
 */
public enum OrderCancelTypeEnum {

    /**
     * 手动取消
     */
    CANCEL_HANDLE,
    /**
     * 超时取消
     */
    CANCEL_OVERTIME,
    /**
     * 系统异常,平台取消
     */
    CANCEL_PLATFORM_ERR,

    /**
     * 医院停诊
     */
    CANCEL_HOSPITAL_STOPREG

}
