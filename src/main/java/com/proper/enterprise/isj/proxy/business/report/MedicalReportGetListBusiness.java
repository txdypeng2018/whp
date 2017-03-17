package com.proper.enterprise.isj.proxy.business.report;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.CategoryContext;
import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.context.SearchTimeContext;
import com.proper.enterprise.isj.function.report.GenerateReportDetailReqByBasicInfoDocFunction;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.service.MedicalReportsService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.exception.ErrMsgException;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class MedicalReportGetListBusiness<T, M extends SearchTimeContext<List<MedicalReportsDocument>> & SearchStatusContext<List<MedicalReportsDocument>> & CategoryContext<List<MedicalReportsDocument>> & MemberIdContext<List<MedicalReportsDocument>> & ModifiedResultBusinessContext<List<MedicalReportsDocument>>>
        implements IBusiness<List<MedicalReportsDocument>, M>, ILoggable {

    @Autowired
    MedicalReportsService reportsService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    GenerateReportDetailReqByBasicInfoDocFunction generateReportDetailReqByBasicInfoDocFunction;

    @Override
    public void process(M ctx) throws Exception {

        String memberId = ctx.getMemberId();
        String searchTime = ctx.getSearchTime();
        String searchStatus = ctx.getSearchStatus();
        String category = ctx.getCategory();

        debug("\nmemberId: {}\nsearchTime: {}\nsearchStatus: {}\ncategory: {}", memberId, searchTime, searchStatus,
                category);

        User user = userService.getCurrentUser();
        // Assert.notNull(user, "User should NOT null when getting reports list!");

        BasicInfoDocument basicInfo;
        if (StringUtil.isEmpty(memberId)) {
            basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
        } else {
            basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        }
        // Assert.notNull(basicInfo, "Basic info should NOT null when getting reports list!");

        if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
            userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), memberId, null);
            if (StringUtil.isEmpty(memberId)) {
                basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
            } else {
                basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
            }
        }
        if (basicInfo == null || StringUtil.isNull(basicInfo.getMedicalNum())) {
            throw new ErrMsgException(CenterFunctionUtils.PATIENTINFO_MEDICALNUM_NULL_ERR);
        }

        List<MedicalReportsDocument> result;
        // 检验报告
        if ("1".equals(category)) {
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
        ctx.setResult(result);

    }

}
