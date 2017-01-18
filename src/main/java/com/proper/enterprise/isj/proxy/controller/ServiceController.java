package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.enums.FeedbackEnum;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.isj.push.PushService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

/**
 * .
 */
@RestController
@RequestMapping(path = "/service")
public class ServiceController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    ServiceService serviceService;

    @Autowired
    UserService userService;

    @Autowired
    BaseInfoRepository baseRepo;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    PushService pushService;

    /**
     * 取得服务电话号码.
     * @return 返回给调用方的应答.
     */
    @AuthcIgnore
    @RequestMapping(value="/phone", method = RequestMethod.GET)
    public ResponseEntity<String> getPhoneNum() throws Exception {
        String retValue = serviceService.getPhoneNum();
        return responseOfGet(retValue);
    }

    /**
     * 保存用户意见.
     * @param opinionDocment .
     * @return 返回给调用方的应答.
     */
    @PostMapping(path = "/userOpinion", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveUserOpinion(@RequestBody ServiceUserOpinionDocument opinionDocment) throws Exception {
        String retValue = "";
        try {
            User currentUser = userService.getCurrentUser();
            BasicInfoDocument basicInfo = userInfoService.getUserInfoByUserId(currentUser.getId());
            opinionDocment.setUserId(currentUser.getId());
            opinionDocment.setUserName(basicInfo.getName());
            opinionDocment.setUserTel(basicInfo.getPhone());
            opinionDocment.setOpinionTime(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
            opinionDocment.setOpinion(opinionDocment.getOpinion());
            opinionDocment.setStatus(CenterFunctionUtils.UNREPLAY);
            opinionDocment.setStatusCode(FeedbackEnum.UNREPLAY.getValue());
            serviceService.saveOpinion(opinionDocment);
        } catch (Exception e) {
            LOGGER.debug("ServiceController.saveUserOpinion[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        return responseOfPost(retValue);
    }

    /**
     * 保存反馈意见.
     * @param opinionDocment .
     * @return 返回给调用方的应答.
     */
    @PutMapping(path = "/feedbackOpinion", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveFeedbackOpinion(@RequestBody ServiceUserOpinionDocument opinionDocment) throws Exception {
        String retValue = "";
        if(StringUtil.isNotNull(opinionDocment.getId())) {
            LOGGER.debug("request_id:" + opinionDocment.getId());
            LOGGER.debug("request_feedback:" + opinionDocment.getFeedback());
            opinionDocment.setFeedbackTime(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
            opinionDocment.setStatus(CenterFunctionUtils.REPLAYED);
            opinionDocment.setStatusCode(FeedbackEnum.REPLAYED.getValue());
            serviceService.saveOpinion(opinionDocment);

            List<String> userNameList = new ArrayList<>();
            userNameList.add(opinionDocment.getUserTel());
            String pushContent = "您有一条意见反馈消息!";
            String pushType = "feedback";
            List<Map<String, String>> paramMapList = new ArrayList<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("opinionId", opinionDocment.getId());
            paramMapList.add(paramMap);
            pushService.pushInfo(pushContent, pushType, userNameList, paramMapList);
            LOGGER.debug("推送反馈意见信息!");
            LOGGER.debug("反馈意见ID:" + opinionDocment.getId());
        }
        return responseOfPut(retValue);
    }

    /**
     * 取得登录用户意见列表.
     *
     * @param feedbackStatus .
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @GetMapping(path = "/userOpinion")
    public ResponseEntity<List<ServiceUserOpinionDocument>> getUserOpinion(@RequestParam(required = false) String feedbackStatus) throws Exception {
        boolean isStatus = !StringUtil.isNull(feedbackStatus);
        User currentUser = userService.getCurrentUser();
        String currentUserId = currentUser.getId();
        List<ServiceUserOpinionDocument> opinionList;
        if(isStatus) {
            opinionList = serviceService.getByUserIdAndFeedbackStatus(currentUserId, feedbackStatus);
        } else {
            opinionList = serviceService.getByUserId(currentUserId);
        }
        return responseOfGet(opinionList);
    }

    /**
     * 取得意见列表.
     *
     * @param userName
     *        用户姓名.
     * @param userTel
     *        用户手机号.
     * @param statusCode
     *        反馈状态.
     * @param opinion
     *        用户意见.
     * @param feedback
     *        反馈意见.
     * @param pageNo
     *        当前页码.
     * @param pageSize
     *        每页数量.
     * @return
     *         意见列表.
     * @throws Exception
     *         异常.
     */
    @GetMapping(path = "/feedbackOpinion")
    public ResponseEntity<ServiceFeedbackDocument> feedbackOpinion(@RequestParam(required = false) String userName,
       @RequestParam(required = false) String userTel, @RequestParam(required = false) String statusCode,
       @RequestParam(required = false) String opinion, @RequestParam(required = false) String feedback,
       @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {

        // 取得反馈意见列表
        ServiceFeedbackDocument feedbackInfo = serviceService.getFeedBackInfo(userName, userTel, statusCode,
                opinion, feedback, pageNo, pageSize);
        return responseOfGet(feedbackInfo);
    }

    /**
     * 取得指定反馈意见信息.
     *
     * @param opinionId 反馈意见ID.
     * @return 返回给调用方的应答.
     */
    @GetMapping(path = "/userOpinion/{opinionId}")
    public ResponseEntity<ServiceUserOpinionDocument> getOpinionInfo(@PathVariable String opinionId) {
        ServiceUserOpinionDocument opinion = serviceService.getById(opinionId);
        return responseOfGet(opinion);
    }

    /**
     * 取得应用名称信息.
     *
     * @return 取得应用名称信息.
     */
    @AuthcIgnore
    @RequestMapping(path = "/appName", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAppName() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.appName"));
        String guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }
}
