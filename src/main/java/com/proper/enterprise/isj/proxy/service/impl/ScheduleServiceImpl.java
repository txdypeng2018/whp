package com.proper.enterprise.isj.proxy.service.impl;

import java.text.DecimalFormat;
import java.util.*;

import com.proper.enterprise.platform.core.utils.sort.CNStrComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.*;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.proxy.enums.ScheduleMistakeCodeEnum;
import com.proper.enterprise.isj.proxy.repository.ScheduleMistakeLogRepository;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.RegInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.SpELParser;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/8/19 0019.
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    ScheduleMistakeLogRepository scheduleMistakeLogRepository;

    @Autowired
    SpELParser parser;

    @Override
    public List<ScheDoctorDocument> getScheduleDoctors(String districtId, String deptId, String major, Date startDate,
            Date endDate, boolean isAppointment, int maxScheDoctorIndex) throws Exception {
        // 获得院区
        List<SubjectDocument> districtList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(String.valueOf(0));
        Map<String, SubjectDocument> districtMap = new HashMap<>();
        for (SubjectDocument dis : districtList) {
            districtMap.put(dis.getId(), dis);
        }
        List<DoctorDocument> scheDoctorList = getScheDoctorList(deptId, major);
        String today = DateUtil.toDateString(new Date());
        String hourMin = DateUtil.toString(new Date(), "HH:mm");
        List<ScheDoctorDocument> docList = new ArrayList<>();
        ScheDoctorDocument scheDoc = null;
        List<Reg> tempRegList = null;
        for (DoctorDocument doctor : scheDoctorList) {
            ResModel<RegInfo> allRegInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(
                    doctor.getId(), DateUtil.toDateString(startDate), DateUtil.toDateString(endDate));
            if (allRegInfo.getReturnCode() != ReturnCode.SUCCESS) {
                LOGGER.debug("HIS返回的数据出错,returnCode:" + allRegInfo.getReturnCode() + ",错误对象:"
                        + RegInfo.class.getName());
                throw new HisReturnException(allRegInfo.getReturnMsg());
            }
            List<RegDoctor> allRegDocList = allRegInfo.getRes().getRegDoctorList();
            if (allRegDocList == null || allRegDocList.size() == 0) {
                continue;
            }
            RegDoctor regDoctor = allRegDocList.get(0);
            if (needToFilter(isAppointment, regDoctor)) {
                continue;
            }
            tempRegList = regDoctor.getRegList();
            if (tempRegList == null || tempRegList.size() == 0) {
                continue;
            }
            scheDoc = new ScheDoctorDocument();
            BeanUtils.copyProperties(doctor, scheDoc);
            boolean timeRegFlag = false;
            Set<String> regDateMoreSet = getDoctorMistakeDate(regDoctor, tempRegList);
            getDoctorDeptNonDistrictSet(regDoctor, tempRegList);
            for (Reg reg : tempRegList) {
                scheDoc.setDeptId(reg.getRegDeptcode());
                scheDoc.setDept(reg.getRegDeptname());
                String disId = CenterFunctionUtils.convertHisDisId2SubjectId(reg.getRegDistrict());
                if (checkDoctorRegIsValid(districtId, deptId, docList, scheDoc, regDateMoreSet, reg, disId, isAppointment, districtMap)) {
                    continue;
                }

                ResModel<TimeRegInfo> timeRegInfo = webService4HisInterfaceCacheUtil
                        .getCacheDoctorTimeRegInfoRes(regDoctor.getDoctorId(), DateUtil.toDateString(reg.getRegDate()));
                if (timeRegInfo.getReturnCode() != ReturnCode.SUCCESS) {
                    LOGGER.debug("HIS返回的数据出错,returnCode:" + timeRegInfo.getReturnCode() + ",错误对象:"
                            + TimeRegInfo.class.getName());
                    continue;
                }
                List<TimeReg> timeRegList = timeRegInfo.getRes().getTimeRegList();
                if (timeRegList.size() > 0) {
                    timeRegFlag = true;
                }
                if (checkDoctorOverCount(today, hourMin, scheDoc, reg, timeRegList)){
                    break;
                }
            }
            if (timeRegFlag) {
                docList.add(scheDoc);
                if(docList.size() == maxScheDoctorIndex){
                    break;
                }
            }
        }
        return docList;
    }

    private boolean checkDoctorOverCount(String today, String hourMin, ScheDoctorDocument scheDoc, Reg reg, List<TimeReg> timeRegList) {
        for (TimeReg tempReg : timeRegList) {
            if (today.equals(DateUtil.toDateString(reg.getRegDate()))
                    && hourMin.compareTo(tempReg.getBeginTime()) >= 0) {
                continue;
            }
            scheDoc.setTotal(scheDoc.getTotal() + tempReg.getTotal());
            scheDoc.setOverCount(scheDoc.getOverCount() + tempReg.getOverCount());
            if (scheDoc.getOverCount() > 0) {
                break;
            }
        }
        if (scheDoc.getOverCount() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获得过滤好的医生集合
     * @param deptId
     * @param major
     * @return
     * @throws Exception
     */
    private List<DoctorDocument> getScheDoctorList(String deptId, String major) throws Exception {
        Map<String, List<SubjectDocument>> subMap = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.DOCTOR.getCode()));
        List<SubjectDocument> disList = new ArrayList<>();
        Set<String> idSet = null;
        if (StringUtil.isNotEmpty(deptId)) {
            disList = subMap.get(deptId);
            if (disList == null) {
                disList =  new ArrayList<>();
            }
        } else {
            for (Map.Entry<String, List<SubjectDocument>> entry : subMap.entrySet()) {
                disList.addAll(subMap.get(entry.getKey()));
            }
        }
        if (StringUtil.isNotEmpty(major)) {
            idSet = new HashSet<String>();
            Map<String, Set<String>> likeMap = webServiceCacheUtil.getCacheDoctorInfoLike();
            Iterator<Map.Entry<String, Set<String>>> likeIter = likeMap.entrySet().iterator();
            while (likeIter.hasNext()) {
                Map.Entry<String, Set<String>> entry = likeIter.next();
                if (entry.getKey().contains(major)) {
                    idSet.addAll(likeMap.get(entry.getKey()));
                }
            }
        }
        Set<String> doctorIdSet = new HashSet<String>();
        DoctorDocument doc = null;
        List<DoctorDocument> scheDoctorList = new ArrayList<>();
        for (SubjectDocument districtDocument : disList) {
            if (idSet != null && !idSet.contains(districtDocument.getId())) {
                continue;
            }
            if(doctorIdSet.contains(districtDocument.getId())) {
                continue;
            }
            doctorIdSet.add(districtDocument.getId());
            doc = webServiceCacheUtil.getCacheDoctorDocument().get(districtDocument.getId());
            if(doc!=null){
                scheDoctorList.add(doc);
            }
        }
        Collections.sort(scheDoctorList, new Comparator<DoctorDocument>() {
            @Override
            public int compare(DoctorDocument doc1, DoctorDocument doc2) {
                int seq1 = CenterFunctionUtils.getDoctorTitleSeq(doc1.getTitle());
                int seq2 = CenterFunctionUtils.getDoctorTitleSeq(doc2.getTitle());
                if(seq1-seq2==0){
                    return new CNStrComparator().compare(doc1.getName(), doc2.getName());
                }else{
                    return seq1-seq2;
                }
            }
        });
        return scheDoctorList;
    }

    /**
     * 校验医生排班是否合法
     * 
     * @param districtId
     * @param deptId
     * @param docList
     * @param scheDoc
     * @param regDateMoreSet
     * @param reg
     * @param disId
     * @return
     */
    private boolean checkDoctorRegIsValid(String districtId, String deptId, List<ScheDoctorDocument> docList,
            ScheDoctorDocument scheDoc, Set<String> regDateMoreSet, Reg reg, String disId, boolean isAppointment,
            Map<String, SubjectDocument> districtMap) {
        // 过滤医生排班没返回出诊院区人员
        if (StringUtil.isEmpty(disId)) {
            LOGGER.debug("医生排班没返回出诊院区");
            return true;
        }
        // 过滤一天多个挂号级别人员
        if (regDateMoreSet.contains(DateUtil.toDateString(reg.getRegDate()))) {
            LOGGER.debug("医生一天多个挂号级别");
            return true;
        }
        // 过滤院区科室与his的出诊院区不同的人员
        if (StringUtil.isNotEmpty(deptId) && StringUtil.isNotEmpty(districtId)) {
            if (!disId.equals(districtId)) {
                LOGGER.debug("医生院区科室与his的出诊院区不同");
                return true;
            }
        }
        // 预约挂号,全局搜索将院区赋值为空
        if (isAppointment && StringUtil.isEmpty(deptId)) {
            disId = "";
        }
        if (scheDoc != null) {
            scheDoc.setDistrictId(disId);
            if (StringUtil.isNotEmpty(scheDoc.getDistrictId())) {
                scheDoc.setDistrict(districtMap.get(scheDoc.getDistrictId()).getName());
            }else{
                scheDoc.setDistrict("");
            }
            // 将没有上下午挂号级别的医生,改为暂停挂号
            if (reg.getRegTimeList() == null || reg.getRegTimeList().size() == 0) {
                scheDoc.setUnallowed(CenterFunctionUtils.REG_TEMP_STOP);
                docList.add(scheDoc);
                return true;
            }
        }

        return false;
    }

    /**
     * 保存并返回医生无院区的的科室Id
     * @param regDoctor
     * @param tempRegList
     * @return
     */
    private synchronized  void getDoctorDeptNonDistrictSet(RegDoctor regDoctor, List<Reg> tempRegList) {
        Set<String> deptNonDistrictSet = new HashSet<>();
        List<ScheduleMistakeLogDocument> misList = scheduleMistakeLogRepository
                .findByDoctorIdAndMistakeCode(regDoctor.getDoctorId(),
                        ScheduleMistakeCodeEnum.DEPT_NON_DISTRICT.toString());
        for (ScheduleMistakeLogDocument mistakeLogDocument : misList) {
            deptNonDistrictSet.add(mistakeLogDocument.getDoctorId()+"_"+mistakeLogDocument.getDeptId());
        }
        for (Reg reg : tempRegList) {
            if (StringUtil.isEmpty(reg.getRegDistrict())) {
                if (deptNonDistrictSet.contains(regDoctor.getDoctorId() + "_" + reg.getRegDeptcode())) {
                    continue;
                }
                ScheduleMistakeLogDocument mistakeLogDocument = new ScheduleMistakeLogDocument();
                mistakeLogDocument.setDoctorId(regDoctor.getDoctorId());
                mistakeLogDocument.setDoctorName(regDoctor.getName());
                mistakeLogDocument.setDeptId(reg.getRegDeptcode());
                mistakeLogDocument.setDeptName(reg.getRegDeptname());
                mistakeLogDocument.setMistakeCode(ScheduleMistakeCodeEnum.DEPT_NON_DISTRICT.toString());
                mistakeLogDocument.setMistakeDesc(CenterFunctionUtils.SCHEDULE_MISTAKE_DEPT_NON_DISTRICT);
                scheduleMistakeLogRepository.save(mistakeLogDocument);
                break;
            }
        }
    }

    /**
     * 保存并返回医生同一天出现多诊别的记录
     * @param regDoctor
     * @param tempRegList
     * @return
     */
    private Set<String> getDoctorMistakeDate(RegDoctor regDoctor, List<Reg> tempRegList) {
        Set<String> regDateSet = new HashSet<String>();
        Set<String> regDateMoreSet = new HashSet<String>();
        List<ScheduleMistakeLogDocument> misList = scheduleMistakeLogRepository.findByDoctorIdAndMistakeCode(
                regDoctor.getDoctorId(), ScheduleMistakeCodeEnum.REGDATE_GT_ONE.toString());
        Set<String> haveMistakeSet = new HashSet<>();
        for (ScheduleMistakeLogDocument mistakeLogDocument : misList) {
            haveMistakeSet.add(mistakeLogDocument.getRegDate());
        }
        for (Reg reg : tempRegList) {
            if (regDateSet.contains(DateUtil.toDateString(reg.getRegDate()))) {
                if (!haveMistakeSet.contains(DateUtil.toDateString(reg.getRegDate()))) {
                    ScheduleMistakeLogDocument mistakeLogDocument = new ScheduleMistakeLogDocument();
                    mistakeLogDocument.setDoctorId(regDoctor.getDoctorId());
                    mistakeLogDocument.setDoctorName(regDoctor.getName());
                    mistakeLogDocument.setRegDate(DateUtil.toDateString(reg.getRegDate()));
                    mistakeLogDocument.setMistakeCode(ScheduleMistakeCodeEnum.REGDATE_GT_ONE.toString());
                    mistakeLogDocument.setMistakeDesc(CenterFunctionUtils.SCHEDULE_MISTAKE_REGDATE_GT_ONE);
                    scheduleMistakeLogRepository.save(mistakeLogDocument);
                }
                regDateMoreSet.add(DateUtil.toDateString(reg.getRegDate()));
            }
            regDateSet.add(DateUtil.toDateString(reg.getRegDate()));
        }
        return regDateMoreSet;
    }

    /**
     * 根据当日挂号规则及参数，判断医生是否需要从当日挂号医生列表中过滤掉
     *
     * @param  isAppointment 查询结束日期
     * @param  doctor        医生信息
     * @return true：需要过滤；false：不需要过滤
     */
    private boolean needToFilter(boolean isAppointment, RegDoctor doctor) {
        Collection<RuleEntity> rules = ruleRepository.findByCatalogue("SAME_DAY_FILTER");
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        Map<String, Object> vars = new HashMap<>(3);
        vars.put("isAppointment", isAppointment);
        vars.put("doctor", doctor);
        for (RuleEntity rule : rules) {
            try {
                LOGGER.debug("Parsing {} rule: {}", rule.getName(), rule.getRule());
                if (!parser.parse(rule.getRule(), vars, Boolean.class)) {
                    return false;
                }
            } catch (ExpressionException ee) {
                LOGGER.debug("Parse {} with {} throw exception:", rule.getRule(), vars, ee);
            }
        }
        return true;
    }

    @Override
    public RegisterDoctorDocument findDoctorScheduleByTime(String doctorId, String date) throws Exception {
        Date apptDate = DateUtil.toDate(date, "yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(apptDate);
        int timeFlag = 1;
        if (cal.get(Calendar.HOUR_OF_DAY) >= 18) {
            timeFlag = 3;
        } else if (cal.get(Calendar.HOUR_OF_DAY) >= 12) {
            timeFlag = 2;
        } else if (cal.get(Calendar.HOUR_OF_DAY) >= 6) {
            timeFlag = 1;
        } else {
            timeFlag = 3;
        }
        // 获得院区
        List<SubjectDocument> districtList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(String.valueOf(0));
        Map<String, SubjectDocument> districtMap = new HashMap<>();
        for (SubjectDocument dis : districtList) {
            districtMap.put(dis.getId(), dis);
        }
        // String hosId = CenterFunctionUtils.getHosId();
        RegisterDoctorDocument regDoc = null;
        DecimalFormat df = new DecimalFormat("0.00");
        ResModel<RegInfo> regInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(doctorId, DateUtil.toDateString(cal.getTime()),
                DateUtil.toDateString(cal.getTime()));
        Set<String> doctorIdSet = new HashSet<String>();
        if (regInfo.getReturnCode() == ReturnCode.SUCCESS) {
            List<RegDoctor> regDocList = regInfo.getRes().getRegDoctorList();
            if (regDocList != null) {
                for (RegDoctor regDoctor : regDocList) {
                    if (doctorIdSet.contains(regDoctor.getDoctorId())) {
                        continue;
                    }
                    doctorIdSet.add(regDoctor.getDoctorId());
                    regDoc = new RegisterDoctorDocument();
                    DoctorDocument doc = webServiceCacheUtil.getCacheDoctorDocument().get(regDoctor.getDoctorId());
                    BeanUtils.copyProperties(doc, regDoc);
                    regDoc.setDeptId(regInfo.getRes().getDeptId());
                    regDoc.setDept(regInfo.getRes().getDeptName());
                    String disId = CenterFunctionUtils
                            .convertHisDisId2SubjectId(regInfo.getRes().getDistrict());

                    if (StringUtil.isEmpty(disId)) {
                        continue;
                    }
                    regDoc.setDistrictId(disId);
                    if (StringUtil.isNotEmpty(regDoc.getDistrictId())) {
                        regDoc.setDistrict(districtMap.get(disId).getName());
                    }
                    List<Reg> regList = regDoctor.getRegList();
                    if (regList != null) {
                        for (Reg reg : regList) {
                            List<RegTime> regTimeList = reg.getRegTimeList();
                            if (regTimeList != null) {
                                for (RegTime regTime : regTimeList) {
                                    regDoc.setClinicCategoryCode(regTime.getRegLevel());
                                    regDoc.setAmount(df.format((regTime.getRegFee() + regTime.getTreatFee()) / 100));
                                    break;
                                }
                                StringBuilder title = new StringBuilder();
                                title.append(
                                        CenterFunctionUtils.getRegLevelNameMap().get(regDoc.getClinicCategoryCode()));
                                if (StringUtil.isNotEmpty(regDoctor.getJobTitle())) {
                                    title.append("(").append(regDoctor.getJobTitle()).append(")");
                                }
                                regDoc.setTitle(title.toString());
                            }
                        }
                    }


                }
            }
        } else {
            throw new HisReturnException(regInfo.getReturnMsg());
        }

        return regDoc;
    }

    @Override
    public List<DoctorScheduleDocument> findDoctorScheduleByDate(String doctorId, String districtId, String subjectId, String date)
            throws Exception {
        // 获得院区
        List<SubjectDocument> districtList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(String.valueOf(0));
        Map<String, SubjectDocument> districtMap = new HashMap<>();
        for (SubjectDocument dis : districtList) {
            districtMap.put(dis.getId(), dis);
        }
        Date sDate = null;
        Date eDate = null;
        Calendar cal = Calendar.getInstance();
        if (StringUtil.isNotEmpty(date)) {
            sDate = DateUtil.toDate(date);
            eDate = DateUtil.toDate(date);
        } else {
            sDate = new Date();
            cal.setTime(sDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            sDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, CenterFunctionUtils.SCHEDULING_MAXADD_DAY);
            eDate = cal.getTime();
        }
        ResModel<RegInfo>  allRegInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(doctorId,
                DateUtil.toDateString(sDate), DateUtil.toDateString(eDate));
        if (allRegInfo.getReturnCode() != ReturnCode.SUCCESS) {
            throw new HisReturnException(allRegInfo.getReturnMsg());
        }
        String disId = null;
        List<Reg> regList = null;
        DoctorDocument doc = null;
        DoctorScheduleDocument docSche = null;
        ResModel<TimeRegInfo> timeRegInfo = null;
        List<TimeReg> timeRegList = null;
        List<RegTime> regTimeList = null;
        DecimalFormat df = new DecimalFormat("0.00");
        List<DoctorScheduleDocument> docScheList = new ArrayList<>();
        List<RegDoctor>  allRegDocList = allRegInfo.getRes().getRegDoctorList();
        Set<String> regDateSet = null;
        for (RegDoctor regDoctor : allRegDocList) {
            regList = regDoctor.getRegList();
            if (regList == null) {
                continue;
            }
            doc = webServiceCacheUtil.getCacheDoctorDocument().get(regDoctor.getDoctorId());
            Set<String> regDateMoreSet = getDoctorMistakeDate(regDoctor, regList);
            getDoctorDeptNonDistrictSet(regDoctor, regList);
            for (Reg reg : regList) {
                docSche = new DoctorScheduleDocument();
                docSche.setDate(DateUtil.toDateString(reg.getRegDate()));
                disId = CenterFunctionUtils.convertHisDisId2SubjectId(reg.getRegDistrict());
                if (checkDoctorRegIsValid(districtId, subjectId, null, null, regDateMoreSet, reg, disId, false, districtMap)) {
                    continue;
                }
                docSche.setDistrictId(disId);
                if (StringUtil.isNotEmpty(docSche.getDistrictId())) {
                    docSche.setDistrict(districtMap.get(docSche.getDistrictId()).getName());
                } else {
                    docSche.setDistrict("");
                }
                docSche.setDoctorId(regDoctor.getDoctorId());
                docSche.setName(regDoctor.getName());
                docSche.setSexCode(doc.getSexCode());
                docSche.setDeptId(reg.getRegDeptcode());
                docSche.setDept(reg.getRegDeptname());
                timeRegInfo = webService4HisInterfaceCacheUtil
                        .getCacheDoctorTimeRegInfoRes(regDoctor.getDoctorId(), DateUtil.toDateString(reg.getRegDate()));
                if (timeRegInfo == null) {
                    continue;
                }
                timeRegList = timeRegInfo.getRes().getTimeRegList();
                for (TimeReg tempReg : timeRegList) {
                    docSche.setTotal(docSche.getTotal() + tempReg.getTotal());
                    docSche.setOverCount(docSche.getOverCount() + tempReg.getOverCount());
                }
                if (docSche.getTotal() == 0) {
                    continue;
                }
                if (docSche.getTotal() > 99) {
                    docSche.setTotal(99);
                }
                if (docSche.getOverCount() > 99) {
                    docSche.setOverCount(99);
                }
                if(docSche.getOverCount()>docSche.getTotal()){
                    docSche.setOverCount(docSche.getTotal());
                }
                regTimeList = reg.getRegTimeList();
                if (regTimeList == null||regTimeList.size()==0) {
                    continue;
                }
                for (RegTime regTime : regTimeList) {
                    docSche.setAmount(df.format((regTime.getRegFee() + regTime.getTreatFee()) / 100));
                    docSche.setCategoryCode(regTime.getRegLevel());
                    docSche.setCategory(
                            CenterFunctionUtils.getRegLevelNameMap().get(regTime.getRegLevel()));
                    break;
                }
                StringBuilder title = new StringBuilder();
                title.append(CenterFunctionUtils.getRegLevelNameMap().get(docSche.getCategoryCode()));
                if (StringUtil.isNotEmpty(regDoctor.getJobTitle())) {
                    title.append("(").append(regDoctor.getJobTitle()).append(")");
                }
                docSche.setTitle(title.toString());
                docScheList.add(docSche);
            }
        }
        return docScheList;
    }

    @Override
    public List<TimeRegDocument> findDoctorTimeRegList(String doctorId, Date date) throws Exception {
        ResModel<TimeRegInfo> timeRegInfo = webService4HisInterfaceCacheUtil
                .getCacheDoctorTimeRegInfoRes(doctorId, DateUtil.toDateString(date));
        Calendar calT = Calendar.getInstance();
        Calendar calD = Calendar.getInstance();
        calT.setTime(new Date());
        calD.setTime(date);
        boolean flag = false;
        String hourMin = "";
        if (calT.get(Calendar.YEAR) == calD.get(Calendar.YEAR) && calT.get(Calendar.MONTH) == calD.get(Calendar.MONTH)
                && calT.get(Calendar.DAY_OF_MONTH) == calD.get(Calendar.DAY_OF_MONTH)) {
            flag = true;
            hourMin = DateUtil.toString(calT.getTime(), "HH:mm");
        }
        List<TimeRegDocument> timeRegDocumentList = new ArrayList<>();
        TimeRegDocument timeReg = null;
        if (timeRegInfo.getReturnCode() == ReturnCode.SUCCESS) {
            List<TimeReg> timeRegList = timeRegInfo.getRes().getTimeRegList();
            if (timeRegList != null) {
                for (TimeReg reg : timeRegList) {
                    if (flag) {
                        if (hourMin.compareTo(reg.getBeginTime()) >= 0) {
                            continue;
                        }
                    }
                    timeReg = new TimeRegDocument();
                    timeReg.setTime(reg.getBeginTime());
                    timeReg.setTotal(reg.getTotal());
                    timeReg.setOverCount(reg.getOverCount());
                    timeRegDocumentList.add(timeReg);
                }
            }
        } else {
            throw new HisReturnException(timeRegInfo.getReturnMsg());
        }
        return timeRegDocumentList;
    }

}
