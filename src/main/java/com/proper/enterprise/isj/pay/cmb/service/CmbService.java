package com.proper.enterprise.isj.pay.cmb.service;

import com.proper.enterprise.isj.pay.cmb.document.CmbProtocolDocument;
import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.BusinessProReq;
import com.proper.enterprise.isj.pay.cmb.model.BusinessRes;
import com.proper.enterprise.isj.pay.cmb.model.RefundNoDupBodyReq;
import com.proper.enterprise.isj.pay.cmb.model.UnifiedOrderReq;
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

    PayResultRes getPrepayinfo(BasicInfoDocument basicInfo, UnifiedOrderReq uoReq) throws Exception;

    PayResultRes querySingleResult(CmbPayEntity payInfo) throws Exception;

    CmbRefundEntity saveRefundResult(RefundNoDupBodyReq refundInfo) throws Exception;

    CmbPayEntity getCmbPayNoticeInfo(HttpServletRequest request) throws Exception;

    CmbPayEntity getPayNoticeInfoByMsg(String msg) throws Exception;

    UnifiedOrderReq createOrderInfo(UnifiedOrderReq uoReq, String strXml) throws Exception;

    <T> String getOrderInfo(T t, Class<T> clz) throws Exception;

    String getTimeNo();

    String encrypt(String strSrc, String encName) throws Exception;

    String getOriginSign(String originSign);

    JSONObject getParamObj(String param) throws Exception;

    byte[] readStream(InputStream inStream) throws Exception;
}
