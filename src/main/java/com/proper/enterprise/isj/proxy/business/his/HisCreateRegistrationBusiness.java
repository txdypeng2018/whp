package com.proper.enterprise.isj.proxy.business.his;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.model.enums.MemberRelationEnum;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.model.enmus.RegType;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;
import com.proper.enterprise.isj.webservices.model.enmus.TimeFlag;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.isj.webservices.model.res.RegInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HisCreateRegistrationBusiness<M extends RegistrationDocumentContext<RegistrationDocument> & ModifiedResultBusinessContext<RegistrationDocument>>
        implements IBusiness<RegistrationDocument, M>, ILoggable {

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    HospitalIntroduceService hospitalIntroduceService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RegistrationRepository registrationRepository;

    @Override
    public void process(M ctx) throws Exception {
        RegistrationDocument reg = ctx.getRegistrationDocument();
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
        saveReg.setClinicNum(basicInfo.getMedicalNum());
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
            debug("HIS接口未返回成功,接口名为:regInfo,返回错误信息:{}", regInfo.getReturnMsg());
            throw new HisReturnException(regInfo.getReturnMsg());
        }
        List<RegDoctor> regDocList = regInfo.getRes().getRegDoctorList();
        if (regDocList == null || regDocList.size() == 0) {
            debug("HIS接口返回的医生列表长度为0");
            throw new RegisterException("未找到医生排班信息,保存挂号单失败");
        }
        RegDoctor regDoctor = regDocList.get(0);
        List<Reg> regList = regDoctor.getRegList();
        if (regList == null) {
            debug("HIS接口返回的医生号点长度为0");
            throw new RegisterException("未找到医生号点信息,保存挂号单失败");
        }
        if (regList.size() != 1) {
            debug("医生号点信息返回值大于1,应该等于1");
            throw new RegisterException("医生号点信息异常,保存挂号单失败");
        }
        Reg tempReg = regList.get(0);
        String districtId = CenterFunctionUtils.convertHisDisId2SubjectId(tempReg.getRegDistrict());
        if (StringUtil.isEmpty(districtId) || !subMap.containsKey(districtId)) {
            debug("未找到医生出诊科室对应的院区,保存挂号单失败");
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
            debug("HIS接口未返回成功,接口名为:timeRegInfo,返回错误信息:" + timeRegInfoRes.getReturnMsg());
            throw new HisReturnException(timeRegInfoRes.getReturnMsg());
        }
        List<TimeReg> timeRegList = timeRegInfoRes.getRes().getTimeRegList();
        if (timeRegList == null || timeRegList.size() == 0) {
            debug("timeRegInfo返回数据为空");
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
            debug("RegTime返回数据为空");
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
            long nextNum;
            if (list.size() == 0) {
                nextNum = Long.parseLong(orderSdf.format(now.clone()).concat(df.format(1)));
            } else {
                nextNum = Long.parseLong(list.get(0).getNum()) + 1;
            }
            saveReg.setNum(String.valueOf(nextNum));
            saveReg = registrationRepository.save(saveReg);
        }
        ctx.setResult(saveReg);

    }
}