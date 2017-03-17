package com.proper.enterprise.isj.function.report;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * 构造报告列表请求对象.
 *
 *         old:com.proper.enterprise.isj.proxy.service.impl.MedicalReportsServiceImpl.getReportListReq(BasicInfoDocument)
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class GenerateReportDetailReqByBasicInfoDocFunction 
implements IFunction<ReportListReq>, ILoggable {
    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public ReportListReq execute(Object... params) throws Exception {
        return getReportListReq((BasicInfoDocument) params[0]);
    }

    /**
     * 构造报告列表请求对象.
     *
     * @param basic
     *        用户基本信息.
     * @return 返回值.
     */
    public ReportListReq getReportListReq(BasicInfoDocument basic) {
        String hosId = CenterFunctionUtils.getHosId();
        ReportListReq listReq = new ReportListReq();
        listReq.setHosId(hosId);
        listReq.setPatientIdcardType(IDCardType.IDCARD);
        listReq.setPatientIdcardNo(basic.getIdCard());
        debug("req[PatientIdcardNo]:" + listReq.getPatientCardNo());
        listReq.setPatientCardType(CardType.CARD);
        listReq.setPatientCardNo(basic.getMedicalNum());
        debug("req[PatientCardNo]:" + listReq.getPatientCardNo());
        listReq.setPatientName(basic.getName());
        debug("req[PatientName]:" + listReq.getPatientName());
        listReq.setPatientPhone(basic.getPhone());
        debug("req[PatientPhone]:" + listReq.getPatientPhone());
        String idCard = basic.getIdCard();
        if (StringUtil.isNotEmpty(idCard)) {
            listReq.setPatientAge(IdcardUtils.getAgeByIdCard(idCard));
        } else {
            listReq.setPatientAge(0);
        }
        debug("req[PatientAge]:" + listReq.getPatientAge());
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        calS.setTime(new Date());
        calE.setTime(new Date());
        calS.add(Calendar.YEAR, -3);
        listReq.setBeginDate(DateUtil.toString(calS.getTime(), "yyyy-MM-dd HH:mm:ss"));
        listReq.setEndDate(DateUtil.toString(calE.getTime(), "yyyy-MM-dd HH:mm:ss"));
        return listReq;
    }

}