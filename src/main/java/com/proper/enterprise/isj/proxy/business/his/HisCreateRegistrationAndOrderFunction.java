package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.registration.SaveOrderAndUpdateRegDocFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationConcession;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.isj.webservices.model.res.OrderReg;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.orderreg.Concession;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;

@Service
public class HisCreateRegistrationAndOrderFunction
        implements IFunction<RegistrationDocument>, ILoggable {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    RegistrationRepository registrationRepository;
    
    @Autowired
    @Qualifier("defaultBusinessToolkit")
    protected BusinessToolkit toolkit;
    
    public RegistrationDocument createRegAndOrder(RegistrationDocument saveReg, String isAppointment) throws Exception{

        OrderRegReq orderReg = toolkit.executeFunction(SaveOrderAndUpdateRegDocFunction.class, saveReg);
        // 预约挂号
        if (isAppointment.equals("1")) {
            // 调用HIS的接口将订单信息同步到HIS
            ResModel<OrderReg> ordrRegModel = webServicesClient.orderReg(orderReg);
            // 调用HIS订单同步失败
            if (ordrRegModel.getReturnCode() != ReturnCode.SUCCESS) {
                debug("调用HIS的orderReg接口返回异常:{}", ordrRegModel.getReturnMsg());
                throw new HisReturnException(ordrRegModel.getReturnMsg());
            }
            // 保存HIS响应信息
            RegistrationOrderHisDocument orderHis = new RegistrationOrderHisDocument();
            BeanUtils.copyProperties(ordrRegModel.getRes(), orderHis);
            if (ordrRegModel.getRes().getConcessions() != null) {
                List<Concession> conList = ordrRegModel.getRes().getConcessions();
                List<RegistrationConcession> regConList = new ArrayList<>();
                RegistrationConcession regCon;
                for (Concession concession : conList) {
                    regCon = new RegistrationConcession();
                    BeanUtils.copyProperties(concession, regCon);
                    regConList.add(regCon);
                }
                orderHis.setRegistrationConcession(regConList);
            }
            saveReg.setRegistrationOrderHis(orderHis);
            registrationRepository.save(saveReg);
        }
        return saveReg;
    }


    @Override
    public RegistrationDocument execute(Object... params) throws Exception {
        return createRegAndOrder((RegistrationDocument)params[0], (String)params[1]);
    }
}
