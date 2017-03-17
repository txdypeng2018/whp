package com.proper.enterprise.isj.function.registration;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.Channel;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.IsReg;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl.convertRegistration2OrderReg(RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class ConvertRegistration2OrderRegFunction implements IFunction<OrderRegReq> {

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public OrderRegReq execute(Object... params) throws Exception {
        return convertRegistration2OrderReg((RegistrationDocument) params[0]);
    }

    /**
     * 转换当日挂号对象为his当日挂号请求对象.
     *
     * @param reg 当日挂号对象.
     * @return his当日挂号请求对象.
     */
    public static OrderRegReq convertRegistration2OrderReg(RegistrationDocument reg) {
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(DateUtil.toDate(DateUtil.toDateString(new Date())));
        OrderRegReq orderReg = new OrderRegReq();
        orderReg.setLockId("1");
        orderReg.setOrderId(reg.getOrderNum());
        // orderReg.setHospPatientId(reg.getPatientId());
        orderReg.setHospPatientId("");
        orderReg.setChannelId(Channel.APP);
        if (reg.getIsAppointment().equals(String.valueOf(0))) {
            orderReg.setIsReg(IsReg.TODAY);
        } else {
            orderReg.setIsReg(IsReg.APPOINT_DIR);
        }
        orderReg.setRegId(reg.getRegId());
        orderReg.setRegLevel(reg.getRegLevelCode());
        orderReg.setHosId(reg.getHospitalId());
        orderReg.setDeptId(reg.getDeptId());
        orderReg.setDoctorId(reg.getDoctorId());
        orderReg.setRegDate(DateUtil.toDate(reg.getRegDate()));
        orderReg.setTimeFlag(reg.getTimeFlag());
        orderReg.setBeginTime(reg.getBeginTime());
        orderReg.setEndTime(reg.getEndTime());
        orderReg.setRegFee(reg.getRegFee());
        orderReg.setTreatFee(reg.getTreatFee());
        orderReg.setRegType(reg.getRegType());
        orderReg.setIdcardType(IDCardType.IDCARD);
        orderReg.setIdcardNo(reg.getPatientIdCard());
        orderReg.setCardType(CardType.CARD);
        orderReg.setCardNo(reg.getPatientCardNo());
        // orderReg.setCardNo("");
        orderReg.setName(reg.getPatientName());
        orderReg.setSex(reg.getPatientSex());
        orderReg.setBirthday(DateUtil.toDate(reg.getPatientBirthday()));
        orderReg.setMobile(reg.getPatientPhone());
        orderReg.setOperIdcardType(IDCardType.IDCARD);
        orderReg.setOperIdcardNo(reg.getOperatorCardNo());
        orderReg.setOperName(reg.getOperatorName());
        orderReg.setOperMobile(reg.getOperatorPhone());
        orderReg.setOrderTime(Timestamp.valueOf(reg.getCreateTime()));
        orderReg.setAddress("");
        orderReg.setAgentId("");
        return orderReg;
    }

}