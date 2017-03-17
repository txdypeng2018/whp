/**
 * <p>
 * application name: SaveOrUpdateRegistrationByPayStatusFunction.java
 * </p>
 * <p>
 * description:
 * </p>
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * 
 * @author: 王东石<wangdongshi@propersoft.cn>
 * @version Ver 1.0 2017年2月22日 新建
 */

package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.controller.RegisterController;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * .
 * <p>
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */
@Service
public class SaveOrUpdateRegistrationByPayStatusFunction
    implements IFunction<Object>, ILoggable {

    @Autowired
    RegistrationService registrationService;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object... params) throws Exception {
        return saveOrUpdateRegistrationByPayStatus((RegistrationDocument) params[0]);
    }

    /**
     * 检查挂号单支付状态,并更新挂号单.
     *
     * @param registrationDocument 注册报文.
     * @return 应答报文.
     * @see RegisterController#saveOrUpdateRegistrationByPayStatus(RegistrationDocument)
     */
    protected RegistrationDocument saveOrUpdateRegistrationByPayStatus(RegistrationDocument registrationDocument) {
        try {
            if (registrationDocument.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
                if (StringUtil.isEmpty(registrationDocument.getRegistrationOrderHis().getHospPayId())) {
                    registrationDocument = registrationService
                            .saveQueryPayTradeStatusAndUpdateReg(registrationDocument);
                }
            } else if (registrationDocument.getStatusCode().equals(RegistrationStatusEnum.PAID.getValue())) {
                if (registrationDocument.getRegistrationOrderHis() == null
                        || StringUtil.isEmpty(registrationDocument.getRegistrationOrderHis().getHospPayId())) {
                    return registrationDocument;
                }
                if (registrationDocument.getRegistrationTradeRefund() != null) {
                    if (StringUtil.isNotEmpty(registrationDocument.getRegistrationTradeRefund().getOutTradeNo())) {
                        registrationDocument = registrationService
                                .saveQueryRefundTradeStatusAndUpdateReg(registrationDocument);
                    }
                }
            }
        } catch (Exception e) {
            debug("挂号单列表初始化校验是否已付款失败,挂号单号:" + registrationDocument.getNum(), e);
        }
        return registrationDocument;
    }

}
