package com.proper.enterprise.platform.auth.jwt.service.impl;

import com.proper.enterprise.isj.user.model.IHosJWTPayload;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.jwt.model.JWTHeader;
import com.proper.enterprise.platform.auth.jwt.model.JWTPayload;
import com.proper.enterprise.platform.auth.jwt.service.JWTAuthcService;
import com.proper.enterprise.platform.auth.jwt.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Primary
public class IHosJWTAuthcServiceImpl implements JWTAuthcService {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    public String getUserToken(String username) throws IOException {
        User user = userService.getByUsername(username);
        JWTHeader header = new JWTHeader();
        header.setId(user.getId());
        header.setName(user.getUsername());
        // APP user have no role, should clear token when login on another device
        boolean hasRole = user.isSuperuser() || !user.getRoles().isEmpty();
        if (!hasRole) {
            jwtService.clearToken(header);
        }
        JWTPayload payload = new IHosJWTPayload(hasRole);
        return jwtService.generateToken(header, payload);
    }

}
