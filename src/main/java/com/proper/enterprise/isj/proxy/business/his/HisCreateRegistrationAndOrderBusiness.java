package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IsAppointmentContext;
import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.registration.SaveOrderAndUpdateRegDocFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationConcession;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.isj.webservices.model.res.OrderReg;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.orderreg.Concession;

@Service
public class HisCreateRegistrationAndOrderBusiness<M extends RegistrationDocumentContext<RegistrationDocument> & IsAppointmentContext<RegistrationDocument>>
        implements IBusiness<RegistrationDocument, M>, ILoggable {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    RegistrationRepository registrationRepository;
    
    @Autowired
    SaveOrderAndUpdateRegDocFunction saveOrderAndUpdateRegDocFunction;

    @Override
    public void process(M ctx) throws Exception {

        
        RegistrationDocument saveReg = ctx.getRegistrationDocument();
        String isAppointment = ctx.getIsAppointment();

        OrderRegReq orderReg = FunctionUtils.invoke(saveOrderAndUpdateRegDocFunction, saveReg);
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
        ctx.setRegistrationDocument(saveReg);
    }
}
