package com.proper.enterprise.isj.payment.logger.utils.codec;

/**
 * 分隔符包装类.
 * <p>
 * 本包中的 {@link SimplePayLogEncoder} 和 {@link SimplePayLogDecoder}均需要指明分隔符.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @see WithSeparator
 * @see DefaultWithSeparator
 * @see SimplePayLogEncoder
 * @see SimplePayLogDecoder
 * @since 0.1.0
 */
public class WithSeparatorWrapper implements WithSeparator {

    /**
     * 被包装的分隔符.
     * <p>
     * 默认为","。
     * </p>
     *
     * @since 0.1.0
     */
    private WithSeparator wrapperedSeparator = new DefaultWithSeparator();

    @Override
    public String getSeparator() {
        return wrapperedSeparator.getSeparator();
    }

    @Override
    public int getSeparatorLength() {
        return wrapperedSeparator.getSeparatorLength();
    }
}
