package com.proper.enterprise.isj.proxy.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.ReportIdContext;
import com.proper.enterprise.isj.context.ReportInfoReqEntityContext;
import com.proper.enterprise.isj.context.ReportListReqContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.context.SearchTimeContext;
import com.proper.enterprise.isj.exception.IHosException;
import com.proper.enterprise.isj.proxy.business.his.HisFetchPacsReportsListBusiness;
import com.proper.enterprise.isj.proxy.business.his.HisFetchReportsListBusiness;
import com.proper.enterprise.isj.proxy.business.his.HisFetchRepostsDetailsInfoAsStringBusiness;
import com.proper.enterprise.isj.proxy.business.his.HisFetchRepostsDetailsInfoBusiness;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDetailDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.proxy.service.MedicalReportsService;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * 检验检测报告ServiceImpl.
 */
@Service
public class MedicalReportsServiceImpl extends AbstractService implements MedicalReportsService, ILoggable {

    /**
     * 取得检验检测报告列表.
     *
     * @param req
     *            请求对象.
     * @param searchStatus
     *            报告状态.
     * @param basicInfo
     *            基本信息.
     * @return .
     * @throws Exception 异常.
     */
    @Override
    public List<MedicalReportsDocument> getPacsReportsList(ReportListReq req, String searchStatus, BasicInfoDocument basicInfo, String searchTime) throws IHosException {
        return toolkit.execute(HisFetchPacsReportsListBusiness.class, ctx -> {
            ((ReportListReqContext<?>) ctx).setReportListReq(req);
            ((SearchStatusContext<?>) ctx).setSearchStatus(searchStatus);
            ((BasicInfoDocumentContext<?>) ctx).setBasicInfoDocument(basicInfo);
            ((SearchTimeContext<?>) ctx).setSearchTime(searchTime);
        });
    }

    /**
     * 取得检验检测报告列表.
     *
     * @param req
     *            请求对象.
     * @param searchStatus
     *            报告状态.
     * @param basicInfo
     *            基本信息.
     * @return .
     * @throws Exception 异常.
     */
    @Override
    public List<MedicalReportsDocument> getReportsList(ReportListReq req, String searchStatus,
            BasicInfoDocument basicInfo, String searchTime) throws Exception {
        return toolkit.execute(HisFetchReportsListBusiness.class, ctx -> {
            ((ReportListReqContext<?>) ctx).setReportListReq(req);
            ((SearchStatusContext<?>) ctx).setSearchStatus(searchStatus);
            ((BasicInfoDocumentContext<?>) ctx).setBasicInfoDocument(basicInfo);
            ((SearchTimeContext<?>) ctx).setSearchTime(searchTime);
        });
    }

    /**
     * 取得pacs报告单详细信息.
     *
     * @param reportId
     *            报告单Id.
     * @return 图片信息.
     * @throws Exception 异常.
     */
    public String getRepostsDetailsInfo(String reportId) throws IHosException {
        return toolkit.execute(HisFetchRepostsDetailsInfoAsStringBusiness.class,
                ctx -> ((ReportIdContext<?>) ctx).setReportId(reportId));
    }

    /**
     * 获取检查结果详细.
     *
     * @param reportId
     *            检查报告Id.
     * @param req
     *            请求对象.
     * @return retDetailInfo.
     * @throws Exception 异常.
     */
    @Override
    public MedicalReportsDetailDocument getRepostsDetailsInfo(String reportId, ReportInfoReq req) throws Throwable {
        return toolkit.execute(HisFetchRepostsDetailsInfoBusiness.class, ctx -> {
            ((ReportIdContext<?>) ctx).setReportId(reportId);
            ((ReportInfoReqEntityContext<?>) ctx).setReportInfoReq(req);
        }, e->{
            throw e;
        });
    }


    /**
     * 构造报告列表请求对象.
     *
     * @param basic
     *        用户基本信息.
     * @return 返回值.
     */
    @Override
    public ReportListReq getReportListReq(BasicInfoDocument basic) {
        String hosId = CenterFunctionUtils.getHosId();
        ReportListReq listReq = new ReportListReq();
        listReq.setHosId(hosId);
        //listReq.setHospPatientId(basic.getMedicalNum());
        listReq.setPatientIdcardType(IDCardType.IDCARD);
        listReq.setPatientIdcardNo(basic.getIdCard());
        debug("req[PatientIdcardNo]:" + listReq.getPatientCardNo());
        //listReq.setPatientIdcardNo("220302197109020219");
        listReq.setPatientCardType(CardType.CARD);
        listReq.setPatientCardNo(basic.getMedicalNum());
        debug("req[PatientCardNo]:" + listReq.getPatientCardNo());
        //listReq.setPatientCardNo("M003911082"); 
        listReq.setPatientName(basic.getName());
        debug("req[PatientName]:" + listReq.getPatientName());
        listReq.setPatientPhone(basic.getPhone());
        debug("req[PatientPhone]:" + listReq.getPatientPhone());
        //listReq.setPatientName("王志波"); 
        //listReq.setPatientPhone(basic.getPhone());
//        if (StringUtil.isNotEmpty(basic.getSexCode())) {
//            switch (Integer.parseInt(basic.getSexCode())) {
//                case 0:
//                    listReq.setPatientSex(Sex.FEMALE);
//                    break;
//                case 1:
//                    listReq.setPatientSex(Sex.MALE);
//                    break;
//                case 2:
//                    listReq.setPatientSex(Sex.SECRET);
//                    break;
//                case 3:
//                    listReq.setPatientSex(Sex.OTHERS);
//                    break;
//                default:
//                    listReq.setPatientSex(Sex.SECRET);
//                    break;
//            }
//        } else {
//            listReq.setPatientSex(Sex.SECRET);
//        }
        String idCard = basic.getIdCard();
        //String idCard = "220302197109020219"; 
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

    /**
     * 获取检查单详细信息对象.
     *
     * @param reportId
     *        报告单号.
     * @return 返回值.
     */
    @Override
    public ReportInfoReq getReportDetailReq(String reportId) {
        String hosId = CenterFunctionUtils.getHosId();
        ReportInfoReq detailReq = new ReportInfoReq();
        debug("hosId:" + hosId);
        detailReq.setHosId(hosId);
        debug("reportId:" + reportId);
        detailReq.setReportId(reportId);
        //detailReq.setHospPatientId("");

        return detailReq;
    }


}
