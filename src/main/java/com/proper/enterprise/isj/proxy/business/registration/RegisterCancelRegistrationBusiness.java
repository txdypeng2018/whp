package com.proper.enterprise.isj.proxy.business.registration;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class RegisterCancelRegistrationBusiness<M extends MapParamsContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    RegistrationService registrationService;

    @Override
    public void process(M ctx) throws Exception {
        Map<String, String> regMap = ctx.getParams();
        String resultMsg = "";
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            String registrationId = regMap.get("registrationId");
            if (registrationId != null) {
                RegistrationDocument reg = registrationService.getRegistrationDocumentById(registrationId);
                if (reg == null) {
                    resultMsg = CenterFunctionUtils.REG_GET_DATA_NULL;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    if (CenterFunctionUtils.checkRegCanBack(reg)) {
                        if (reg.getRegistrationOrderHis() == null
                                || StringUtil.isEmpty(reg.getRegistrationOrderHis().getHospOrderId())) {
                            resultMsg = CenterFunctionUtils.CANCELREG_OLD_ORDER;
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } else {
                            registrationService.saveCancelRegistration(registrationId,
                                    OrderCancelTypeEnum.CANCEL_HANDLE);
                        }
                    } else {
                        resultMsg = CenterFunctionUtils.CANCELREG_ISTODAY_ERR;
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                }
            } else {
                resultMsg = CenterFunctionUtils.REG_GET_DATA_NULL;
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            resultMsg = HIS_DATALINK_ERR;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            resultMsg = e.getMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (RegisterException e) {
            debug("接口内异常", e);
            resultMsg = e.getMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            debug("系统错误", e);
            resultMsg = APP_SYSTEM_ERR;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ctx.setResult(new ResponseEntity<>(resultMsg, httpStatus));
    }
}
