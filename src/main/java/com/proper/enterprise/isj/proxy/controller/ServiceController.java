package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.FeedbackContext;
import com.proper.enterprise.isj.context.FeedbackStatusContext;
import com.proper.enterprise.isj.context.OpinionContext;
import com.proper.enterprise.isj.context.OpinionIdContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.ServiceUserOpinionDocumentContext;
import com.proper.enterprise.isj.context.StatusCodeContext;
import com.proper.enterprise.isj.context.UserNameContext;
import com.proper.enterprise.isj.context.UserTelContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.customerservice.FetchAppNameBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.GetPhoneNumBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.SaveFeedbackOpinionBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.ServiceFeedbackOpinionBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.ServiceSaveUserOpinionBusiness;
import com.proper.enterprise.isj.proxy.business.opinion.GetOpinionInfoBusiness;
import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * .
 */
@RestController
@RequestMapping(path = "/service")
public class ServiceController extends IHosBaseController {

    /**
     * 取得服务电话号码.
     *
     * @return 返回给调用方的应答.
     */
    @AuthcIgnore
    @RequestMapping(value = "/phone", method = RequestMethod.GET)
    public ResponseEntity<String> getPhoneNum() throws Exception {
        return responseOfGet(toolkit.execute(GetPhoneNumBusiness.class, c -> {
        }));
    }

    /**
     * 保存用户意见.
     *
     * @param opinionDocment .
     * @return 返回给调用方的应答.
     */
    @PostMapping(path = "/userOpinion", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveUserOpinion(@RequestBody ServiceUserOpinionDocument opinionDocment)
            throws Exception {
        return responseOfPost(toolkit.execute(ServiceSaveUserOpinionBusiness.class, (c) -> {
            ((ServiceUserOpinionDocumentContext<?>) c).setOpinionDocment(opinionDocment);
        }));
    }

    /**
     * 保存反馈意见.
     *
     * @param opinionDocment .
     * @return 返回给调用方的应答.
     */
    @PutMapping(path = "/feedbackOpinion", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveFeedbackOpinion(@RequestBody ServiceUserOpinionDocument opinionDocment)
            throws Exception {
        return responseOfPut(toolkit.execute(SaveFeedbackOpinionBusiness.class, (c) -> {
            ((ServiceUserOpinionDocumentContext<?>) c).setOpinionDocment(opinionDocment);
        }));

    }

    /**
     * 取得登录用户意见列表.
     *
     * @param feedbackStatus .
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @GetMapping(path = "/userOpinion")
    public ResponseEntity<List<ServiceUserOpinionDocument>> getUserOpinion(
            @RequestParam(required = false) String feedbackStatus) throws Exception {
        return responseOfGet(toolkit.execute(SaveFeedbackOpinionBusiness.class, (c) -> {
            ((FeedbackStatusContext<?>) c).setFeedbackStatus(feedbackStatus);
        }));
    }

    /**
     * 取得意见列表.
     *
     * @param userName
     *            用户姓名.
     * @param userTel
     *            用户手机号.
     * @param statusCode
     *            反馈状态.
     * @param opinion
     *            用户意见.
     * @param feedback
     *            反馈意见.
     * @param pageNo
     *            当前页码.
     * @param pageSize
     *            每页数量.
     * @return
     *         意见列表.
     * @throws Exception
     *             异常.
     */
    @GetMapping(path = "/feedbackOpinion")
    public ResponseEntity<ServiceFeedbackDocument> feedbackOpinion(@RequestParam(required = false) String userName,
            @RequestParam(required = false) String userTel, @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String opinion, @RequestParam(required = false) String feedback,
            @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {

        return responseOfGet(toolkit.execute(ServiceFeedbackOpinionBusiness.class, (c) -> {
            ((UserNameContext<?>) c).setUserName(userName);
            ((UserTelContext<?>) c).setUserTel(userTel);
            ((StatusCodeContext<?>) c).setStatusCode(statusCode);
            ((OpinionContext<?>) c).setOpinion(opinion);
            ((FeedbackContext<?>) c).setFeedback(feedback);
            ((PageNoContext<?>) c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>) c).setPageSize(Integer.parseInt(pageSize));
        }));

    }

    /**
     * 取得指定反馈意见信息.
     *
     * @param opinionId 反馈意见ID.
     * @return 返回给调用方的应答.
     */
    @GetMapping(path = "/userOpinion/{opinionId}")
    public ResponseEntity<ServiceUserOpinionDocument> getOpinionInfo(@PathVariable String opinionId) {

        return responseOfGet(toolkit.execute(GetOpinionInfoBusiness.class, (c) -> {
            ((OpinionIdContext<?>) c).setOpinionId(opinionId);
        }));

    }

    /**
     * 取得应用名称信息.
     *
     * @return 取得应用名称信息.
     */
    @AuthcIgnore
    @RequestMapping(path = "/appName", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAppName() {
        return responseOfGet(toolkit.execute(FetchAppNameBusiness.class, c -> {
        }));

    }
}
