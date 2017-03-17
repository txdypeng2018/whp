package com.proper.enterprise.isj.proxy.business.customerservice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proper.enterprise.isj.business.BusinessResponse;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.auth.jwt.model.JWTHeader;
import com.proper.enterprise.platform.auth.jwt.service.JWTService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class UserInfoGetUserBusiness<M extends ModifiedResultBusinessContext<Object>> extends BusinessResponse
        implements IBusiness<Object, M>, ILoggable {
    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    JWTService jwtService;

    @Override
    public void process(M ctx) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        UserInfoDocument userInfo = getLoginUserInfoDocument(request);
        if (userInfo == null) {
            ctx.setResult(new ResponseEntity<>(new UserInfoDocument(), HttpStatus.UNAUTHORIZED));
        }
        ctx.setResult(responseOfGet(userInfo));

    }

    // repeat
    private UserInfoDocument getLoginUserInfoDocument(HttpServletRequest request) throws Exception {
        String token = jwtService.getTokenFromHeader(request);
        boolean tokenVerify = isTokenVerify(token);
        UserInfoDocument userInfo;
        if (tokenVerify) {
            JWTHeader header = jwtService.getHeader(token);
            String userId = header.getId();
            userInfo = userInfoServiceImpl.getUserInfoByUserId(userId);
        } else {
            throw new Exception("登录失效");
        }
        return userInfo;
    }

    // repeat
    private boolean isTokenVerify(String token) throws IOException {
        boolean tokenVerify = true;
        try {
            tokenVerify = jwtService.verify(token);
        } catch (IOException e) {
            debug("校验token异常", e);
            throw e;
        }
        return tokenVerify;
    }
}
