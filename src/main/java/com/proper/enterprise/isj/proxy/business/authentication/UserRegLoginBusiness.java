package com.proper.enterprise.isj.proxy.business.authentication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.service.AuthcService;
import com.proper.enterprise.platform.auth.jwt.service.JWTAuthcService;

@Service
public class UserRegLoginBusiness<M extends MapParamsContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    AuthcService authcService;

    @Autowired
    JWTAuthcService jwtAuthcService;

    @Override
    public void process(M ctx) throws Exception {
        Map<String, String> phoneMap = ctx.getParams();
        String phone = phoneMap.get("phone");
        String password = phoneMap.get("password");
        if (!authcService.authenticate(phone, password)) {
            throw new RuntimeException(CenterFunctionUtils.LOGIN_ERROR);
        }

        try {
            ctx.setResult(jwtAuthcService.getUserToken(phone));
        } catch (Exception e) {
            error("Login ERROR!", e);
            throw new RuntimeException(CenterFunctionUtils.LOGIN_ERROR);
        }
    }
}
