package com.proper.enterprise.isj.pay.cmb.model;

import com.proper.enterprise.isj.pay.cmb.constants.CmbConstants;

import java.io.Serializable;

/**
 * 招商银行支付请求对象
 */
public class UnifiedOrderReq implements Serializable {

    /**
     * (必填)_支付商户开户分行号，4位，请咨询开户的招商银行分支机构
     */
    private String branchID = CmbConstants.CMB_PAY_BRANCHID;

    /**
     * (必填)_支付商户号，6位长数字，由银行在商户开户时确定；//收单商户号
     */
    private String cono = CmbConstants.CMB_PAY_CONO;

    /**
     * (必填)_定单号，6位或10位长数字，由商户系统生成，一天内不能重复；
     */
    private String billNo;

    /**
     * (必填)_定单总金额，格式为：xxxx.xx元；
     */
    private String amount;

    /**
     * (必填)_格式：YYYYMMDD
     * 向银行系统发起支付请求的实际日期，一般为系统当前日期。与实际时间只能有一个小时误差。
     * 日期不能跨天。如果交易日期为前一天，则要求截至时间为凌晨1点钟。如果交易日期为后一天，当前时间必须为23点以后
     */
    private String date;

    /**
     * (选填)_必须为大于零的整数，单位为分钟。该参数指定当前支付请求必须在指定时间跨度内完成（从系统收到支付请求开始计时），
     * 否则按过期处理。该参数适用于航空客票等对交易完成时间敏感的支付请求；
     * 注意：
     * 1、	由于系统有会话超时保护机制，因此实际订单有效时间受该参数和会话有效时间（目前是30分钟）约束，取值较小者有效。
     * 2、	如果客户在商户端对同一笔订单重新发起支付请求，则以商户在后一次发起时指定的参数为准。
     */
    private String expireTimeSpan;

    /**
     * (必填)_支付结果通知命令中参数之前的部分，长度不能超过128个字节。
     * 例如： http://www.merchant.com/path/WAPProcResult.dll
     * 注意：MerchantUrl自身不能带商户参数。
     */
    private String merchantUrl;

    /**
     * (选填)_商户需要银行在支付结果通知中转发的商户参数；
     * 注意：MerchantPara参数可为空，商户如果需要不止一个参数，
     * 可以自行把参数组合、拼装，但组合后的结果不能带有’&amp;’字符，总长不能超过128个字节。
     * 例如：
     * MerchantPara=Ref1=12345678|Ref2=ABCDEFG|Ref3=HIJKLM
     */
    private String merchantPara;

    /**
     * (必填)_这一字段的内容由商户校验码的算法生成，填写内容及生成算法请详见“商户校验码”部分的说明。
     */
    private String merchantCode;

    /**
     * (必填)_支付完成后，系统提供返回商户功能，跳转到MerchantRetUrl与MerchantRetPara所指定的地址。
     * 例如：
     * MerchantRetUrl = http://www.merchant.com/path/BacktoMerchant.aspx
     * MerchantRetPara = Para1=abc|Para2=123
     * 则，点击“返回按钮”，客户浏览器将跳转到：
     * http://www.merchant.com/path/BacktoMerchant.aspx?Para1=abc|Para2=123
     * 注意：MerchantRetPara参数可为空，商户如果需要不止一个参数，
     * 可以自行把参数组合、拼装，但组合后的结果不能带有’&amp;’字符，总长不能超过128个字节
     */
    private String merchantRetUrl;

    /**
     * (选填)_详见MerchantRetUrl中的说明
     */
    private String merchantRetPara;

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public String getCono() {
        return cono;
    }

    public void setCono(String cono) {
        this.cono = cono;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExpireTimeSpan() {
        return expireTimeSpan;
    }

    public void setExpireTimeSpan(String expireTimeSpan) {
        this.expireTimeSpan = expireTimeSpan;
    }

    public String getMerchantUrl() {
        return merchantUrl;
    }

    public void setMerchantUrl(String merchantUrl) {
        this.merchantUrl = merchantUrl;
    }

    public String getMerchantPara() {
        return merchantPara;
    }

    public void setMerchantPara(String merchantPara) {
        this.merchantPara = merchantPara;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantRetUrl() {
        return merchantRetUrl;
    }

    public void setMerchantRetUrl(String merchantRetUrl) {
        this.merchantRetUrl = merchantRetUrl;
    }

    public String getMerchantRetPara() {
        return merchantRetPara;
    }

    public void setMerchantRetPara(String merchantRetPara) {
        this.merchantRetPara = merchantRetPara;
    }
}
