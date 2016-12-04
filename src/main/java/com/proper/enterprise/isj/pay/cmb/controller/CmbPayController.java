package com.proper.enterprise.isj.pay.cmb.controller;

import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
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
        boolean ret = false;
        try {
            ret = cmbService.saveNoticePayInfo(request);
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
     * @param payInfo 支付信息
     * @return 处理结果
     * @throws Exception
     */
    @JWTIgnore
    @PostMapping(value = "/querySinglePayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> querySinglePayInfo(@RequestBody CmbPayEntity payInfo) throws Exception {
        PayResultRes resObj = new PayResultRes();
        try {
            resObj = cmbService.querySingleResult(payInfo);
        } catch (Exception e) {
            LOGGER.debug("CmbPayController.querySinglePayInfo[Exception]:", e);
            resObj.setResultCode("-1");
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return responseOfPost(resObj);
    }
}
