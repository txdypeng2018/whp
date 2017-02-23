package com.proper.enterprise.isj.payment.logger.entity;

/**
 * 日志内容.
 * <p>
 * 日志的主体部分.包括：业务对象的具体类型和业务对象的值(默认为JSON格式).
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface LogContent extends LogBase {
    /**
     * 业务对象的类型.
     *
     * @return 业务对象的类型.
     * @since 0.1.0
     */
    String getJavaType();

    /**
     * 业务对象的类型.
     *
     * @param javaType 业务对象的类型.
     * @since 0.1.0
     */
    void setJavaType(String javaType);

    /**
     * 业务对象的值.
     *
     * @return 业务对象的值.
     * @since 0.1.0
     */
    String getContent();

    /**
     * 业务对象的值.
     *
     * @param content 业务对象的值.
     * @since 0.1.0
     */
    void setContent(String content);
}
