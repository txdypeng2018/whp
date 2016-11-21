package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDetailDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsItemDocument;
import com.proper.enterprise.isj.proxy.service.MedicalReportsService;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4FileCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.isj.webservices.model.res.ReportDetailInfo;
import com.proper.enterprise.isj.webservices.model.res.ReportInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.Report;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportCheckListDetail;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportList;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportUserDetailInfo;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.wideunique.utils.json.JSONArray;
import com.wideunique.utils.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 检验检测报告ServiceImpl
 */
@Service
public class MedicalReportsServiceImpl implements MedicalReportsService{

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalReportsServiceImpl.class);

    @Autowired
    WebService4HisInterfaceCacheUtil webServiceCacheUtil;

    @Autowired
    WebService4FileCacheUtil webServiceFileCacheUtil;

    /**
     * 取得检验检测报告列表
     *
     * @param req
     *        请求对象
     * @param searchStatus
     *        报告状态
     * @param basicInfo
     *        基本信息
     * @return List<MedicalReportsDocument>
     * @throws Exception
     */
    @Override
    public List<MedicalReportsDocument> getPacsReportsList(ReportListReq req, String searchStatus, BasicInfoDocument basicInfo, String searchTime) throws Exception {

        // 需要返回的数据
        List<MedicalReportsDocument> retList = new ArrayList<>();
        try {
            LOGGER.debug("检查报告:" + basicInfo.getMedicalNum());
            LOGGER.debug("getMedicalNum:" + basicInfo.getMedicalNum());
            String reportRet = webServiceCacheUtil.getCachePacsListInfo(basicInfo.getMedicalNum());
            LOGGER.debug("reportRet:" + reportRet);
            if (StringUtil.isNotEmpty(reportRet)) {
                JSONArray reportList = new JSONArray(reportRet);
                LOGGER.debug("reportList" + reportList.toString());
                for (int i = 0; i < reportList.length(); i++) {
                    MedicalReportsDocument medicalReport = new MedicalReportsDocument();
                    JSONObject reportObj = reportList.getJSONObject(i);

                    // 报告ID
                    medicalReport.setId(String.valueOf(reportObj.get("checkserialnum")));
                    // 报告名称
                    if (StringUtil.isNotEmpty(String.valueOf(reportObj.get("studyscription")))) {
                        String reportName = String.valueOf(reportObj.get("studyscription")).replaceAll(",无", "");
                        medicalReport.setName(reportName.substring(1, reportName.length() - 1));
                    }
                    // 检查时间
                    if (StringUtil.isNotEmpty(String.valueOf(reportObj.get("studytime")))) {
                        medicalReport.setExaminationDate(DateUtil.toString(DateUtil.toDate(String.valueOf(reportObj.get("studytime")), "yyyy-MM-dd HH:mm:ss.S"), "yyyy年MM月dd日 HH:mm"));
                    }
                    // 报告类别
                    medicalReport.setReportCategory("2");
                    // 报告状态
                    String realStatus = "0";
                    String studyStatus = String.valueOf(reportObj.get("studystatus"));
                    if (StringUtil.isNotEmpty(studyStatus)) {
                        if (Integer.parseInt(studyStatus) < 70) {
                            // 报告状态:未出
                            medicalReport.setStatus("0");
                        } else {
                            // 报告状态:已出
                            medicalReport.setStatus("1");
                            realStatus = "1";
                        }
                    } else {
                        // 报告状态:未出
                        medicalReport.setStatus("0");
                    }

                    // 判断是否满足查询条件
                    boolean isSatify = this.satisfyQueryCondition(searchTime, String.valueOf(reportObj.get("studytime")), searchStatus, realStatus, 2);

                    // 添加结果
                    if (isSatify) {
                        retList.add(medicalReport);
                    }
                }
            }
        }catch (IOException ie) {
            ie.printStackTrace();
            throw ie;
        }
        // 按照时间倒序
        return getSortedReportList(retList);
    }

    /**
     * 取得检验检测报告列表
     *
     * @param req
     *        请求对象
     * @param searchStatus
     *        报告状态
     * @param basicInfo
     *        基本信息
     * @return List<MedicalReportsDocument>
     * @throws Exception
     */
    @Override
    public List<MedicalReportsDocument> getReportsList(ReportListReq req, String searchStatus, BasicInfoDocument basicInfo, String searchTime) throws Exception {

        // 需要返回的数据
        List<MedicalReportsDocument> retList = new ArrayList<>();
        // 检验报告
        LOGGER.debug("category == 1");
        LOGGER.debug("hosId:" + req.getHosId() + "\nidenno:" + req.getPatientIdcardNo() + "\ncard_no:" + req.getPatientCardNo() + "\nname:" + req.getPatientName() + "\nbegin_date:" + req.getBeginDate() + "\nend_date:" + req.getEndDate());
        LOGGER.debug("basicInfo:[id]=" + basicInfo.getId() + " [medicalNum]=" + basicInfo.getMedicalNum());
        ResModel<ReportInfo> reprotListRes = webServiceCacheUtil.getCheckOutReportList(req);
        if (reprotListRes.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("his interface error!");
            throw new HisReturnException(reprotListRes.getReturnMsg());
        } else {
            LOGGER.debug("payListRes success!");
            ReportList reportList = reprotListRes.getRes().getReportList();
            List<Report> reprotDetailList = reportList.getReport();
            LOGGER.debug("reportList:" + reportList.toString());
            for (Report reprotDetail : reprotDetailList) {
                // 取得报告种类
                int reportType = reprotDetail.getReportType();
                // 目前只处理普通报告
                if (reportType == 0) {
                    // 判断是否满足查询条件
                    boolean isSatify = this.satisfyQueryCondition(searchTime, reprotDetail.getReportTime(), searchStatus, reprotDetail.getState(), 1);

                    // 添加结果
                    if (isSatify) {
                        MedicalReportsDocument medicalReport = new MedicalReportsDocument();
                        // 报告ID
                        medicalReport.setId(reprotDetail.getReportId());
                        // 报告名称
                        medicalReport.setName(reprotDetail.getItemName());
                        // 检查时间
                        if (StringUtil.isNotEmpty(reprotDetail.getReportTime())) {
                            medicalReport.setExaminationDate(DateUtil.toString(DateUtil.toDate(reprotDetail.getReportTime(), "yyyyMMddHHmmSS"), "yyyy年MM月dd日 HH:mm"));
                        }
                        // 报告类别
                        medicalReport.setReportCategory("1");
                        // 报告状态
                        medicalReport.setStatus(reprotDetail.getState());
                        retList.add(medicalReport);
                    }
                    // 其他报告类型
                } else {
                    LOGGER.debug("其他报告类型"); // TODO
                }
            }
        }
        // 按照时间倒序
        return getSortedReportList(retList);
    }

    /**
     * 按照时间倒序排序
     *
     * @param retList
     *        报告列表
     * @return retList
     */
    private List<MedicalReportsDocument> getSortedReportList(List<MedicalReportsDocument> retList) {
        // 按照时间倒序
        if(retList.size() > 0) {
            Collections.sort(retList, new Comparator<MedicalReportsDocument>() {
                @Override
                public int compare(MedicalReportsDocument doc1, MedicalReportsDocument doc2) {
                    if (StringUtil.isEmpty(doc1.getExaminationDate())) {
                        return 1;
                    } else if (StringUtil.isEmpty(doc2.getExaminationDate())) {
                        return -1;
                    } else if (StringUtil.isEmpty(doc1.getExaminationDate())
                            && StringUtil.isEmpty(doc2.getExaminationDate())) {
                        return 0;
                    } else {
                        return doc1.getExaminationDate().compareTo(doc2.getExaminationDate());
                    }
                }
            });
            Collections.reverse(retList);
        }
        return retList;
    }

    /**
     * 取得pacs报告单详细信息
     *
     * @param reportId
     *        报告单Id
     * @return 图片信息
     * @throws Exception
     */
    public String getRepostsDetailsInfo(String reportId) throws Exception {
        try {
            return webServiceFileCacheUtil.getCacheReportPhoto(reportId);
        } catch (IOException ie) {
            ie.printStackTrace();
            throw ie;
        }
    }

    /**
     * 获取检查结果详细
     *
     * @param reportId
     *        检查报告Id
     * @param req
     *        请求对象
     * @return retDetailInfo
     * @throws Exception
     */
    @Override
    public MedicalReportsDetailDocument getRepostsDetailsInfo(String reportId, ReportInfoReq req) throws Exception {
        // 返回对象
        MedicalReportsDetailDocument retDetailInfo = new MedicalReportsDetailDocument();

        ResModel<ReportDetailInfo> reprotListRes = webServiceCacheUtil.getNormalReportInfo(req);
        if (reprotListRes.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("his interface error!");
            throw new HisReturnException(reprotListRes.getReturnMsg());
        } else {
            LOGGER.debug("payDetailRes:" + reprotListRes.getRes().getCheckList().size());
            // 取得报告用户信息
            ReportUserDetailInfo resUserInfo = reprotListRes.getRes().getReportInfo().get(0);
            // 取得报告项目明细
            List<ReportCheckListDetail> resReportDetailList = reprotListRes.getRes().getCheckList().get(0).getDetail();

            // 设置返回参数
            // 报告ID
            retDetailInfo.setId(reportId);
            // 报告名称
            retDetailInfo.setName(resUserInfo.getItemName());
            // 患者姓名
            retDetailInfo.setPatient(resUserInfo.getPatientName());
            // 检查时间
            if (StringUtil.isNotEmpty(resUserInfo.getReportTime())) {
                retDetailInfo.setExaminationDate(DateUtil.toString(DateUtil.toDate(resUserInfo.getReportTime(), "yyyy-MM-dd HH:mm:ss"), "yyyy年MM月dd日 HH:mm"));
            }
            // 检查医生
            retDetailInfo.setInspectionDoctor(resUserInfo.getDoctorName());
            // 审核医生
            retDetailInfo.setExamineDoctor(resUserInfo.getReviewName());
            // 报告类别
            retDetailInfo.setReportCategory("1");
            // 报告状态
            // 报告状态:已出
            if (StringUtil.isNotEmpty(resUserInfo.getReviewTime())) {
                retDetailInfo.setStatus("1");
                // 报告状态:未出
            } else {
                retDetailInfo.setStatus("0");
            }
            // 获取检查各个检查项目明细
            List<MedicalReportsItemDocument> reportDetaiList = new ArrayList<>();
            for (int i = 0; i < resReportDetailList.size(); i++) {
                ReportCheckListDetail resDetail = resReportDetailList.get(i);
                MedicalReportsItemDocument reportDetail = new MedicalReportsItemDocument();
                // 明细ID
                reportDetail.setId(String.valueOf(i + 1));
                // 明细项目名称
                reportDetail.setName(resDetail.getCheckName());
                // 结果
                reportDetail.setResult(resDetail.getResult());
                // 参考
                reportDetail.setReference(resDetail.getReferenceValue());
                reportDetaiList.add(reportDetail);
            }
            // 设置检查结果列表
            retDetailInfo.setItems(reportDetaiList);
        }
        return retDetailInfo;
    }


    /**
     * 构造报告列表请求对象
     *
     * @param basic
     *        用户基本信息
     * @return
     */
    @Override
    public ReportListReq getReportListReq(BasicInfoDocument basic) {
        String hosId = CenterFunctionUtils.getHosId();
        ReportListReq listReq = new ReportListReq();
        listReq.setHosId(hosId);
        //listReq.setHospPatientId(basic.getMedicalNum());
        listReq.setPatientIdcardType(IDCardType.IDCARD);
        listReq.setPatientIdcardNo(basic.getIdCard());
        LOGGER.debug("req[PatientIdcardNo]:" + listReq.getPatientCardNo());
        //listReq.setPatientIdcardNo("220302197109020219"); // TODO TEMP
        listReq.setPatientCardType(CardType.CARD);
        listReq.setPatientCardNo(basic.getMedicalNum());
        LOGGER.debug("req[PatientCardNo]:" + listReq.getPatientCardNo());
        //listReq.setPatientCardNo("M003911082"); // TODO TEMP
        listReq.setPatientName(basic.getName());
        LOGGER.debug("req[PatientName]:" + listReq.getPatientName());
        listReq.setPatientPhone(basic.getPhone());
        LOGGER.debug("req[PatientPhone]:" + listReq.getPatientPhone());
        //listReq.setPatientName("王志波"); // TODO TEMP
        //listReq.setPatientPhone(basic.getPhone());
//        if (StringUtil.isNotEmpty(basic.getSexCode())) {
//            switch (Integer.parseInt(basic.getSexCode())) {
//                case 0:
//                    listReq.setPatientSex(Sex.FEMALE);
//                    break;
//                case 1:
//                    listReq.setPatientSex(Sex.MALE);
//                    break;
//                case 2:
//                    listReq.setPatientSex(Sex.SECRET);
//                    break;
//                case 3:
//                    listReq.setPatientSex(Sex.OTHERS);
//                    break;
//                default:
//                    listReq.setPatientSex(Sex.SECRET);
//                    break;
//            }
//        } else {
//            listReq.setPatientSex(Sex.SECRET);
//        }
        String idCard = basic.getIdCard();
        //String idCard = "220302197109020219"; // TODO TEMP
        if (StringUtil.isNotEmpty(idCard)) {
            listReq.setPatientAge(IdcardUtils.getAgeByIdCard(idCard));
        } else {
            listReq.setPatientAge(0);
        }
        LOGGER.debug("req[PatientAge]:" + listReq.getPatientAge());
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        calS.setTime(new Date());
        calE.setTime(new Date());
        calS.add(Calendar.YEAR, -3);
        listReq.setBeginDate(DateUtil.toString(calS.getTime(), "yyyy-MM-dd HH:mm:ss"));
        listReq.setEndDate(DateUtil.toString(calE.getTime(), "yyyy-MM-dd HH:mm:ss"));
        return listReq;
    }

    /**
     * 获取检查单详细信息对象
     *
     * @param reportId
     *        报告单号
     * @return
     */
    @Override
    public ReportInfoReq getReportDetailReq(String reportId) {
        String hosId = CenterFunctionUtils.getHosId();
        ReportInfoReq detailReq = new ReportInfoReq();
        LOGGER.debug("hosId:" + hosId);
        detailReq.setHosId(hosId);
        LOGGER.debug("reportId:" + reportId);
        detailReq.setReportId(reportId);
        //detailReq.setHospPatientId("");

        return detailReq;
    }

    /**
     * 是否满足查询条件
     *
     * @param searchTime
     *        查询时间段参数
     * @param originSearchTime
     *        数据中时间段参数
     * @param searchStatus
     *        查询状态参数
     * @param originSearchState
     *        数据中状态参数
     * @param typeFlg
     *        报告类型
     * @return ret
     */
    private boolean satisfyQueryCondition(String searchTime, String originSearchTime, String searchStatus, String originSearchState, int typeFlg) {
        boolean ret = false;
        // 判断条件
        boolean timeFlg = false;
        boolean statusFlg = false;
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        Calendar calO = Calendar.getInstance();
        calS.setTime(new Date());
        calE.setTime(new Date());

        // searchTime查询参数不为空并且报告日期也不为空
        if (StringUtil.isNotEmpty(searchTime) && StringUtil.isNotEmpty(originSearchTime)) {
            // 普通报告
            if(1 == typeFlg) {
                calO.setTime(DateUtil.toDate(originSearchTime, "yyyyMMddHHmmss"));
                // 检查报告(pacs)
            } else if(2 == typeFlg){
                calO.setTime(DateUtil.toDate(originSearchTime, "yyyy-MM-dd HH:mm:ss.S"));
            }
            // 一周内
            if("week".equals(searchTime)) {
                LOGGER.debug("一周内");
                calS.add(Calendar.WEEK_OF_MONTH, -1);
                // 一月内
            } else if ("month".equals(searchTime)) {
                LOGGER.debug("一月内");
                calS.add(Calendar.MONTH, -1);
                // 半年内
            } else if ("halfYear".equals(searchTime)) {
                LOGGER.debug("半年内");
                calS.add(Calendar.MONTH, -6);
                // 一年内
            } else if ("year".equals(searchTime)) {
                LOGGER.debug("一年内");
                calS.add(Calendar.YEAR, -1);
            }
            if(calS.before(calO) && calE.after(calO)) {
                LOGGER.debug("在时间范围内");
                timeFlg = true;
            }
        } else if(StringUtil.isEmpty(searchTime)){
            LOGGER.debug("时间不限");
            timeFlg = true;
        }

        // 状态查询条件为空
        if(StringUtil.isEmpty(searchStatus)) {
            statusFlg = true;
            if("0".equals(originSearchState)) {
                timeFlg = true;
            }
            // 状态查询条件为已出
        } else if("1".equals(searchStatus)) {
            if("1".equals(originSearchState)) {
                statusFlg = true;
            }
            // 状态查询条件为未出
        } else if ("0".equals(searchStatus)) {
            if("0".equals(originSearchState)) {
                statusFlg = true;
                timeFlg = true;
            }
        }
        // 添加结果
        if(timeFlg && statusFlg) {
            ret = true;
        }
        return ret;
    }
}
