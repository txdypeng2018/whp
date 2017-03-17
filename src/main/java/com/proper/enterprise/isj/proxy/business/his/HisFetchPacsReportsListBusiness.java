package com.proper.enterprise.isj.proxy.business.his;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.ReportListReqContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.context.SearchTimeContext;
import com.proper.enterprise.isj.exception.IHosException;
import com.proper.enterprise.isj.function.report.SatisfyQueryConditionFunction;
import com.proper.enterprise.isj.function.report.SortReportListFunction;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.wideunique.utils.json.JSONArray;
import com.wideunique.utils.json.JSONObject;

@Service
public class HisFetchPacsReportsListBusiness<M extends ReportListReqContext<Collection<MedicalReportsDocument>> & SearchStatusContext<Collection<MedicalReportsDocument>> & BasicInfoDocumentContext<Collection<MedicalReportsDocument>> & SearchTimeContext<Collection<MedicalReportsDocument>> & ModifiedResultBusinessContext<Collection<MedicalReportsDocument>>>
        implements IBusiness<Collection<MedicalReportsDocument>, M>, ILoggable {

    @Autowired
    WebService4HisInterfaceCacheUtil webServiceCacheUtil;

    @Autowired
    SatisfyQueryConditionFunction satisfyQueryConditionFunction;

    @Autowired
    SortReportListFunction sortReportListFunction;

    @Override
    public void process(M ctx) throws Exception {
        BasicInfoDocument basicInfo = ctx.getBasicInfoDocument();
        String searchTime = ctx.getSearchTime();
        String searchStatus = ctx.getSearchStatus();

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
                    boolean isSatify = satisfyQueryConditionFunction.execute(searchTime, String.valueOf(reportObj.get("studytime")), searchStatus, realStatus, 2);

                    // 添加结果
                    if (isSatify) {
                        retList.add(medicalReport);
                    }
                }
            }
        }catch (IOException ie) {
            LOGGER.error("Error occurs when getting PACS reports!", ie);
            throw new IHosException(CenterFunctionUtils.APP_PACS_REPORT_ERR);
        }
        // 按照时间倒序
        ctx.setResult(FunctionUtils.invoke(sortReportListFunction, retList));
    }

}