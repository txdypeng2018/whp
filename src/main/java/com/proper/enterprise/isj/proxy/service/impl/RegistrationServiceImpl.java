package com.proper.enterprise.isj.proxy.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.entity.AliRefundEntity;
import com.proper.enterprise.isj.pay.weixin.model.WeixinRefundReq;
import com.proper.enterprise.isj.proxy.document.RegistrationConcession;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundHisDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.model.enums.MemberRelationEnum;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.*;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.isj.webservices.model.res.*;
import com.proper.enterprise.isj.webservices.model.res.orderreg.Concession;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/9/4 0004.
 */
@Service
public class RegistrationServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    MessagesService messagesService;

    @Autowired
    private WebServicesClient webServicesClient;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    HospitalIntroduceService hospitalIntroduceService;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;


    @Autowired
    MongoTemplate mongoTemplate;




    public RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument saveReg, String isAppointment) throws Exception {
        OrderRegReq orderReg = saveOrderAndUpdateReg(saveReg);
        if(isAppointment.equals(String.valueOf(1))){
            saveReg2His(saveReg, orderReg);
        }
        return saveReg;
    }

    /**
     * 生成未缴费订单,更新挂号单
     * 
     * @param saveReg
     * @return
     */
    private OrderRegReq saveOrderAndUpdateReg(RegistrationDocument saveReg) {
        Order orderInfo = orderService.saveCreateOrder(saveReg);
        saveReg.setOrderNum(orderInfo.getOrderNo());
        OrderRegReq orderReg = this.convertRegistration2OrderReg(saveReg);
        RegistrationOrderReqDocument orderReq = new RegistrationOrderReqDocument();
        BeanUtils.copyProperties(orderReg, orderReq);
        saveReg.setRegistrationOrderReq(orderReq);
        registrationRepository.save(saveReg);
        return orderReg;
    }

    /**
     * 生成挂号单
     * 
     * @param reg
     * @return
     * @throws Exception
     */
    public RegistrationDocument saveCreateRegistration(RegistrationDocument reg) throws Exception {
        Map<String, String> subMap = webServiceDataSecondCacheUtil.getCacheSubjectMap();
        HosInfo hosInfo = hospitalIntroduceService.getHospitalInfoFromHis();
        Date apptDate = DateUtil.toDate(reg.getRegisterDate(), "yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(apptDate);
        User user = userService.getCurrentUser();
        UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(user.getId());
        BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(),
                reg.getPatientId());
        RegistrationDocument saveReg = new RegistrationDocument();
        saveReg.setPatientId(basicInfo.getId());
        saveReg.setPatientName(basicInfo.getName());
        saveReg.setPatientCardNo(basicInfo.getMedicalNum());
        saveReg.setPatientIdCard(basicInfo.getIdCard());
        saveReg.setPatientPhone(basicInfo.getPhone());
        saveReg.setApptDate(DateUtil.toString(cal.getTime(), "yyyy年MM月dd日 HH:mm"));
        saveReg.setOperatorCardNo(userInfo.getIdCard());
        saveReg.setOperatorName(userInfo.getName());
        saveReg.setOperatorPhone(userInfo.getPhone());
        saveReg.setHospitalId(CenterFunctionUtils.getHosId());
        saveReg.setHospital(hosInfo.getName());
        saveReg.setRegisterDate(DateUtil.toString(new Date(), "yyyy年MM月dd日 HH:mm"));
        saveReg.setStatusCode(RegistrationStatusEnum.NOT_PAID.getValue());
        saveReg.setStatus(CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.NOT_PAID.getValue()));
        saveReg.setIsAppointment(reg.getIsAppointment());
        if (userInfo.getId().equals(reg.getPatientId())) {
            saveReg.setRegType(RegType.SELF);
        } else if (basicInfo.getMemberRelation() == MemberRelationEnum.SON
                || basicInfo.getMemberRelation() == MemberRelationEnum.DAUGHTER) {
            saveReg.setRegType(RegType.CHILDREN);
        } else {
            saveReg.setRegType(RegType.OTHERS);
        }
        switch (Integer.parseInt(basicInfo.getSexCode())) {
        case 0:
            saveReg.setPatientSex(Sex.FEMALE);
            break;
        case 1:
            saveReg.setPatientSex(Sex.MALE);
            break;

        case 2:
            saveReg.setPatientSex(Sex.SECRET);
            break;
        case 3:
            saveReg.setPatientSex(Sex.OTHERS);
            break;
        default:
            saveReg.setPatientSex(Sex.OTHERS);
            break;
        }

        String birth = IdcardUtils.getBirthByIdCard(basicInfo.getIdCard());
        saveReg.setPatientBirthday(DateUtil.toDateString(DateUtil.toDate(birth, "yyyyMMdd")));
        String tempDate = reg.getRegisterDate().split(" ")[0];
        ResModel<RegInfo> regInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(reg.getDoctorId(),
                tempDate, tempDate);
        if (regInfo.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("HIS接口未返回成功,接口名为:regInfo,返回错误信息:" + regInfo.getReturnMsg());
            throw new HisReturnException(regInfo.getReturnMsg());
        }
        List<RegDoctor> regDocList = regInfo.getRes().getRegDoctorList();
        if (regDocList == null || regDocList.size() == 0) {
            LOGGER.debug("HIS接口返回的医生列表长度为0");
            throw new RegisterException("未找到医生排班信息,保存挂号单失败");
        }
        RegDoctor regDoctor = regDocList.get(0);
        List<Reg> regList = regDoctor.getRegList();
        if (regList == null) {
            LOGGER.debug("HIS接口返回的医生号点长度为0");
            throw new RegisterException("未找到医生号点信息,保存挂号单失败");
        }
        if (regList.size() != 1) {
            LOGGER.debug("医生号点信息返回值大于1,应该等于1");
            throw new RegisterException("医生号点信息异常,保存挂号单失败");
        }
        Reg tempReg = regList.get(0);
        String districtId = CenterFunctionUtils.convertHisDisId2SubjectId(tempReg.getRegDistrict());
        if (StringUtil.isEmpty(districtId) || !subMap.containsKey(districtId)) {
            LOGGER.debug("未找到医生出诊科室对应的院区,保存挂号单失败");
            throw new RegisterException("未找到医生出诊科室对应的院区,保存挂号单失败");
        }
        saveReg.setRegDate(DateUtil.toDateString(tempReg.getRegDate()));
        saveReg.setDeptId(tempReg.getRegDeptcode());
        saveReg.setDept(tempReg.getRegDeptname());
        // saveReg.setRoomName(regInfo.getRes().getRoomName());
        saveReg.setDoctorId(regDoctor.getDoctorId());
        saveReg.setDoctor(regDoctor.getName());
        saveReg.setDistrictId(districtId);
        saveReg.setDistrict(subMap.get(districtId));
        ResModel<TimeRegInfo> timeRegInfoRes = webService4HisInterfaceCacheUtil
                .getCacheDoctorTimeRegInfoRes(reg.getDoctorId(), tempDate);
        if (timeRegInfoRes.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("HIS接口未返回成功,接口名为:timeRegInfo,返回错误信息:" + timeRegInfoRes.getReturnMsg());
            throw new HisReturnException(timeRegInfoRes.getReturnMsg());
        }
        List<TimeReg> timeRegList = timeRegInfoRes.getRes().getTimeRegList();
        if (timeRegList == null || timeRegList.size() == 0) {
            LOGGER.debug("timeRegInfo返回数据为空");
            throw new HisReturnException("未找到医生出诊具体的出诊信息,保存挂号单失败");
        }
        for (TimeReg timeReg : timeRegList) {
            if (timeReg.getBeginTime().equals(reg.getRegisterDate().split(" ")[1])) {
                saveReg.setBeginTime(timeReg.getBeginTime());
                saveReg.setEndTime(timeReg.getEndTime());
                if (Integer.parseInt(timeReg.getTimeFlag()) == 1) {
                    saveReg.setTimeFlag(TimeFlag.AM);
                } else if (Integer.parseInt(timeReg.getTimeFlag()) == 2) {
                    saveReg.setTimeFlag(TimeFlag.PM);
                } else {
                    saveReg.setTimeFlag(TimeFlag.NIGHT);
                }
                saveReg.setRegId(timeReg.getRegId());
                saveReg.setRegNum(timeReg.getRegNum());
            }
        }
        List<RegTime> regTimeList = tempReg.getRegTimeList();
        if (regTimeList == null || regTimeList.size() == 0) {
            LOGGER.debug("RegTime返回数据为空");
            throw new HisReturnException("未找到医生分时排班的信息,保存挂号单失败");
        }

        for (RegTime regTime : regTimeList) {
            if (regTime.getTimeFlag().getCode() != saveReg.getTimeFlag().getCode()) {
                continue;
            }
            saveReg.setRegFee((int) regTime.getRegFee());
            saveReg.setTreatFee((int) regTime.getTreatFee());
            saveReg.setRegLevelCode(regTime.getRegLevel());
            saveReg.setRegLevelName(CenterFunctionUtils.getRegLevelNameMap().get(regTime.getRegLevel()));
            saveReg.setAmount(String.valueOf(regTime.getRegFee() + regTime.getTreatFee()));
        }
        Date now = new Date();
        synchronized (now.clone()) {
            Pattern pattern = Pattern.compile("^" + DateUtil.toDateString((Date) now.clone()) + ".*$",
                    Pattern.CASE_INSENSITIVE);
            Pattern pattern2 = Pattern.compile("^\\d*$", Pattern.CASE_INSENSITIVE);
            Query query = new Query();
            query.addCriteria(Criteria.where("createTime").regex(pattern).and("num").regex(pattern2));
            query.with(new Sort(Sort.Direction.DESC, "num"));
            SimpleDateFormat orderSdf = new SimpleDateFormat("yyMMddHHmm");
            DecimalFormat df = new DecimalFormat("00000");
            List<RegistrationDocument> list = mongoTemplate.find(query, RegistrationDocument.class);
            long nextNum = 0l;
            if (list.size() == 0) {
                nextNum = Long.parseLong(orderSdf.format(now.clone()).concat(df.format(1)));
            } else {
                nextNum = Long.parseLong(list.get(0).getNum()) + 1;
            }
            saveReg.setNum(String.valueOf(nextNum));
            saveReg = registrationRepository.save(saveReg);
        }

        return saveReg;
    }

    /**
     * 更新his号点,根据his单号生成挂号单号,方便查询,挂号单号格式yyMMdd+his挂号单号
     * 
     * @param saveReg
     * @param orderReg
     * @throws Exception
     */
    private RegistrationDocument saveReg2His(RegistrationDocument saveReg, OrderRegReq orderReg) throws Exception {
        ResModel<OrderReg> ordrRegModel = webServicesClient.orderReg(orderReg);
        if (ordrRegModel.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("调用HIS的orderReg接口返回异常:" + ordrRegModel.getReturnMsg());
            throw new HisReturnException(ordrRegModel.getReturnMsg());
        }
        saveReg.setClinicNum(ordrRegModel.getRes().getHospMedicalNum());
        RegistrationOrderHisDocument orderHis = new RegistrationOrderHisDocument();
        BeanUtils.copyProperties(ordrRegModel.getRes(), orderHis);
        if (ordrRegModel.getRes().getConcessions() != null) {
            List<Concession> conList = ordrRegModel.getRes().getConcessions();
            List<RegistrationConcession> regConList = new ArrayList<>();
            RegistrationConcession regCon = null;
            for (Concession concession : conList) {
                regCon = new RegistrationConcession();
                BeanUtils.copyProperties(concession, regCon);
                regConList.add(regCon);
            }
            orderHis.setRegistrationConcession(regConList);
        }
        saveReg.setRegistrationOrderHis(orderHis);
        return registrationRepository.save(saveReg);
    }

    public void updateRegistrationAndOrder(Object req) throws Exception {
        String channelId = null;
        ResModel<PayReg> payRegRes = null;
        Order order = null;
        if(req instanceof PayRegReq){
            PayRegReq payRegReq = (PayRegReq)req;
            channelId = payRegReq.getPayChannelId();
            synchronized (payRegReq.getOrderId()){
                order = orderService.findByOrderNo(payRegReq.getOrderId());
            }
            if(order != null){
                LOGGER.debug("预约挂号缴费请求参数----------->>>");
                LOGGER.debug(JSONUtil.toJSON(payRegReq));
                payRegRes = webServicesClient.payReg(payRegReq);
            }
        }else{
            PayOrderRegReq payOrderRegReq = (PayOrderRegReq)req;
            channelId = payOrderRegReq.getPayChannelId();
            synchronized (payOrderRegReq.getOrderId()){
                order = orderService.findByOrderNo(payOrderRegReq.getOrderId());
            }
            if(order != null) {
                LOGGER.debug("当日挂号缴费请求参数----------->>>");
                LOGGER.debug(JSONUtil.toJSON(payOrderRegReq));
                payRegRes = webServicesClient.payOrderReg(payOrderRegReq);
            }
        }
        if (order != null) {
            updateRegistrationAndOrderStatus(channelId, payRegRes, order);
        }
    }

    /**
     * 订单不为空,更新挂号单和订单状态
     * @param channelId
     * @param payRegRes
     * @param order
     * @throws HisReturnException
     */
    private void updateRegistrationAndOrderStatus(String channelId, ResModel<PayReg> payRegRes, Order order) throws HisReturnException {
        RegistrationDocument regDoc = this.getRegistrationDocumentById(order.getFormId());
        RegistrationOrderHisDocument his = regDoc.getRegistrationOrderHis();
        his.setClientReturnMsg(payRegRes.getReturnMsg() + "(" + payRegRes.getReturnCode() + ")");
        if (payRegRes.getReturnCode() == ReturnCode.SUCCESS) {
//            String hospMedicalNum = payRegRes.getRes().getHospMedicalNum();
//            if (StringUtil.isNotEmpty(his.getHospMedicalNum())) {
//                hospMedicalNum = his.getHospMedicalNum();
//            }
            BeanUtils.copyProperties(payRegRes.getRes(), his);
//            his.setHospMedicalNum(hospMedicalNum);
            regDoc.setRegistrationOrderHis(his);
            regDoc.setStatusCode(RegistrationStatusEnum.PAID.getValue());
            regDoc.setStatus(CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.PAID.getValue()));
//            regDoc.setClinicNum(hospMedicalNum);
            this.saveRegistrationDocument(regDoc);
            order.setOrderStatus(String.valueOf(2));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
            order.setPayWay(channelId);
            orderService.save(order);
            //saveMedicalNum2UserInfo(regDoc, his);
        } else {
            regDoc.setRegistrationOrderHis(his);
            this.saveRegistrationDocument(regDoc);
            throw new HisReturnException(payRegRes.getReturnMsg());
        }
    }

