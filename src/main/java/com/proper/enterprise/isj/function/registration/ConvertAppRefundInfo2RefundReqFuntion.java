package com.proper.enterprise.isj.function.registration;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.convertAppRefundInfo2RefundReq(Object,
 * String, String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class ConvertAppRefundInfo2RefundReqFuntion implements IFunction<RefundReq> {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj 支付平台异步通知对象.
     * @param orderNo 订单号.
     * @param refundId 退款流水号.
     * @return 转换后的请求对象.
     */
    public RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo, String refundId) {
        String hosId = CenterFunctionUtils.getHosId();
        RefundReq refundReq = new RefundReq();
        refundReq.setHosId(hosId);
        // int fee = 0;
        Order order = toolkitx.executeRepositoryFunction(OrderRepository.class, "findByOrderNo", orderNo);
        if (orderNo != null) {
            RegistrationDocument reg = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                    order.getFormId());
            if (reg != null) {
                refundReq.setOrderId(reg.getOrderNum());
                refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                refundReq.setRefundId(refundId);
                refundReq.setRefundResDesc("");
                refundReq.setRefundRemark("");
                // 支付宝退款响应信息
                if (infoObj instanceof AliRefundRes) {
                    AliRefundRes refund = (AliRefundRes) infoObj;
                    refundReq.setRefundSerialNum(refund.getTradeNo());
                    BigDecimal bigDecimal = new BigDecimal(refund.getRefundFee());
                    bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                    refundReq.setTotalFee(bigDecimal.intValue());
                    refundReq.setRefundFee(bigDecimal.intValue());
                    refundReq.setRefundDate(refund.getGmtRefundPay().split(" ")[0]);
                    refundReq.setRefundTime(refund.getGmtRefundPay().split(" ")[1]);
                    if (StringUtil.isNotEmpty(refund.getMsg())) {
                        refundReq.setRefundResCode(refund.getMsg());
                    } else {
                        refundReq.setRefundResCode("");
                    }
                    // 微信退款响应信息
                } else if (infoObj instanceof WechatRefundRes) {
                    WechatRefundRes refund = (WechatRefundRes) infoObj;
                    refundReq.setRefundSerialNum(refund.getTransactionId());
                    refundReq.setTotalFee(Integer.parseInt(refund.getTotalFee()));
                    refundReq.setRefundFee(Integer.parseInt(refund.getTotalFee()));
                    refundReq.setRefundDate(DateUtil.toDateString(new Date()));
                    refundReq.setRefundTime(DateUtil.toTimestamp(new Date(), false).split(" ")[1]);
                    if (StringUtil.isNotEmpty(refund.getNonceStr())) {
                        refundReq.setRefundResCode(refund.getNonceStr());
                    } else {
                        refundReq.setRefundResCode("");
                    }
                    // 招行退款响应信息
                } else if (infoObj instanceof CmbRefundNoDupRes) {
                    CmbRefundNoDupRes refund = (CmbRefundNoDupRes) infoObj;
                    // 银行流水号
                    refundReq.setRefundSerialNum(refund.getBody().getBankSeqNo());
                    BigDecimal bigDecimal = new BigDecimal(refund.getBody().getAmount());
                    bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                    refundReq.setTotalFee(bigDecimal.intValue());
                    refundReq.setRefundFee(bigDecimal.intValue());
                    // 日期
                    String date = DateUtil.toString(DateUtil.toDate(refund.getBody().getDate(), "yyyyMMdd"),
                            "yyyy-MM-dd");
                    // 时间
                    String time = DateUtil.toString(DateUtil.toDate(refund.getBody().getTime(), "HHmmss"), "HH:mm:ss");
                    refundReq.setRefundDate(date);
                    refundReq.setRefundTime(time);
                    refundReq.setRefundResCode(refund.getHead().getErrMsg());
                }
            }
        }
        return refundReq;
    }

    @Override
    public RefundReq execute(Object... params) throws Exception {
        return convertAppRefundInfo2RefundReq(params[0], (String) params[1], (String) params[2]);
    }

}
