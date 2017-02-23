package com.proper.enterprise.isj.payment.logger.utils.codec;

import org.apache.commons.lang3.StringUtils;

/**
 * 默认分隔符类.
 * <p>
 * 此类一般与{@link WithSeparatorWrapper}协同使用.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class DefaultWithSeparator implements WithSeparator {
    /**
     * 分隔符.
     *
     * @since 0.1.0
     */
    private String separator = DEFAULT_SEPARATOR;

    /**
     * 分隔符长度.
     *
     * @since 0.1.0
     */
    private int length = 1;

    @Override
    public String getSeparator() {
        return separator;
    }

    /**
     * 设置分隔符.
     * <p>
     * 该方法自动重新计算{@link #getSeparatorLength()}的结果值. 当分隔符为空时，长度为0.
     * </p>
     *
     * @param separator 分隔符.
     * @see #length
     * @since 0.1.0
     */
    public void setSeparator(String separator) {
        this.separator = separator;
        if (StringUtils.isNoneBlank(this.separator)) {
            length = this.separator.length();
        } else {
            length = 0;
        }
    }

    @Override
    public int getSeparatorLength() {
        return length;
    }
}
