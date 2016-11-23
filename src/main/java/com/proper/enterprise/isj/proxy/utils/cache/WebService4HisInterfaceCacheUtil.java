package com.proper.enterprise.isj.proxy.utils.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.utils.scheduler.TaskSchedulerUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.PayListReq;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.isj.webservices.model.res.*;
import com.proper.enterprise.isj.webservices.model.res.deptinfo.Dept;
import com.proper.enterprise.isj.webservices.model.res.doctorinfo.Doctor;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.http.HttpClient;

/**
 * Created by think on 2016/9/23 0023.
 */
@Component
@CacheConfig(cacheNames = "pep-temp")
public class WebService4HisInterfaceCacheUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(WebService4HisInterfaceCacheUtil.class);

    private static final String HIS_DEPTINFO_KEY = "'his_deptinfo'";

    private static final String HIS_DOCTORINFO_KEY = "'his_doctorinfo'";

    private static final String HIS_HOSPITAL_INFO_KEY = "'his_hospital_info_key'";

    @Autowired
    WebServicesClient webServicesClient;

    @Autowired
    TaskSchedulerUtil taskSchedulerUtil;

    @CachePut(key = HIS_DEPTINFO_KEY)
    public List<Dept> cacheDeptInfo() throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        ResModel<DeptInfo> deptInfo = webServicesClient.getDeptInfoByParentID(hosId, String.valueOf(-1), "");
        List<Dept> list = new ArrayList<>();
        if (deptInfo.getRes().getDeptList() != null) {
            list.addAll(deptInfo.getRes().getDeptList());
        }
        return list;
    }

    @Cacheable(key = HIS_DEPTINFO_KEY)
    public List<Dept> getCacheDeptInfo() throws Exception {
        return this.cacheDeptInfo();
    }

    @CachePut(key = HIS_DOCTORINFO_KEY)
    public List<Doctor> cacheDoctorInfoRes() throws Exception {
        List<Doctor> doctorList = new ArrayList<>();
        ResModel<DoctorInfo> doctorInfoRes = webServicesClient.getDoctorInfo(CenterFunctionUtils.getHosId(),
                String.valueOf(-1), String.valueOf(-1));
        if (doctorInfoRes.getRes().getDoctorList() != null) {
            doctorList.addAll(doctorInfoRes.getRes().getDoctorList());
        }
        return doctorList;
    }

    @Cacheable(key = HIS_DOCTORINFO_KEY)
    public List<Doctor> getCacheDoctorInfoRes() throws Exception {
        return this.cacheDoctorInfoRes();
    }

    @CachePut(key = HIS_HOSPITAL_INFO_KEY)
    public HosInfo cacheHospitalInfoRes() throws Exception {
        HosInfo info = null;
        ResModel<HosInfo> hosInfoRes = webServicesClient.getHosInfo(CenterFunctionUtils.getHosId());
        if (hosInfoRes.getReturnCode() == ReturnCode.SUCCESS) {
            info = hosInfoRes.getRes();
        }
        return info;
    }

    @Cacheable(key = HIS_HOSPITAL_INFO_KEY)
    public HosInfo getCacheHospitalInfoRes() throws Exception {
        return this.cacheHospitalInfoRes();
    }

    @CachePut(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120, key = "'doctorSche_'+#p0+#p1+#p2")
    public ResModel<RegInfo> cacheDoctorScheInfoRes(String doctorId, String startDate, String endDate) throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        ResModel<RegInfo> regInfo = webServicesClient.getRegInfo(hosId, String.valueOf(-1), doctorId, DateUtil.toDate(startDate),
                DateUtil.toDate(endDate));
        return regInfo;
    }

    @Cacheable(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120, key = "'doctorSche_'+#p0+#p1+#p2")
    public ResModel<RegInfo> getCacheDoctorScheInfoRes(String doctorId, String startDate, String endDate) throws Exception {
        return cacheDoctorScheInfoRes(doctorId, startDate, endDate);
    }

    @CacheEvict(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120, key = "'doctorSche_'+#p0+#p1+#p2")
    public void evictCacheDoctorScheInfoRes(String doctorId, String startDate, String endDate) throws Exception {
    }

    @CachePut(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120, key = "'doctorTimeReg_'+#p0+#p1")
    public ResModel<TimeRegInfo> cacheDoctorTimeRegInfoRes(String doctorId, String regDate)
            throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        ResModel<TimeRegInfo> timeRegInfo = webServicesClient.getTimeRegInfo(hosId, String.valueOf(-1), doctorId, DateUtil.toDate(regDate), -1);
        return timeRegInfo;
    }

    @Cacheable(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120, key = "'doctorTimeReg_'+#p0+#p1")
    public ResModel<TimeRegInfo> getCacheDoctorTimeRegInfoRes(String doctorId, String regDate)
            throws Exception {
        return cacheDoctorTimeRegInfoRes(doctorId, regDate);
    }

    @CacheEvict(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120, key = "'doctorTimeReg_'+#p0+#p1")
    public void evictCacheDoctorTimeRegInfoRes(String doctorId, String regDate) throws Exception {
    }

    @CachePut(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_60, key = "'recipe_idcard_'+#p0.hospPatientId+'_'+#p0.queryType+'_'+#p0.cardNo")
    public ResModel<PayList> cachePayListRes(PayListReq payListReq) throws Exception {
        return webServicesClient.getPayDetailAll(payListReq);
    }

    @Cacheable(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_60, key = "'recipe_idcard_'+#p0.hospPatientId+'_'+#p0.queryType+'_'+#p0.cardNo")
    public ResModel<PayList> getCachePayListRes(PayListReq payListReq) throws Exception {
        return cachePayListRes(payListReq);
    }

    @CacheEvict(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_60, key = "'recipe_idcard_'+#p0+'_'+#p1+'_'+#p2")
    public void evictCachePayListRes(String patientId, String queryType, String cardNo) throws Exception {
    }

    @CachePut(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_600, key = "'report_idcard_'+#p0.patientCardNo")
    public ResModel<ReportInfo> cacheOutReportList(ReportListReq reportListReq) throws Exception {
        return webServicesClient.getCheckOutReportList(reportListReq);
    }

    @Cacheable(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_600, key = "'report_idcard_'+#p0.patientCardNo")
    public ResModel<ReportInfo> getCheckOutReportList(ReportListReq reportListReq) throws Exception {
        ResModel<ReportInfo> res = cacheOutReportList(reportListReq);
        if(res.getReturnCode()!= ReturnCode.SUCCESS){
            this.evictCacheReportList(reportListReq);
        }
        return res;
    }

    @CacheEvict(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_600, key = "'report_idcard_'+#p0.patientCardNo")
    public void evictCacheReportList(ReportListReq reportListReq) throws Exception {
    }

    @CachePut(value = CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900, key = "'report_detail_reportid_'+#p0.reportId")
    public ResModel<ReportDetailInfo> cacheOutReportInfo(ReportInfoReq reportInfoReq) throws Exception {
        return webServicesClient.getNormalReportInfo(reportInfoReq);
    }

    @Cacheable(value = CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900, key = "'report_detail_reportid_'+#p0.reportId")
    public ResModel<ReportDetailInfo> getNormalReportInfo(ReportInfoReq reportInfoReq) throws Exception {
        ResModel<ReportDetailInfo> res = cacheOutReportInfo(reportInfoReq);
        if (res.getReturnCode() != ReturnCode.SUCCESS) {
            this.evictCacheReportInfo(reportInfoReq);
        }
        return res;
    }

    @CacheEvict(value = CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900, key = "'report_detail_reportid_'+#p0.reportId")
    public void evictCacheReportInfo(ReportInfoReq reportInfoReq) throws Exception {
    }

    @CachePut(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_600, key = "'report_pacs_list_idcard_'+#p0")
    public String cachePacsListInfo(String patientId) throws IOException {
        StringBuilder reportListUrl = new StringBuilder();
        reportListUrl.append(ConfCenter.get("isj.report.baseUrl"));
        reportListUrl.append(ConfCenter.get("isj.report.showListParam1"));
        reportListUrl.append(patientId);
        //reportListUrl.append("M004830551"); // TODO TEMP
        reportListUrl.append(ConfCenter.get("isj.report.showListParam2"));
        String reportRet = new String(HttpClient.get(reportListUrl.toString()).getBody(), PEPConstants.DEFAULT_CHARSET);
        return reportRet;
    }
    @Cacheable(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_600, key = "'report_pacs_list_idcard_'+#p0")
    public String getCachePacsListInfo(String patientId) throws Exception {
        return this.cachePacsListInfo(patientId);
    }

    @CacheEvict(value=CenterFunctionUtils.CACHE_NAME_PEP_TEMP_600, key = "'report_pacs_list_idcard_'+#p0")
    public void evictCachePacsListInfo(String patientId) throws Exception {
    }
}
