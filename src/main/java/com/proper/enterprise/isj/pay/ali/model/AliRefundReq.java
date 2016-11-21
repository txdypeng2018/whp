package com.proper.enterprise.isj.pay.ali.model;

import com.proper.enterprise.isj.pay.ali.constants.AliConstants;

public class AliRefundReq {
    /**
     * 接口名称。 必填
     */
    private String service = AliConstants.ALI_PAY_TRADE_REFUND;

    /**
     * 合作者身份ID 必填
     */
    private String partner = AliConstants.ALI_PAY_PARTNER_ID;

    /**
     * 参数编码字符集 必填
     */
    private String inputCharset = AliConstants.ALI_PAY_INPUT_CHARSET;

    /**
     * 服务器异步通知页面路径
     */
    private String notifyUrl = AliConstants.ALI_PAY_NOTICE_URL;

    /**
     * 卖家支付宝账号 必填 (卖家支付宝账号与卖家用户ID两者必填其中一个)
     */
    private String sellerEmail = AliConstants.ALI_PAY_SELLER_ID;

    /**
     * 	卖家用户ID 必填 (卖家支付宝账号与卖家用户ID两者必填其中一个)
     */
    private String sellerUserId;

    /**
     * 退款请求的当前时间。格式为：yyyy-MM-dd HH:mm:ss。  必填
     */
    private String refundDate;

    /**
     * 退款批次号  必填
     *
     * 每进行一次即时到账批量退款，都需要提供一个批次号，通过该批次号可以查询这一批次的退款交易记录，
     * 对于每一个合作伙伴，传递的每一个批次号都必须保证唯一性。格式为：退款日期（8位）+流水号（3～24位）。
     * 不可重复，且退款日期必须是当天日期。流水号可以接受数字或英文字符，建议使用数字，但不可接受“000”。
     */
    private String batchNo;

    /**
     * 总笔数  必填
     *
     * 即参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的最大数量为999个）。
     */
    private String batchNum = "1";

    /**
     * 	单笔数据集
     *
     单笔数据集参数说明
     单笔数据集格式为：第一笔交易退款数据集#第二笔交易退款数据集#第三笔交易退款数据集…#第N笔交易退款数据集；
     交易退款数据集的格式为：原付款支付宝交易号^退款总金额^退款理由；
     不支持退分润功能。
     单笔数据集（detail_data）注意事项
     detail_data中的退款笔数总和要等于参数batch_num的值；
     “退款理由”长度不能大于256字节，“退款理由”中不能有“^”、“|”、“$”、“#”等影响detail_data格式的特殊字符；
     detail_data中退款总金额不能大于交易总金额；
     一笔交易可以多次退款，退款次数最多不能超过99次，需要遵守多次退款的总金额不超过该笔交易付款金额的原则。
     */
    private String detailData;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String refundDate) {
        this.refundDate = refundDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getDetailData() {
        return detailData;
    }

    public void setDetailData(String detailData) {
        this.detailData = detailData;
    }
}
