package com.proper.enterprise.isj.payment.logger.utils.codec;

/**
 * 需要使用分隔符的接口.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @see SimplePayLogDecoder
 * @since 0.1.0
 */
public interface WithSeparator {

    /**
     * 默认分隔符,",".
     *
     * @since 0.1.0
     */
    static String DEFAULT_SEPARATOR = ",";

    /**
     * 获得分隔符.
     *
     * @return 分隔符.
     * @since 0.1.0
     */
    String getSeparator();

    /**
     * 分隔符长度.
     *
     * @return 分隔符长度.
     * @since 0.1.0
     */
    int getSeparatorLength();
}
