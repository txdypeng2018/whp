package com.proper.enterprise.isj.proxy.business.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.HttpServletRequestContext;
import com.proper.enterprise.platform.auth.jwt.service.JWTService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class UserInfoTokenValBusiness<M extends HttpServletRequestContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {
    @Autowired
    JWTService jwtService;

    @Override
    public void process(M ctx) throws Exception {
        HttpServletRequest request = ctx.getRequest();
        String token = jwtService.getTokenFromHeader(request);
        try {
            boolean flag = this.isTokenVerify(token);
            if (flag) {
                ctx.setResult(new ResponseEntity<>("", HttpStatus.OK));
            } else {
                ctx.setResult(new ResponseEntity<>("", HttpStatus.UNAUTHORIZED));
            }
        } catch (Exception e) {
            LOGGER.debug("获得token异常", e);
            ctx.setResult(new ResponseEntity<>("", HttpStatus.UNAUTHORIZED));
        }
    }

    /**
     * 检验token.
     *
     * @param token token.
     * @return 检验结果.
     * @throws IOException 异常.
     */
    private boolean isTokenVerify(String token) throws IOException {
        boolean tokenVerify = true;
        try {
            tokenVerify = jwtService.verify(token);
        } catch (IOException e) {
            LOGGER.debug("校验token异常", e);
            throw e;
        }
        return tokenVerify;
    }
}
