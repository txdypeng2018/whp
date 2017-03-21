package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.ReportListReqContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.context.SearchTimeContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.report.SatisfyQueryConditionFunction;
import com.proper.enterprise.isj.function.report.SortReportListFunction;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.isj.webservices.model.res.ReportInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.Report;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportList;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HisFetchReportsListBusiness<M extends ReportListReqContext<Collection<MedicalReportsDocument>> & SearchStatusContext<Collection<MedicalReportsDocument>> & BasicInfoDocumentContext<Collection<MedicalReportsDocument>> & SearchTimeContext<Collection<MedicalReportsDocument>> & ModifiedResultBusinessContext<Collection<MedicalReportsDocument>>>
        implements IBusiness<Collection<MedicalReportsDocument>, M>, ILoggable {

    @Autowired
    WebService4HisInterfaceCacheUtil webServiceCacheUtil;

    @Autowired
    SatisfyQueryConditionFunction satisfyQueryConditionFunction;

    @Autowired
    SortReportListFunction sortReportListFunction;

    @Override
    public void process(M ctx) throws Exception {

        ReportListReq req = ctx.getReportListReq();
        BasicInfoDocument basicInfo = ctx.getBasicInfoDocument();
        String searchTime = ctx.getSearchTime();
        String searchStatus = ctx.getSearchStatus();
        // 需要返回的数据
        List<MedicalReportsDocument> retList = new ArrayList<>();
        // 检验报告
        debug("category == 1");
        debug("hosId:" + req.getHosId() + "\nidenno:" + req.getPatientIdcardNo() + "\ncard_no:" + req.getPatientCardNo()
                + "\nname:" + req.getPatientName() + "\nbegin_date:" + req.getBeginDate() + "\nend_date:"
                + req.getEndDate());
        debug("basicInfo:[id]=" + basicInfo.getId() + " [medicalNum]=" + basicInfo.getMedicalNum());
        ResModel<ReportInfo> reprotListRes = webServiceCacheUtil.getCheckOutReportList(req);
        if (reprotListRes.getReturnCode() != ReturnCode.SUCCESS) {
            debug("his interface error!");
            throw new HisReturnException(reprotListRes.getReturnMsg());
        } else {
            debug("payListRes success!");
            ReportList reportList = reprotListRes.getRes().getReportList();
            List<Report> reprotDetailList = reportList.getReport();
            debug("reportList:" + reportList.toString());
            for (Report reprotDetail : reprotDetailList) {
                // 取得报告种类
                int reportType = reprotDetail.getReportType();
                // 目前只处理普通报告
                if (reportType == 0) {
                    // 判断是否满足查询条件
                    boolean isSatify = FunctionUtils.invoke(satisfyQueryConditionFunction, searchTime,
                            reprotDetail.getReportTime(), searchStatus, reprotDetail.getState(), 1);

                    // 添加结果
                    if (isSatify) {
                        MedicalReportsDocument medicalReport = new MedicalReportsDocument();
                        // 报告ID
                        medicalReport.setId(reprotDetail.getReportId());
                        // 报告名称
                        medicalReport.setName(reprotDetail.getItemName());
                        // 检查时间
                        if (StringUtil.isNotEmpty(reprotDetail.getReportTime())) {
                            medicalReport.setExaminationDate(
                                    DateUtil.toString(DateUtil.toDate(reprotDetail.getReportTime(), "yyyyMMddHHmmSS"),
                                            "yyyy年MM月dd日 HH:mm"));
                        }
                        // 报告类别
                        medicalReport.setReportCategory("1");
                        // 报告状态
                        medicalReport.setStatus(reprotDetail.getState());
                        retList.add(medicalReport);
                    }
                    // 其他报告类型
                } else {
                    debug("其他报告类型"); 
                }
            }
        }
        // 按照时间倒序
        ctx.setResult(FunctionUtils.invoke(sortReportListFunction, retList));
    }

}