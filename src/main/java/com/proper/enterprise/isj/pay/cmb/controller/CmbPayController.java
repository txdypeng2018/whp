package com.proper.enterprise.isj.pay.cmb.controller;

import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbQueryRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.QueryRefundRes;
import com.proper.enterprise.isj.pay.cmb.model.RefundNoDupBodyReq;
import com.proper.enterprise.isj.pay.cmb.model.RefundNoDupRes;
import com.proper.enterprise.isj.pay.cmb.model.UnifiedOrderReq;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.proxy.tasks.CmbPayNotice2BusinessTask;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
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

    @Autowired
    private CmbPayNotice2BusinessTask cmbPayNoticeTask;

    /**
     * 一网通预支付信息
     *
     * @param uoReq 一网通预支付信息
     * @return PayResultRes
     * @throws Exception
     */
    @PostMapping(value = "/prepayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> getPrepayinfo(@RequestBody UnifiedOrderReq uoReq) throws Exception {
        PayResultRes resObj = new PayResultRes();
        try {
            // 需要先进行查询,查询用户信息绑定的协议号,如果没有签署协议则需要生成新的协议号
            User currentUser = userService.getCurrentUser();
            BasicInfoDocument basicInfo = userInfoService.getUserInfoByUserId(currentUser.getId());
            resObj = cmbService.savePrepayinfo(basicInfo, uoReq);
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
     * @param request 请求
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
            ret = cmbService.saveNoticeProtocolInfo(reqData);
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
     * @param request 请求
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @GetMapping(value = "/noticePayInfo")
    public ResponseEntity<String> dealNoticePayInfo(HttpServletRequest request) throws Exception {
        LOGGER.debug("-----------一网通支付结果异步通知------开始---------------");
        // 获取从银行返回的信息
        String queryStr = request.getQueryString();
        // 检验数字签名
        if (!cmbService.isValid(queryStr)) {
            LOGGER.debug("验签失败！{}", queryStr);
            LOGGER.debug("-----------一网通支付结果异步通知:处理过的信息------结束-------------------");
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        }

        LOGGER.debug("验签成功！{}", queryStr);
        // 取得一网通支付结果异步通知对象
        CmbPayEntity cmbInfo = cmbService.getCmbPayNoticeInfo(request);
        cmbPayNoticeTask.run(cmbInfo);
        LOGGER.debug("-----------一网通支付结果异步通知------正常结束---------------");
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    /**
     * 查询一网通单笔交易支付结果
     *
     * @param payInfo 支付信息
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @PostMapping(value = "/querySinglePayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> querySinglePayInfo(@RequestBody CmbPayEntity payInfo) throws Exception {
        PayResultRes resObj = new PayResultRes();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(payInfo.getDate()).append(payInfo.getBillNo());
            resObj = cmbService.querySingleOrder(sb.toString());
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.querySinglePayInfo[Exception]:", e);
            resObj.setResultCode("-1");
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return responseOfPost(resObj);
    }

    /**
     * 查询一网退款接口
     *
     * @param refundInfo 退款
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @PostMapping(value = "/refundPayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RefundNoDupRes> refundPayInfo(@RequestBody RefundNoDupBodyReq refundInfo) throws Exception {
        RefundNoDupRes resObj = new RefundNoDupRes();
        try {
            // 设定退款流水号
            refundInfo.setRefundNo(RandomStringUtils.randomNumeric(20));
            resObj = cmbService.saveRefundResult(refundInfo);
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.refundPayInfo[Exception]:", e);
        }
        return responseOfPost(resObj);
    }

    /**
     * 查询一网退款查询接口
     *
     * @param queryRefundInfo 退款查询信息
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @PostMapping(value = "/queryRefundInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QueryRefundRes> queryRefundInfo(@RequestBody CmbQueryRefundEntity queryRefundInfo) throws Exception {
        QueryRefundRes resObj = new QueryRefundRes();
        try {
            resObj = cmbService.queryRefundResult(queryRefundInfo);
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.queryRefundInfo[Exception]:", e);
        }
        return responseOfPost(resObj);
    }
}
