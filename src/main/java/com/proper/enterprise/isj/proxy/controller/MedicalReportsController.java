package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.core.controller.IHosController;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDetailDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.MedicalReportsService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.exception.ErrMsgException;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

/**
 * 检验检查报告接口.
 */
@RestController
@RequestMapping(path = "/medicalReports")
public class MedicalReportsController extends IHosController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalReportsController.class);

    @Autowired
    MedicalReportsService reportsService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    BaseInfoRepository baseRepo;

    @Autowired
    UserService userService;

    /**
     * 获得报告列表.
     * @param memberId
     *        家庭成员Id.
     * @param searchTime
     *        时间范围.
     * @param searchStatus
     *        状态.
     * @param category
     *        类别.
     * @return 返回给调用方的应答.
     * @exception Exception 异常.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MedicalReportsDocument>> getReportsList(String memberId, String searchTime, String searchStatus, String category) throws Exception {
        LOGGER.debug("\nmemberId: {}\nsearchTime: {}\nsearchStatus: {}\ncategory: {}", memberId, searchTime, searchStatus, category);

        User user = userService.getCurrentUser();
        Assert.notNull(user, "User should NOT null when getting reports list!");

        BasicInfoDocument basicInfo;
        if (StringUtil.isEmpty(memberId)) {
            basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
        } else {
            basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        }
        Assert.notNull(basicInfo, "Basic info should NOT null when getting reports list!");

        if(StringUtil.isEmpty(basicInfo.getMedicalNum())){
            userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), memberId, null);
            if (StringUtil.isEmpty(memberId)) {
                basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
            } else {
                basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
            }
        }
        if (StringUtil.isNull(basicInfo.getMedicalNum())) {
            throw new ErrMsgException(CenterFunctionUtils.PATIENTINFO_MEDICALNUM_NULL_ERR);
        }

        List<MedicalReportsDocument> result;
        // 检验报告
        if("1".equals(category)) {
            // 取得请求对象
            ReportListReq reportReq = reportsService.getReportListReq(basicInfo);
            // 获取返回报告列表
            result = reportsService.getReportsList(reportReq, searchStatus, basicInfo, searchTime);
            // 检查报告
        } else if ("2".equals(category)) {
            // 获取返回报告列表
            result = reportsService.getPacsReportsList(null, searchStatus, basicInfo, searchTime);
        } else {
            throw new ErrMsgException(HIS_DATALINK_ERR);
        }
        return responseOfGet(result);
    }

    /**
     * 取得指定检查报告类别详细.
     *
     * @param category
     *        报告类别.
     * @param reportId
     *        报告Id.
     * @return 返回给调用方的应答.
     */
    @RequestMapping(path = "/{category}/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getReportDetailInfo(@PathVariable String category, @PathVariable String reportId) throws Exception {
        // 检验报告
        if("1".equals(category)) {
            ReportInfoReq detailReq = reportsService.getReportDetailReq(reportId);
            // 取得返回结果
            MedicalReportsDetailDocument retObj = reportsService.getRepostsDetailsInfo(reportId, detailReq);
            return responseOfGet(retObj);
            // 检查报告
        } else if("2".equals(category)) {
            String retValue = reportsService.getRepostsDetailsInfo(reportId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return responseOfGet(retValue, headers);
        } else {
            throw new ErrMsgException(HIS_DATALINK_ERR);
        }
    }

    /**
     * 报告单须知信息.
     *
     * @return 报告单须知信息.
     */
    @AuthcIgnore
    @RequestMapping(path = "/prompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAgreement() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.report"));
        String guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }
}
