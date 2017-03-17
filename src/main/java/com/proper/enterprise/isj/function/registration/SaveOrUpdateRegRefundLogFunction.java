package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.saveOrUpdateRegRefundLog(RegistrationDocument,
 * RegistrationRefundLogDocument, String, String, String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveOrUpdateRegRefundLogFunction implements IFunction<Object> {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Override
    public Object execute(Object... params) throws Exception {
        saveOrUpdateRegRefundLog((RegistrationDocument) params[0], (RegistrationRefundLogDocument) params[1],
                (String) params[2], (String) params[3], (String) params[4]);
        return null;
    }

    public void saveOrUpdateRegRefundLog(RegistrationDocument regBack, RegistrationRefundLogDocument refundLogDocument,
            String cancelRegStatus, String refundStatus, String refundHisStatus) {
        if (refundLogDocument != null) {
            refundLogDocument.setNum(regBack.getNum());
            refundLogDocument.setOrderNum(regBack.getOrderNum());
            refundLogDocument.setPayChannelId(regBack.getPayChannelId());
            refundLogDocument.setRegistrationId(regBack.getId());
            refundLogDocument.setCancelRegStatus(cancelRegStatus);
            refundLogDocument.setRefundStatus(refundStatus);
            refundLogDocument.setRefundHisStatus(refundHisStatus);

            toolkitx.executeRepositoryFunction(RegistrationRefundLogDocument.class, "save", refundLogDocument);
        }
    }

}
