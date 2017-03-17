package com.proper.enterprise.isj.proxy.business.report;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.CategoryContext;
import com.proper.enterprise.isj.context.ReportIdContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.IHosException;
import com.proper.enterprise.isj.function.report.GenerateReportDetailReqByReportIdFunction;
import com.proper.enterprise.isj.proxy.service.MedicalReportsService;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.exception.ErrMsgException;

@Service
public class MedicalReportGetDetailInfoBusiness<T, M extends ReportIdContext<Object> & CategoryContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    MedicalReportsService reportsService;

    @Autowired
    GenerateReportDetailReqByReportIdFunction generateReportDetailReqByReportIdFunction;

    @Override
    public void process(M ctx) throws Throwable {
        String category = ctx.getCategory();
        String reportId = ctx.getReportId();
        try {
            // 检验报告
            if ("1".equals(category)) {
                ReportInfoReq detailReq = reportsService.getReportDetailReq(reportId);
                // 取得返回结果
                ctx.setResult(reportsService.getRepostsDetailsInfo(reportId, detailReq));
                // 检查报告
            } else if ("2".equals(category)) {
                String retValue = reportsService.getRepostsDetailsInfo(reportId);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);

                ctx.setResult(new IHosBaseController.RetValAndHeaderResult(retValue, headers));

            } else {
                throw new ErrMsgException(HIS_DATALINK_ERR);
            }
        } catch (UnmarshallingFailureException e) {
            error("MedicalReportsController.getReportDetailInfo[UnmarshallingFailureException]:", e);
            throw new ErrMsgException(HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            error("MedicalReportsController.getReportDetailInfo[HisReturnException]:", e);
            throw e;
        } catch (ErrMsgException e) {
            throw e;
        } catch (Exception e) {
            error("MedicalReportsController.getReportDetailInfo[Exception]:", e);
            Throwable cause = e.getCause();
            if(e instanceof RuntimeException && cause!=null && cause instanceof IHosException){
                throw new ErrMsgException(cause.getMessage());
            }else{
                throw new ErrMsgException(APP_SYSTEM_ERR);
            }
        }
    }
}