//    /**
//     * 将病历号更新到人员信息
//     * @param regDoc
//     * @param his
//     */
//    private void saveMedicalNum2UserInfo(RegistrationDocument regDoc, RegistrationOrderHisDocument his) {
//        UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(regDoc.getCreateUserId());
//        if (userInfo.getId().equals(regDoc.getPatientId())) {
//            userInfo.setMedicalNum(his.getHospMedicalNum());
//            Map<String, String> medicalNumMap = userInfo.getMedicalNumMap();
//            medicalNumMap.put(his.getHospMedicalNum(), DateUtil.getTimestamp());
//            userInfo.setMedicalNumMap(medicalNumMap);
//        } else {
//            List<FamilyMemberInfoDocument> fList = userInfo.getFamilyMemberInfo();
//            for (FamilyMemberInfoDocument familyMemberInfoDocument : fList) {
//                if (familyMemberInfoDocument.getId().equals(regDoc.getPatientId())) {
//                    familyMemberInfoDocument.setMedicalNum(his.getHospMedicalNum());
//                    Map<String, String> medicalNumMap = familyMemberInfoDocument.getMedicalNumMap();
//                    medicalNumMap.put(his.getHospMedicalNum(), DateUtil.getTimestamp());
//                    familyMemberInfoDocument.setMedicalNumMap(medicalNumMap);
//                }
//            }
//        }
//        userInfoService.saveOrUpdateUserInfo(userInfo);
//    }


    public void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception {
        ResModel<Refund> refundRes = webServicesClient.refund(refundReq);
        if (refundRes.getReturnCode() == ReturnCode.SUCCESS) {
            RegistrationRefundHisDocument refundHis = new RegistrationRefundHisDocument();
            BeanUtils.copyProperties(refundRes.getRes(), refundHis);
            Order order = orderService.findByOrderNo(refundReq.getOrderId());
            if (order != null) {
                RegistrationDocument regDoc = this.getRegistrationDocumentById(order.getFormId());
                regDoc.setRegistrationRefundHis(refundHis);
                regDoc.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                regDoc.setStatus(
                        CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                regDoc.setRefundApplyType(String.valueOf(1));
                this.saveRegistrationDocument(regDoc);
                order.setOrderStatus(String.valueOf(3));
                order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
                order.setCancelDate(DateUtil.toTimestamp(new Date()));
                // 更新订单状态
                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                orderService.save(order);
            }
        } else {
            throw new HisReturnException(refundRes.getReturnMsg());
        }
    }

    public RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo) {
        String hosId = CenterFunctionUtils.getHosId();
        RefundReq refundReq = new RefundReq();
        refundReq.setHosId(hosId);
        int fee = 0;
        Order order = orderService.findByOrderNo(orderNo);
        if (orderNo != null) {
            RegistrationDocument reg = this.getRegistrationDocumentById(order.getFormId());
            if (reg != null) {
                if (infoObj instanceof AliRefundEntity) {
                    AliRefundEntity refund = (AliRefundEntity) infoObj;
                    refundReq.setOrderId(orderNo);
                    refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                    refundReq.setRefundId(reg.getRegistrationRefundReq().getRefundId());
                    refundReq.setRefundSerialNum(refund.getTradeNo());
                    BigDecimal bigDecimal = new BigDecimal(refund.getRefundFee());
                    bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                    refundReq.setTotalFee(bigDecimal.intValue());
                    refundReq.setRefundFee(bigDecimal.intValue());
                    refundReq.setRefundDate(refund.getGmtRefundPay().split(" ")[0]);
                    refundReq.setRefundTime(refund.getGmtRefundPay().split(" ")[1]);
                    refundReq.setRefundResCode(refund.getMsg());
                    refundReq.setRefundResDesc("");
                    refundReq.setRefundRemark("");
                } else if (infoObj instanceof WeixinRefundReq) {
                    WeixinRefundReq refund = (WeixinRefundReq) infoObj;
                    refundReq.setOrderId(orderNo);
                    refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                    refundReq.setRefundId(refund.getOutRefundNo());
                    refundReq.setRefundSerialNum(refund.getTransactionId());
                    refundReq.setTotalFee(refund.getTotalFee());
                    refundReq.setRefundFee(refund.getTotalFee());
                    refundReq.setRefundDate(DateUtil.toDateString(new Date()));
                    refundReq.setRefundTime(DateUtil.toTimestamp(new Date(), false).split(" ")[1]);
                    refundReq.setRefundResCode(refund.getNonceStr());
                    refundReq.setRefundResDesc("");
                    refundReq.setRefundRemark("");
                }
            }
        }
        return refundReq;
    }

    public RegistrationDocument getRegistrationDocumentById(String id) {
        return registrationRepository.findOne(id);
    }

    public RegistrationDocument getRegistrationDocumentByNum(String num) {
        return registrationRepository.findByNum(num);
    }

    public void deleteRegistrationDocument(RegistrationDocument reg) {
        registrationRepository.delete(reg);
    }

    public RegistrationDocument saveRegistrationDocument(RegistrationDocument reg) {
        reg = registrationRepository.save(reg);
        return reg;
    }

    public void saveCancelRegistrationImpl(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        RegistrationDocument reg = this.getRegistrationDocumentById(registrationId);
        String cancelTime = DateUtil.toTimestamp(new Date());
        String cancelRemark = "";
        String regStatus = "";
        String regStatusCode = "";
        if (cancelType == OrderCancelTypeEnum.CANCEL_HANDLE) {
            cancelRemark = CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG;
            regStatusCode = RegistrationStatusEnum.CANCEL.getValue();
        } else if (cancelType == OrderCancelTypeEnum.CANCEL_OVERTIME) {
            cancelRemark = CenterFunctionUtils.ORDER_CANCEL_OVERTIME_MSG;
            regStatusCode = RegistrationStatusEnum.EXCHANGE_CLOSED.getValue();
        } else {
            cancelRemark = CenterFunctionUtils.ORDER_CANCEL_SYS_MSG;
            regStatusCode = RegistrationStatusEnum.EXCHANGE_CLOSED.getValue();
        }

        if (reg != null) {
            if (reg.getIsAppointment().equals("1")) {
                if (StringUtil.isEmpty(reg.getOrderNum())) {
                    throw new RegisterException("挂号单未找到订单号信息,不能进行退号");
                }
                if (StringUtil.isNotEmpty(reg.getCancelHisReturnMsg())
                        && reg.getCancelHisReturnMsg().contains(ReturnCode.SUCCESS.toString())) {
                    return;
                }
                ResModel res = webServicesClient.cancelReg(hosId, reg.getOrderNum(), cancelTime, cancelRemark);
                reg.setCancelHisReturnMsg(res.getReturnMsg() + "(" + res.getReturnCode() + ")");
                this.saveRegistrationDocument(reg);
                if (res.getReturnCode() != ReturnCode.SUCCESS) {
                    if (reg.getRegistrationOrderHis() == null
                            || StringUtil.isEmpty(reg.getRegistrationOrderHis().getHospOrderId())) {
                        saveNonPaidCancelRegistration(registrationId, reg, cancelTime, cancelRemark, regStatusCode);
                    } else {
                        throw new RegisterException(res.getReturnMsg());
                    }
                }
                saveNonPaidCancelRegistration(registrationId, reg, cancelTime, cancelRemark, regStatusCode);

            } else {
                saveNonPaidCancelRegistration(registrationId, reg, cancelTime, cancelRemark, regStatusCode);
            }
        }
    }

    /**
     * 修改退号未支付的状态
     * @param registrationId
     * @param reg
     * @param cancelTime
     * @param cancelRemark
     * @param regStatusCode
     */
    private void saveNonPaidCancelRegistration(String registrationId, RegistrationDocument reg, String cancelTime, String cancelRemark, String regStatusCode) {
        if (reg.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
            reg.setStatusCode(regStatusCode);
            reg.setStatus(CenterFunctionUtils.getRegistrationStatusName(regStatusCode));
            this.saveRegistrationDocument(reg);
            Order order = orderService.getByFormId(registrationId);
            if (order != null) {
                order.setCancelRemark(cancelRemark);
                order.setCancelDate(cancelTime);
                order.setOrderStatus(String.valueOf(0));
                orderService.save(order);
            }
        }
    }

    public OrderRegReq convertRegistration2OrderReg(RegistrationDocument reg) {
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(DateUtil.toDate(DateUtil.toDateString(new Date())));
        OrderRegReq orderReg = new OrderRegReq();
        orderReg.setLockId(String.valueOf(1));
        orderReg.setOrderId(reg.getOrderNum());
        // orderReg.setHospPatientId(reg.getPatientId());
        orderReg.setHospPatientId("");
        orderReg.setChannelId(Channel.APP);
        if (reg.getIsAppointment().equals(String.valueOf(0))) {
            orderReg.setIsReg(IsReg.TODAY);
        } else {
            orderReg.setIsReg(IsReg.APPOINT_DIR);
        }
        orderReg.setRegId(reg.getRegId());
        orderReg.setRegLevel(reg.getRegLevelCode());
        orderReg.setHosId(reg.getHospitalId());
        orderReg.setDeptId(reg.getDeptId());
        orderReg.setDoctorId(reg.getDoctorId());
        orderReg.setRegDate(DateUtil.toDate(reg.getRegDate()));
        orderReg.setTimeFlag(reg.getTimeFlag());
        orderReg.setBeginTime(reg.getBeginTime());
        orderReg.setEndTime(reg.getEndTime());
        orderReg.setRegFee(reg.getRegFee());
        orderReg.setTreatFee(reg.getTreatFee());
        orderReg.setRegType(reg.getRegType());
        orderReg.setIdcardType(IDCardType.IDCARD);
        orderReg.setIdcardNo(reg.getPatientIdCard());
        orderReg.setCardType(CardType.CARD);
        orderReg.setCardNo(reg.getPatientCardNo());
        //orderReg.setCardNo("");
        orderReg.setName(reg.getPatientName());
        orderReg.setSex(reg.getPatientSex());
        orderReg.setBirthday(DateUtil.toDate(reg.getPatientBirthday()));
        orderReg.setMobile(reg.getPatientPhone());
        orderReg.setOperIdcardType(IDCardType.IDCARD);
        orderReg.setOperIdcardNo(reg.getOperatorCardNo());
        orderReg.setOperName(reg.getOperatorName());
        orderReg.setOperMobile(reg.getOperatorPhone());
        orderReg.setOrderTime(Timestamp.valueOf(reg.getCreateTime()));
        orderReg.setAddress("");
        orderReg.setAgentId("");
        return orderReg;
    }

}
