package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ReportIdContext;
import com.proper.enterprise.isj.context.ReportInfoReqEntityContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDetailDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsItemDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4FileCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.res.ReportDetailInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportCheckListDetail;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportUserDetailInfo;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HisFetchRepostsDetailsInfoBusiness<M extends ReportIdContext<MedicalReportsDetailDocument>
    & ReportInfoReqEntityContext<MedicalReportsDetailDocument>
    & ModifiedResultBusinessContext<MedicalReportsDetailDocument>
>
        implements IBusiness<MedicalReportsDetailDocument, M>, ILoggable {

    @Autowired
    WebService4FileCacheUtil webServiceFileCacheUtil;

    @Autowired
    WebService4HisInterfaceCacheUtil webServiceCacheUtil;

    @Override
    public void process(M ctx) throws Exception {

        String reportId = ctx.getReportId();
        ReportInfoReq req = ctx.getReportInfoReq();
        // 返回对象
        MedicalReportsDetailDocument retDetailInfo = new MedicalReportsDetailDocument();

        ResModel<ReportDetailInfo> reprotListRes = webServiceCacheUtil.getNormalReportInfo(req);
        if (reprotListRes.getReturnCode() != ReturnCode.SUCCESS) {
            debug("his interface error!");
            throw new HisReturnException(reprotListRes.getReturnMsg());
        } else {
            debug("payDetailRes:" + reprotListRes.getRes().getCheckList().size());
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
                retDetailInfo.setExaminationDate(DateUtil.toString(
                        DateUtil.toDate(resUserInfo.getReportTime(), "yyyy-MM-dd HH:mm:ss"), "yyyy年MM月dd日 HH:mm"));
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
        ctx.setResult(retDetailInfo);

    }

}
