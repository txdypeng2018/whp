package com.proper.enterprise.isj.payment.logger.utils.codec;

import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 支付日志的简单编码器.
 * <p>
 * 以","为分隔符记录支付步骤(4位十六进制整数),业务对象类型，业务对象JSON格式字符串.
 * </p>
 *
 * @param <T> 编码前数据类型.
 * @param <L> 编码后获得的支付日志类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
@Service
public class SimplePayLogEncoder<T, L extends PayLog<T>> extends WithSeparatorWrapper implements PayLogEncoder<T, L> {

    @Override
    public String encode(L source) {
        T bo = source.getBusinessObject();
        String type = bo.getClass().getName();
        String step = String.format("%04x", source.getStep());
        String cause = String.format("%08x", source.getCause());
        String jsonBo = "";
        try {
            jsonBo = JSONUtil.toJSON(bo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String sep = this.getSeparator();
        StringBuffer sb = new StringBuffer(12 + 2 * getSeparatorLength() + type.length() + jsonBo.length());
        sb.append(step).append(cause).append(sep).append(type).append(sep).append(jsonBo);
        return sb.toString();
    }

}
