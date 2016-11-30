package com.proper.enterprise.isj.pay.cmb.controller;

import cmb.netpayment.Security;
import com.cmb.b2b.B2BResult;
import com.cmb.b2b.Base64;
import com.cmb.b2b.FirmbankCert;
import com.proper.enterprise.isj.pay.cmb.document.CmbProtocolDocument;
import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.BusinessRes;
import com.proper.enterprise.isj.pay.cmb.model.RefundNoDupBodyReq;
import com.proper.enterprise.isj.pay.cmb.model.UnifiedOrderReq;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.wideunique.utils.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Map;

/**
 * 招商银行一网通支付Controller
 */
@RestController
@RequestMapping(value = "/pay/cmb")
public class CmbPayController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmbPayController.class);

    @Autowired
    Marshaller marshaller;

    @Autowired
    Map<String, Unmarshaller> unmarshallerMap;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    CmbService cmbService;

    /**
     * 一网通预支付信息
     *
     * @param uoReq
     *        一网通预支付信息
     * @return PayResultRes
     * @throws Exception
     */
    @PostMapping(value = "/prepayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> getPrepayinfo(@RequestBody UnifiedOrderReq uoReq) throws Exception {
        PayResultRes resObj = new PayResultRes();
        try {
            // 需要先进行查询,查询用户信息绑定的协议号,如果没有签署协议则需要生成新的协议号 TODO
            User currentUser = userService.getCurrentUser();
            BasicInfoDocument basicInfo = userInfoService.getUserInfoByUserId(currentUser.getId());
            resObj = cmbService.getPrepayinfo(basicInfo, uoReq);
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.getPrepayinfo[Exception]:", e);
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
            resObj.setResultCode("-1");
        }

        return responseOfPost(resObj);
    }

    /**
     * 一网通签约异步通知
     *
     * @param request
     *        请求
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @PostMapping(value = "/noticeProtocolInfo")
    public ResponseEntity<String> dealNoticeProtocolInfo(HttpServletRequest request) throws Exception {
        LOGGER.debug("-----------一网通签约异步通知------开始---------------");
        // 返回给一网通服务器的异步通知结果
        boolean ret = false;
        try {
            // 获取一网通POST过来反馈信息
            // 唯一的元素
            String reqData = request.getParameter("RequestData");
            //接收方需要兼容可能早期版本发送的未作URLEncoder.encode的方式
            reqData = reqData.replace(' ', '+');
            LOGGER.debug("cmb_reqData:" + reqData);
            JSONObject reqObject = new JSONObject(reqData);
            // 企业网银公钥BASE64字符串 TODO 切换正式环境需要进行替换?
            String sBase64PubKey = ConfCenter.get("isj.pay.cmb.publickey");

            // 初始化公钥,验证签名
            B2BResult bRet = FirmbankCert.initPublicKey(sBase64PubKey);
            if (!bRet.isError()) {
                LOGGER.debug("验签成功!");
                // 业务数据包：报文数据必须经过base64编码。
                String busdat = reqObject.getString("BUSDAT");
                byte[] bt = Base64.decode(busdat);
                BusinessRes res = (BusinessRes) unmarshallerMap.get("unmarshallBusinessRes")
                        .unmarshal(new StreamSource(new ByteArrayInputStream(bt)));
                // 取得用户请求的参数
                JSONObject paramObj = cmbService.getParamObj(res.getNoticepara());
                // 获取用户ID以及协议号
                String userId = paramObj.getString("userid");
                String pno = paramObj.getString("pno");
                // 获取用户协议信息
                CmbProtocolDocument protocolInfo = cmbService.getUserProtocolInfo(userId);
                // 用户协议信息不为空
                if (protocolInfo != null) {
                    // 用户没有签约过或者签约失败
                    if (protocolInfo.getSign().equals(ConfCenter.get("isj.pay.cmb.protocolResFail"))) {
                        // 如果签约成功
                        if (ConfCenter.get("isj.pay.cmb.signSuccess").equals(res.getRespcod())
                                && pno.equals(res.getCustArgno())) {
                            // 更新协议信息
                            // 协议号
                            protocolInfo.setProtocolNo(res.getCustArgno());
                            // 协议状态
                            protocolInfo.setSign(ConfCenter.get("isj.pay.cmb.protocolResSuccess"));
                            cmbService.saveUserProtocolInfo(protocolInfo);
                            res.setRespmsg("protocol sign success");
                            // 保存异步通知信息
                            cmbService.saveBusinessInfo(userId, reqObject, res);
                            ret = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.dealNoticeProtocolInfo[Exception]:", e);
            throw e;
        }
        if (ret) {
            LOGGER.debug("-----------一网通签约异步通知------正常结束---------------");
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } else {
            LOGGER.debug("-----------一网通签约异步通知:处理过或者无效的协议信息------结束-------------------");
            return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 一网通支付结果异步通知
     *
     * @param request
     *        请求
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @GetMapping(value = "/noticePayInfo")
    public ResponseEntity<String> dealNoticePayInfo(HttpServletRequest request) throws Exception {
        LOGGER.debug("-----------一网通支付结果异步通知------开始---------------");
        boolean ret = false;
        try {
            // 获取从银行返回的信息
            String queryStr = request.getQueryString();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("cmbkey/public.key");
            // 构造方法
            Security cmbSecurity = new Security(cmbService.readStream(inputStream));
            // 检验数字签名
            if (cmbSecurity.checkInfoFromBank(queryStr.getBytes("GB2312"))) {
                LOGGER.debug("验签成功");
                // 取得一网通支付结果异步通知对象
                CmbPayEntity payInfo = cmbService.getCmbPayNoticeInfo(request);
                // 取得支付时传入的参数
                JSONObject paramObj = cmbService.getParamObj(payInfo.getMerchantPara());
                // 获取用户ID
                String userId = paramObj.getString("userid");
                payInfo.setUserId(userId);
                // 获取异步通知信息
                CmbPayEntity queryPanInfo = cmbService.getPayNoticeInfoByMsg(payInfo.getMsg());
                if (queryPanInfo == null) {
                    // 保存异步通知信息
                    cmbService.saveCmbPayNoticeInfo(payInfo);
                }
                // TODO 业务逻辑,更新订单状态等等
                ret = true;
            }
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.dealNoticePayInfo[Exception]:", e);
            throw e;
        }
        if (ret) {
            LOGGER.debug("-----------一网通支付结果异步通知------正常结束---------------");
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } else {
            LOGGER.debug("-----------一网通支付结果异步通知:处理过的信息------结束-------------------");
            return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 查询一网通单笔交易支付结果
     *
     * @param payInfo
     *        支付信息
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore //TODO 临时
    @PostMapping(value = "/querySinglePayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> querySinglePayInfo(@RequestBody CmbPayEntity payInfo) throws Exception {
        PayResultRes resObj = new PayResultRes();
        try {
            resObj = cmbService.querySingleResult(payInfo);
        } catch (Exception e) {
            e.printStackTrace();
            resObj.setResultCode("-1");
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return responseOfPost(resObj);
    }

    /**
     * 查询一网退款接口_暂时 // TODO 临时测试接口,需要删除
     *
     * @param refundInfo
     *        退款
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @PostMapping(value = "/refundPayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CmbRefundEntity> refundPayInfo(@RequestBody RefundNoDupBodyReq refundInfo) throws Exception {
        CmbRefundEntity resObj = new CmbRefundEntity();
        try {
            // 设定退款金额
            refundInfo.setAmount("0.01");
            // 设定退款流水号
            refundInfo.setRefundNo(RandomStringUtils.randomNumeric(20));
            resObj = cmbService.saveRefundResult(refundInfo);
        } catch (Exception e) {
            e.printStackTrace();
            resObj.setRefundCode("-1");
        }
        return responseOfPost(resObj);
    }
}
