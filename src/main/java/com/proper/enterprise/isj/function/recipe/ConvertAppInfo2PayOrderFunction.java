package com.proper.enterprise.isj.function.recipe;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.wechat.entity.WechatEntity;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.convertAppInfo2PayOrder(Order, Object)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class ConvertAppInfo2PayOrderFunction implements IFunction<PayOrderReq>, ILoggable{
    

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkit;

    @Override
    public PayOrderReq execute(Object... params) throws Exception {
        return convertAppInfo2PayOrder((Order)params[0], params[1]);
    }
    
    /**
     * 将APP转化为向HIS请求对象.
     *
     * @param order 订单信息.
     * @param infoObj 支付对象.
     * @return 向HIS请求对象.
     */
    public PayOrderReq convertAppInfo2PayOrder(Order order, Object infoObj) {
        debug("将订单转换成缴费参数对象传递给HIS,订单号:{}", order.getOrderNo());
        PayOrderReq payOrderReq;
        int fee;
        try {
            RecipeOrderDocument recipeOrder = toolkit.executeRepositoryFunction(RecipeOrderRepository.class, "findOne", order.getFormId().split("_")[0]);
            
            String hosId = CenterFunctionUtils.getHosId();
            RecipePaidDetailDocument nonPaid = recipeOrder.getRecipeNonPaidDetail();
            BigDecimal totalFee = new BigDecimal(nonPaid.getAmount());
            payOrderReq = new PayOrderReq();
            payOrderReq.setHosId(hosId);
            payOrderReq.setHospClinicCode(recipeOrder.getClinicCode());
            payOrderReq.setHospSequence(nonPaid.getHospSequence());
            fee = totalFee.intValue();
            if (infoObj instanceof AliEntity) {
                AliEntity aliEntity = (AliEntity) infoObj;
                payOrderReq.setOrderId(aliEntity.getOutTradeNo());
                payOrderReq.setSerialNum(aliEntity.getTradeNo());
                payOrderReq.setPayDate(aliEntity.getNotifyTime().split(" ")[0]);
                payOrderReq.setPayTime(aliEntity.getNotifyTime().split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.ALIPAY);
                payOrderReq.setPayResCode(aliEntity.getTradeStatus());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(aliEntity.getBuyerId());
            } else if (infoObj instanceof AliPayTradeQueryRes) {
                AliPayTradeQueryRes aliPayQuery = (AliPayTradeQueryRes) infoObj;
                payOrderReq.setOrderId(aliPayQuery.getOutTradeNo());
                payOrderReq.setSerialNum(aliPayQuery.getTradeNo());
                payOrderReq.setPayDate(aliPayQuery.getSendPayDate().split(" ")[0]);
                payOrderReq.setPayTime(aliPayQuery.getSendPayDate().split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.ALIPAY);
                payOrderReq.setPayResCode(aliPayQuery.getTradeStatus());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(aliPayQuery.getBuyerLogonId());
            } else if (infoObj instanceof WechatEntity) {
                WechatEntity weixinEntity = (WechatEntity) infoObj;
                payOrderReq.setOrderId(weixinEntity.getOutTradeNo());
                payOrderReq.setSerialNum(weixinEntity.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinEntity.getTimeEnd(), "yyyyMMddHHmmss");
                payOrderReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payOrderReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.WECHATPAY);
                payOrderReq.setPayResCode(weixinEntity.getResultCode());
                payOrderReq.setMerchantId(weixinEntity.getMchId());
                payOrderReq.setTerminalId(weixinEntity.getDeviceInfo());
                payOrderReq.setPayAccount(weixinEntity.getAppid());
            } else if (infoObj instanceof WechatPayQueryRes) {
                WechatPayQueryRes weixinPayQuery = (WechatPayQueryRes) infoObj;
                payOrderReq.setOrderId(weixinPayQuery.getOutTradeNo());
                payOrderReq.setSerialNum(weixinPayQuery.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinPayQuery.getTimeEnd(), "yyyyMMddHHmmss");
                payOrderReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payOrderReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.WECHATPAY);
                payOrderReq.setPayResCode(weixinPayQuery.getResultCode());
                payOrderReq.setMerchantId(weixinPayQuery.getMchId());
                payOrderReq.setTerminalId(weixinPayQuery.getDeviceInfo());
                payOrderReq.setPayAccount(weixinPayQuery.getAppid());
            } else if (infoObj instanceof CmbPayEntity) {
                CmbPayEntity cmbEntity = (CmbPayEntity) infoObj;
                payOrderReq.setOrderId(order.getOrderNo());
                // 支付信息
                String account = cmbEntity.getMsg();
                payOrderReq.setSerialNum(account.substring(account.length() - 20, account.length()));
                // 交易日期
                String date = DateUtil.toString(DateUtil.toDate(cmbEntity.getDate(), "yyyyMMdd"), "yyyy-MM-dd");

                payOrderReq.setPayDate(date);
                payOrderReq.setPayTime(cmbEntity.getTime());
                payOrderReq.setPayChannelId(PayChannel.WEB_UNION);
                payOrderReq.setPayResCode(cmbEntity.getSucceed());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(cmbEntity.getBillNo());
            } else if (infoObj instanceof CmbQuerySingleOrderRes) {
                CmbQuerySingleOrderRes cmbPayQuery = (CmbQuerySingleOrderRes) infoObj;
                payOrderReq.setOrderId(order.getOrderNo());
                payOrderReq.setSerialNum(cmbPayQuery.getBody().getBankSeqNo());
                // 日期
                String date = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptDate(), "yyyyMMdd"),
                        "yyyy-MM-dd");
                // 时间
                String time = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptTime(), "HHmmss"),
                        "HH:mm:ss");
                payOrderReq.setPayDate(date);
                payOrderReq.setPayTime(time);
                payOrderReq.setPayChannelId(PayChannel.WEB_UNION);
                payOrderReq.setPayResCode(cmbPayQuery.getHead().getCode());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(cmbPayQuery.getBody().getBillNo());
            }
            payOrderReq.setBankNo("");
            payOrderReq.setPayResDesc("");
            payOrderReq.setPayTotalFee(fee);
            payOrderReq.setPayBehooveFee(fee);
            payOrderReq.setPayActualFee(fee);
            payOrderReq.setPayMiFee(0);
            payOrderReq.setOperatorId("");
            payOrderReq.setReceiptId("");
        } catch (Exception e) {
            debug("将订单转换成缴费参数对象异常,订单号:{}{}", order.getOrderNo(), e);
            payOrderReq = null;
        }
        return payOrderReq;
    }


}
