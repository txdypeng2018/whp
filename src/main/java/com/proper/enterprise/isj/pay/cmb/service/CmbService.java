package com.proper.enterprise.isj.pay.cmb.service;

import com.proper.enterprise.isj.pay.cmb.document.CmbProtocolDocument;
import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbQueryRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.*;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.wideunique.utils.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * 招商银行支付Service
 */
public interface CmbService {

    boolean saveUserProtocolNo(String userId, BusinessProReq businessReq) throws Exception;

    CmbProtocolDocument getUserProtocolInfo(String userId) throws Exception;

    void saveUserProtocolInfo(CmbProtocolDocument userProtocolInfo) throws Exception;

    void saveBusinessInfo(String userId, JSONObject reqObject, BusinessRes businessInfo) throws Exception;

    void saveCmbPayNoticeInfo(CmbPayEntity payInfo) throws Exception;

    PayResultRes savePrepayinfo(BasicInfoDocument basicInfo, UnifiedOrderReq uoReq) throws Exception;

    boolean saveNoticeProtocolInfo(String reqData) throws Exception;

    boolean saveNoticePayInfo(CmbPayEntity cmbInfo) throws Exception;

    CmbPayEntity getQueryInfo(String orderNo) throws Exception;

    PayResultRes querySingleOrder(String orderNo) throws Exception;

    QuerySingleOrderRes getCmbPayQueryRes(String orderNo) throws Exception;

    QueryRefundRes queryRefundResult(CmbQueryRefundEntity queryRefundInfo) throws Exception;

    RefundNoDupRes saveRefundResult(RefundNoDupBodyReq refundInfo) throws Exception;

    CmbPayEntity getCmbPayNoticeInfo(HttpServletRequest request) throws Exception;

    CmbPayEntity getPayNoticeInfoByMsg(String msg) throws Exception;

    UnifiedOrderReq createOrderInfo(UnifiedOrderReq uoReq, String strXml) throws Exception;

    <T> String getOrderInfo(T t, Class<T> clz) throws Exception;

    String getTimeNo();

    String encrypt(String strSrc, String encName) throws Exception;

    String getOriginSign(String originSign);

    JSONObject getParamObj(String param) throws Exception;

    byte[] readStream(InputStream inStream) throws Exception;

    CmbPayEntity getNoticePayInfoByOrderNo(String orderNo) throws Exception;
}
