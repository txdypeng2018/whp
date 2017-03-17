package com.proper.enterprise.isj.function.payment;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.QueryType;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;
import com.proper.enterprise.isj.webservices.model.req.PayListReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl.getPayListReq(BasicInfoDocument, String, String,
 * String, String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchPayListReqFunction implements IFunction<PayListReq> {

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public PayListReq execute(Object... params) throws Exception {
        return getPayListReq((BasicInfoDocument) params[0], (String) params[1], (String) params[2], (String) params[3],
                (String) params[4]);
    }

    /**
     * 获取his请求对象.
     *
     * @param basic 用户基本信息对象.
     * @param clinicCode 门诊流水号.
     * @param payStatus 交易状态.
     * @param sDate 开始时间.
     * @param eDate 结束时间.
     * @return 向HIS发送的请求对象.
     */
    public static PayListReq getPayListReq(BasicInfoDocument basic, String clinicCode, String payStatus, String sDate,
            String eDate) {
        String hosId = CenterFunctionUtils.getHosId();
        PayListReq listReq = new PayListReq();
        listReq.setHosId(hosId);
        listReq.setHospPatientId(basic.getId());
        listReq.setIdcardType(IDCardType.IDCARD);
        listReq.setIdcardNo(basic.getIdCard());
        listReq.setCardType(CardType.CARD);
        listReq.setCardNo(basic.getMedicalNum());
        listReq.setPatientName(basic.getName());
        listReq.setMobile(basic.getPhone());
        if (StringUtil.isNotEmpty(clinicCode)) {
            listReq.setClinicCode(clinicCode);
        }
        if (StringUtil.isNotEmpty(basic.getSexCode())) {
            switch (Integer.parseInt(basic.getSexCode())) {
            case 0:
                listReq.setPatientSex(Sex.FEMALE);
                break;
            case 1:
                listReq.setPatientSex(Sex.MALE);
                break;
            case 2:
                listReq.setPatientSex(Sex.SECRET);
                break;
            case 3:
                listReq.setPatientSex(Sex.OTHERS);
                break;
            default:
                listReq.setPatientSex(Sex.SECRET);
                break;
            }
        } else {
            listReq.setPatientSex(Sex.SECRET);
        }
        String idCard = basic.getIdCard();
        if (StringUtil.isNotEmpty(idCard)) {
            listReq.setPatientAge(IdcardUtils.getAgeByIdCard(idCard));
        } else {
            listReq.setPatientAge(0);
        }
        if (StringUtil.isEmpty(payStatus)) {
            listReq.setQueryType(QueryType.ALL);
        } else if (payStatus.equals(String.valueOf(0))) {
            listReq.setQueryType(QueryType.TO_PAY);
        } else if (payStatus.equals(String.valueOf(1))) {
            listReq.setQueryType(QueryType.PAYED);
        } else if (payStatus.equals(String.valueOf(2))) {
            listReq.setQueryType(QueryType.REFUND);
        }

        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        calS.setTime(new Date());
        calE.setTime(new Date());
        if (StringUtil.isEmpty(sDate)) {
            calS.add(Calendar.MONTH, -6);
        } else {
            calS.setTime(DateUtil.toDate(sDate));
        }
        if (StringUtil.isNotEmpty(eDate)) {
            calE.setTime(DateUtil.toDate(eDate));
        }
        listReq.setBeginDate(calS.getTime());
        listReq.setEndDate(calE.getTime());
        return listReq;
    }

}