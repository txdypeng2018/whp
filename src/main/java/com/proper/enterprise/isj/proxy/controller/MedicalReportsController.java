package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.CategoryContext;
import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.ReportIdContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.context.SearchTimeContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.function.report.GenerateReportDetailReqByBasicInfoDocFunction;
import com.proper.enterprise.isj.function.report.GenerateReportDetailReqByReportIdFunction;
import com.proper.enterprise.isj.proxy.business.report.MedicalReportGetAgreementBusiness;
import com.proper.enterprise.isj.proxy.business.report.MedicalReportGetDetailInfoBusiness;
import com.proper.enterprise.isj.proxy.business.report.MedicalReportGetListBusiness;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 检验检查报告接口.
 */
@RestController
@RequestMapping(path = "/medicalReports")
public class MedicalReportsController extends IHosBaseController {

    @Autowired
    GenerateReportDetailReqByBasicInfoDocFunction generateReportDetailReqByBasicInfoDocFunction;

    @Autowired
    GenerateReportDetailReqByReportIdFunction generateReportDetailReqByReportIdFunction;

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
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MedicalReportsDocument>> getReportsList(String memberId, String searchTime, String searchStatus, String category) throws Exception {

        return responseOfGet((List<MedicalReportsDocument>) toolkit.execute(MedicalReportGetListBusiness.class, c -> {
            ((MemberIdContext<List<MedicalReportsDocument>>) c).setMemberId(memberId);
            ((SearchTimeContext<List<MedicalReportsDocument>>) c).setSearchTime(searchTime);
            ((SearchStatusContext<List<MedicalReportsDocument>>) c).setSearchStatus(searchStatus);
            ((CategoryContext<List<MedicalReportsDocument>>) c).setCategory(category);
        }));
    }

    /**
     * 取得指定检查报告类别详细.
     *
     * @param category
     *            报告类别.
     * @param reportId
     *            报告Id.
     * @return 返回给调用方的应答.
     */
    @SuppressWarnings({ "rawtypes"})
    @RequestMapping(path = "/{category}/{reportId}", method = RequestMethod.GET)
    public ResponseEntity getReportDetailInfo(@PathVariable String category, @PathVariable String reportId)
            throws Exception {
        Object res = toolkit.execute(MedicalReportGetDetailInfoBusiness.class,
                c -> {
                    ((CategoryContext<?>) c).setCategory(category);
                    ((ReportIdContext<?>) c).setReportId(reportId);
                });

        if (res instanceof IHosBaseController.RetValAndHeaderResult) {
            IHosBaseController.RetValAndHeaderResult vh = (IHosBaseController.RetValAndHeaderResult)res;
            return responseOfGet(vh.getRetValue(), vh.getHeaders());
        } else {
            return responseOfGet(res);
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
        return responseOfGet((String) toolkit.execute(MedicalReportGetAgreementBusiness.class, c -> {
        }));
    }
}
