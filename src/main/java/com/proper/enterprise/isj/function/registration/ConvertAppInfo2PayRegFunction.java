package com.proper.enterprise.isj.function.registration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.wechat.entity.WechatEntity;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;

@Service
public class ConvertAppInfo2PayRegFunction implements IFunction<PayRegReq>, ILoggable{


    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Override
    public PayRegReq execute(Object... params) throws Exception {
        return convertAppInfo2PayReg(params[0], (String)params[1]);
    }
    
    
    public PayRegReq convertAppInfo2PayReg(Object infoObj, String regId) {
        // HIS请求对象
        PayRegReq payRegReq = new PayRegReq();
        // 医院ID
        payRegReq.setHosId(CenterFunctionUtils.getHosId());
        // 取得挂号单信息
        RegistrationDocument reg = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne", regId);
        // 如果未获取到挂号单信息则返回null
        if (reg == null) {
            return null;
        }
        try {
            // HIS请求参数金额(以分为单位)
            int fee = Integer.parseInt(reg.getAmount());
            payRegReq.setOrderId(reg.getOrderNum());
            // 支付宝异步通知保存对象
            if (infoObj instanceof AliEntity) {
                AliEntity aliEntity = (AliEntity) infoObj;
                payRegReq.setSerialNum(aliEntity.getTradeNo());
                payRegReq.setPayDate(aliEntity.getNotifyTime().split(" ")[0]);
                payRegReq.setPayTime(aliEntity.getNotifyTime().split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.ALIPAY.getCode()));
                payRegReq.setPayResCode(aliEntity.getTradeStatus());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(aliEntity.getBuyerId());
                // 支付宝单笔订单查询结果对象
            } else if (infoObj instanceof AliPayTradeQueryRes) {
                AliPayTradeQueryRes payTradeQuery = (AliPayTradeQueryRes) infoObj;
                payRegReq.setSerialNum(payTradeQuery.getTradeNo());
                payRegReq.setPayDate(payTradeQuery.getSendPayDate().split(" ")[0]);
                payRegReq.setPayTime(payTradeQuery.getSendPayDate().split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.ALIPAY.getCode()));
                payRegReq.setPayResCode(payTradeQuery.getTradeStatus());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(payTradeQuery.getBuyerUserId());
                // 微信异步通知保存对象
            } else if (infoObj instanceof WechatEntity) {
                WechatEntity weixinEntity = (WechatEntity) infoObj;
                payRegReq.setSerialNum(weixinEntity.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinEntity.getTimeEnd(), "yyyyMMddHHmmss");
                payRegReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payRegReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WECHATPAY.getCode()));
                payRegReq.setPayResCode(weixinEntity.getResultCode());
                payRegReq.setMerchantId(weixinEntity.getMchId());
                payRegReq.setTerminalId(weixinEntity.getDeviceInfo());
                payRegReq.setPayAccount(weixinEntity.getAppid());
                // 微信单笔订单查询结果对象
            } else if (infoObj instanceof WechatPayQueryRes) {
                WechatPayQueryRes weixinPayQuery = (WechatPayQueryRes) infoObj;
                payRegReq.setSerialNum(weixinPayQuery.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinPayQuery.getTimeEnd(), "yyyyMMddHHmmss");
                payRegReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payRegReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WECHATPAY.getCode()));
                payRegReq.setPayResCode(weixinPayQuery.getResultCode());
                payRegReq.setMerchantId(weixinPayQuery.getMchId());
                payRegReq.setTerminalId(weixinPayQuery.getDeviceInfo());
                payRegReq.setPayAccount(weixinPayQuery.getAppid());
                // 一网通异步通知保存对象
            } else if (infoObj instanceof CmbPayEntity) {
                CmbPayEntity cmbEntity = (CmbPayEntity) infoObj;
                // 支付信息
                String account = cmbEntity.getMsg();
                // 一网通20位银行流水号
                payRegReq.setSerialNum(account.substring(account.length() - 20, account.length()));
                // 交易日期
                String date = DateUtil.toString(DateUtil.toDate(cmbEntity.getDate(), "yyyyMMdd"), "yyyy-MM-dd");
                payRegReq.setPayDate(date);
                payRegReq.setPayTime(cmbEntity.getTime());
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WEB_UNION.getCode()));
                payRegReq.setPayResCode(cmbEntity.getSucceed());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(cmbEntity.getBillNo());
                // 一网通单笔订单查询结果对象
            } else if (infoObj instanceof CmbQuerySingleOrderRes) {
                CmbQuerySingleOrderRes cmbPayQuery = (CmbQuerySingleOrderRes) infoObj;
                payRegReq.setSerialNum(cmbPayQuery.getBody().getBankSeqNo());
                // 日期
                String date = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptDate(), "yyyyMMdd"),
                        "yyyy-MM-dd");
                // 时间
                String time = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptTime(), "HHmmss"),
                        "HH:mm:ss");
                payRegReq.setPayDate(date);
                payRegReq.setPayTime(time);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WEB_UNION.getCode()));
                payRegReq.setPayResCode(cmbPayQuery.getHead().getCode());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(cmbPayQuery.getBody().getBillNo());
            }
            payRegReq.setBankNo("");
            payRegReq.setPayResDesc("");
            payRegReq.setPayTotalFee(fee);
            payRegReq.setPayCopeFee(fee);
            payRegReq.setPayFee(fee);
            payRegReq.setOperatorId(CenterFunctionUtils.convertDistrictId2OperatorId(reg.getDistrictId()));
        } catch (Exception e) {
            debug("挂号缴费参数转换成HIS需要的参数时异常", e);
            throw e;
        }
        return payRegReq;
    }

}
