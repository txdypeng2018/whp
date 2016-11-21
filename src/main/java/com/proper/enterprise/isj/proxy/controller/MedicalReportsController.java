package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDetailDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.MedicalReportsService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 检验检查报告接口
 */
@RestController
@RequestMapping(path = "/medicalReports")
public class MedicalReportsController extends BaseController {

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
     *
     * @param memberId
     *        家庭成员Id
     * @param searchTime
     *        时间范围
     * @param searchStatus
     *        状态
     * @param category
     *        类别
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MedicalReportsDocument>> getReportsList(String memberId, String searchTime, String searchStatus, String category) throws Exception {
        LOGGER.debug("\nmemberId:" + memberId + "\nsearchTime:" + searchTime + "\nsearchStatus:" + searchStatus + "\ncategory:" + category);
        List<MedicalReportsDocument> result = new ArrayList<>();
        try {
            User user = userService.getCurrentUser();
            LOGGER.debug("getCurrentUser");
            if (user != null) {
                BasicInfoDocument basicInfo = null;
                LOGGER.debug("user != null");
                if (StringUtil.isEmpty(memberId)) {
                    LOGGER.debug("memberId is not empty");
                    basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
                } else {
                    LOGGER.debug("memberId is empty");
                    basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
                }
                if(basicInfo != null) {
                    if(StringUtil.isEmpty(basicInfo.getMedicalNum())){
                        userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), memberId, null);
                        if (StringUtil.isEmpty(memberId)) {
                            LOGGER.debug("memberId is not empty");
                            basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
                        } else {
                            LOGGER.debug("memberId is empty");
                            basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
                        }
                    }
                    LOGGER.debug("basicInfo != null && 病历号不为空!");
                    // 检验报告
                    if("1".equals(category)) {
                        LOGGER.debug("category == 1");
                        // 取得请求对象
                        ReportListReq reportReq = reportsService.getReportListReq(basicInfo);
                            LOGGER.debug("getReportListReq success!");
                            // 获取返回报告列表
                            result = reportsService.getReportsList(reportReq, searchStatus, basicInfo, searchTime);
                            LOGGER.debug("ReportListresult:" + result.toString());
                        // 检查报告
                    } else if ("2".equals(category)) {
                        LOGGER.debug("category == 2");
                        // 获取返回报告列表
                        result = reportsService.getPacsReportsList(null, searchStatus, basicInfo, searchTime);
                        LOGGER.debug("ReportListresult:" + result.toString());
                    }
                }
            }
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException ie) {
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_PACS_REPORT_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(result);
    }

    /**
     * 取得指定检查报告类别详细
     *
     * @param category
     *        报告类别
     * @param reportId
     *        报告Id
     * @return retReportsDetail
     */
    @RequestMapping(path = "/{category}/{reportId}", method = RequestMethod.GET)
    public ResponseEntity<MedicalReportsDetailDocument> getReportDetailInfo(@PathVariable String category, @PathVariable String reportId) {
        try {
            if(StringUtil.isNotEmpty(category) && StringUtil.isNotEmpty(reportId)) {
                // 检验报告
                if("1".equals(category)) {
                    ReportInfoReq detaiReq = reportsService.getReportDetailReq(reportId);
                    // 取得返回结果
                    MedicalReportsDetailDocument retObj = reportsService.getRepostsDetailsInfo(reportId, detaiReq);
                    return responseOfGet(retObj);
                    // 检查报告
                } else if("2".equals(category)) {
                    try {
                        String retValue = reportsService.getRepostsDetailsInfo(reportId);
                        return CenterFunctionUtils.setTextResponseEntity(retValue, HttpStatus.OK);
                    } catch (IOException ie) {
                        ie.printStackTrace();
                        return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_PACS_REPORT_ERR,
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                }
            }
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
        return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                HttpStatus.BAD_REQUEST);
    }

    /**
     * 报告单须知信息
     *
     * @return 报告单须知信息
     */
    @JWTIgnore
    @RequestMapping(path = "/prompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAgreement() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.report"));
        String guideMsg = "";
        guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }
}