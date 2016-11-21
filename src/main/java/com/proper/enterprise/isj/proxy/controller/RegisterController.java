package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.isj.proxy.service.rule.RegistrationRuleService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by think on 2016/8/16 0016.
 *
 */

@RestController
@RequestMapping(path = "/register")
public class RegisterController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    RegistrationService registrationService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    BaseInfoRepository baseRepo;

    @Autowired
    RegistrationRuleService registrationRuleService;

    @Autowired
    CacheManager cacheManager;

    /**
     * 取得就诊退号说明
     *
     * @return 就诊退号说明
     */
    @JWTIgnore
    @RequestMapping(path = "/visitAndBacknumDesc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> getVisitAndBacknumDesc() {
        List<BaseInfoEntity> visitInfo1 = baseRepo.findByInfoType(ConfCenter.get("isj.info.visit1"));
        List<BaseInfoEntity> visitInfo2 = baseRepo.findByInfoType(ConfCenter.get("isj.info.visit2"));
        List<BaseInfoEntity> backnumInfo = baseRepo.findByInfoType(ConfCenter.get("isj.info.backnum"));
        Map<String, String> retMap = new HashMap<>();
        retMap.put("visit1", visitInfo1.get(0).getInfo());
        retMap.put("visit2", visitInfo2.get(0).getInfo());
        retMap.put("backnum", backnumInfo.get(0).getInfo());
        return responseOfGet(retMap);
    }

    /**
     * 取得挂号须知信息
     *
     * @return 挂号须知信息
     */
    @JWTIgnore
    @RequestMapping(path = "/agreement", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAgreement() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.agreement"));
        String guideMsg = "";
        guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }

    /**
     * 取得预约挂号提示信息
     *
     * @return 预约挂号提示信息
     */
    @JWTIgnore
    @RequestMapping(path = "/apptPrompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getApptPrompt() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.apptPrompt"));
        String guideMsg = "";
        guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }

    /**
     * 取得当日挂号提示信息
     *
     * @return 当日挂号提示信息
     */
    @JWTIgnore
    @RequestMapping(path = "/todayPrompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTodayPrompt() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.todayPrompt"));
        String guideMsg = "";
        guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }

    /**
     * 取得医生挂号信息
     *
     * @param id
     *            医生ID
     * @param date
     *            挂号时间
     * @return 医生挂号信息
     */
    @JWTIgnore
    @RequestMapping(path = "/doctor", method = RequestMethod.GET)
    public ResponseEntity<RegisterDoctorDocument> getRegisterDoctor(@RequestParam(required = true) String id,
            @RequestParam(required = true) String date) {
        RegisterDoctorDocument scheList = null;
        try {
            scheList = scheduleService.findDoctorScheduleByTime(id, date);
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(scheList);
    }

    /**
     * 取得指定挂号单详细
     *
     * @return 指定挂号单详细
     */
    @RequestMapping(path = "/registrations", method = RequestMethod.GET)
    public ResponseEntity<List<RegistrationDocument>> getUserRegistrations(String memberId) throws Exception {
        List<RegistrationDocument> resultList = new ArrayList<>();
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RegisterException(CenterFunctionUtils.USERINFO_LOGIN_ERR);
        }
        BasicInfoDocument basicInfo = null;
        if (StringUtil.isEmpty(memberId)) {
            basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
        } else {
            basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        }
        if (basicInfo == null) {
            throw new RegisterException(CenterFunctionUtils.PATIENTINFO_GET_ERR);
        }
        try {
            List<RegistrationDocument> regList = registrationService.findRegistrationDocumentList(basicInfo.getId());
            BigDecimal tempBig = null;
            DecimalFormat df = new DecimalFormat("0.00");
            for (RegistrationDocument registrationDocument : regList) {
                registrationDocument = saveOrUpdateRegistrationByPayStatus(registrationDocument);
                if (registrationDocument == null) {
                    continue;
                }
                if (StringUtil.isNotEmpty(registrationDocument.getAmount())) {
                    tempBig = new BigDecimal(registrationDocument.getAmount()).divide(new BigDecimal("100"));
                    registrationDocument.setAmount(df.format(tempBig));
                    registrationDocument.setClinicNum(registrationDocument.getNum());
                }
                resultList.add(registrationDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(resultList);
    }

    /**
     * 检查挂号单支付状态,并更新挂号单
     * 
     * @param registrationDocument
     * @return
     */
    private RegistrationDocument saveOrUpdateRegistrationByPayStatus(RegistrationDocument registrationDocument) {
        try {
            if (registrationDocument.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
                if (StringUtil.isEmpty(registrationDocument.getRegistrationOrderHis().getHospPayId())) {
                    registrationDocument = registrationService
                            .saveQueryPayTradeStatusAndUpdateReg(registrationDocument);
                }
            } else if (registrationDocument.getStatusCode().equals(RegistrationStatusEnum.PAID.getValue())) {
                if (registrationDocument.getRegistrationOrderHis() == null
                        || StringUtil.isEmpty(registrationDocument.getRegistrationOrderHis().getHospPayId())) {
                    return registrationDocument;
                }
                if (registrationDocument.getRegistrationTradeRefund() != null) {
                    if (StringUtil.isNotEmpty(registrationDocument.getRegistrationTradeRefund().getOutTradeNo())) {
                        registrationDocument = registrationService
                                .saveQueryRefundTradeStatusAndUpdateReg(registrationDocument);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug("挂号单列表初始化校验是否已付款失败,挂号单号:" + registrationDocument.getNum());
        }
        return registrationDocument;
    }

    /**
     * 取得指定挂号单详细
     *
     * @return 指定挂号单详细
     */
    @RequestMapping(path = "/registrations/registration", method = RequestMethod.GET)
    public ResponseEntity<RegistrationDocument> getRegistration(@RequestParam(required = true) String id) {
        RegistrationDocument reg = null;
        try {
            reg = registrationService.getRegistrationDocumentById(id);
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.toDate(reg.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT));
            cal.add(Calendar.MINUTE, CenterFunctionUtils.ORDER_COUNTDOWN);
            if (cal.getTime().compareTo(new Date()) <= 0
                    && reg.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
                registrationService.saveCancelRegistration(reg.getId(), OrderCancelTypeEnum.CANCEL_OVERTIME);
            }
            reg = registrationService.getRegistrationDocumentById(id);
            BigDecimal tempBig = new BigDecimal(reg.getAmount()).divide(new BigDecimal("100"));
            DecimalFormat df = new DecimalFormat("0.00");
            reg.setAmount(df.format(tempBig));
            boolean canBack = CenterFunctionUtils.checkRegCanBack(reg);
            if (canBack) {
                reg.setCanBack(String.valueOf(1));
            } else {
                reg.setCanBack(String.valueOf(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(reg);
    }

    /**
     * 添加挂号单
     *
     * @return
     */
    @RequestMapping(path = "/registrations/registration", method = RequestMethod.POST)
    public ResponseEntity<Object> addRegistration(@RequestBody RegistrationDocument reg) throws Exception {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RegisterException(CenterFunctionUtils.USERINFO_LOGIN_ERR);
        }
        // 查找患者信息
        BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(),
                reg.getPatientId());
        if (basicInfo == null) {
            throw new RegisterException(CenterFunctionUtils.PATIENTINFO_GET_ERR);
        }
        if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
            userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), basicInfo.getId(), null);
        }
        // 将历史订单进行校验
        List<RegistrationDocument> hasRegOrderList = registrationService
                .findRegistrationDocumentList(basicInfo.getId());
        for (RegistrationDocument registrationDocument : hasRegOrderList) {
            this.saveOrUpdateRegistrationByPayStatus(registrationDocument);
        }
        try {
            // 预约挂号
            if (reg.getIsAppointment().equals(String.valueOf(1))) {
                // 预约挂号过滤同一号点的并发
                saveOrRemoveCacheRegKey(reg, String.valueOf(1));
                // 预约挂号校验是否有未付款的订单
                List<RegistrationDocument> regisList = registrationService.findRegistrationByCreateUserIdAndPayStatus(
                        user.getId(), RegistrationStatusEnum.NOT_PAID.getValue(), reg.getIsAppointment());
                if (regisList != null && regisList.size() > 0) {
                    return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.ORDER_NON_PAY_ERR,
                            HttpStatus.CONFLICT);
                }
            }
            // 校验挂号单的时间是否有冲突
            checkRegOrderTimeIsValid(reg);
            // 根据规则判断是否可挂此号
            String checkRegMsg = registrationRuleService.checkPersonRegistration(reg.getDeptId(), basicInfo.getIdCard());
            if (StringUtil.isNotEmpty(checkRegMsg)) {
                throw new RegisterException(checkRegMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveOrRemoveCacheRegKey(reg, String.valueOf(0));
            throw e;
        }
        return createRegistrationAndReturnOrder(reg);

    }

    /**
     * 创建挂号单
     * 
     * @param reg
     * @return
     * @throws RegisterException
     * @throws HisLinkException
     * @throws HisReturnException
     */
    private ResponseEntity<Object> createRegistrationAndReturnOrder(@RequestBody RegistrationDocument reg)
            throws RegisterException, HisLinkException, HisReturnException {
        try {
            RegistrationDocument saveReg = registrationService.saveCreateRegistrationAndOrder(reg,
                    reg.getIsAppointment());
            saveOrRemoveCacheRegKey(reg, String.valueOf(0));
            if (saveReg == null) {
                throw new RegisterException(CenterFunctionUtils.ORDER_SAVE_ERR);
            }
            Map<String, String> map = new HashMap<>();
            map.put("orderNum", saveReg.getOrderNum());
            return new ResponseEntity(map, HttpStatus.CREATED);
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            saveOrRemoveCacheRegKey(reg, String.valueOf(0));
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            e.printStackTrace();
            saveOrRemoveCacheRegKey(reg, String.valueOf(0));
            throw new HisReturnException(e.getMessage());
        } catch (RegisterException e) {
            e.printStackTrace();
            saveOrRemoveCacheRegKey(reg, String.valueOf(0));
            throw new RegisterException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            saveOrRemoveCacheRegKey(reg, String.valueOf(0));
            throw new RegisterException(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
    }

    /**
     * 将号点锁定,或释放,减少对his接口的访问
     * 
     * @param reg
     *            挂号单信息, 保存的Key值:registion_医生Id_挂号日期+时间点
     * @param cacheType
     *            1:保存缓存,如果有则抛异常,0:清缓存
     * @throws RegisterException
     */
    private synchronized void saveOrRemoveCacheRegKey(@RequestBody RegistrationDocument reg, String cacheType)
            throws RegisterException {
        String cacheRegKey = "registration_" + reg.getDoctorId().concat("_").concat(reg.getRegisterDate());
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_60);
        Cache.ValueWrapper valueWrapper = tempCache.get(cacheRegKey);
        if (valueWrapper != null && valueWrapper.get() != null) {
            if (cacheType.equals("1")) {
                String patientId = (String) valueWrapper.get();
                if (StringUtil.isEmpty(patientId) || !reg.getPatientId().equals(patientId)) {
                    throw new RegisterException(CenterFunctionUtils.REG_IS_ABSENCE_ERROR);
                }
            } else {
                tempCache.evict(cacheRegKey);
            }
        } else {
            tempCache.put(cacheRegKey, reg.getPatientId());
        }
    }

    /**
     * 检验挂号单的时间正确性
     * 
     * @param reg
     * @throws RegisterException
     */
    private void checkRegOrderTimeIsValid(@RequestBody RegistrationDocument reg) throws RegisterException {
        if (StringUtil.isEmpty(reg.getIsAppointment())) {
            throw new RegisterException(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        Date regTimeDate = DateUtil.toDate(reg.getRegisterDate(), "yyyy-MM-dd HH:mm");
        if (regTimeDate.compareTo(new Date()) <= 0) {
            throw new RegisterException(CenterFunctionUtils.ORDER_PAGE_OVERTIME_ERR);
        }
        String dateStr = reg.getRegisterDate().split(" ")[0];
        String today = DateUtil.toDateString(new Date());
        // 预约挂号,预约日期与操作日期不能相同
        if (reg.getIsAppointment().equals(String.valueOf(1)) && dateStr.equals(today)) {
            throw new RegisterException(CenterFunctionUtils.ORDER_PAGE_OVERTIME_ERR);
        }
        // 当日挂号,预约日期必须与操作日期相同
        if (reg.getIsAppointment().equals(String.valueOf(0)) && !dateStr.equals(today)) {
            throw new RegisterException(CenterFunctionUtils.ORDER_PAGE_OVERTIME_ERR);
        }
    }

    /**
     * 退号
     * 
     * @param regMap
     * @return
     */
    @RequestMapping(path = "/registrations/registration/back", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cancelRegistration(@RequestBody Map<String, String> regMap) {
        String resultMsg = "";
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            String registrationId = regMap.get("registrationId");
            if (registrationId != null) {
                RegistrationDocument reg = registrationService.getRegistrationDocumentById(registrationId);
                if (reg == null) {
                    resultMsg = CenterFunctionUtils.REG_GET_DATA_NULL;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    if (CenterFunctionUtils.checkRegCanBack(reg)) {
                        if (reg.getRegistrationOrderHis() == null
                                || StringUtil.isEmpty(reg.getRegistrationOrderHis().getHospOrderId())) {
                            resultMsg = CenterFunctionUtils.CANCELREG_OLD_ORDER;
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else {
                            registrationService.saveCancelRegistration(registrationId,
                                    OrderCancelTypeEnum.CANCEL_HANDLE);
                        }
                    } else {
                        resultMsg = CenterFunctionUtils.CANCELREG_ISTODAY_ERR;
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                }
            } else {
                resultMsg = CenterFunctionUtils.REG_GET_DATA_NULL;
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            resultMsg = CenterFunctionUtils.HIS_DATALINK_ERR;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (HisReturnException e) {
            e.printStackTrace();
            resultMsg = e.getMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (RegisterException e) {
            e.printStackTrace();
            resultMsg = e.getMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            resultMsg = CenterFunctionUtils.APP_SYSTEM_ERR;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity(resultMsg, httpStatus);
    }

}
