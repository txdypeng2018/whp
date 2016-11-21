package com.proper.enterprise.platform.auth.service.impl;

import com.proper.enterprise.platform.api.auth.service.PasswordEncryptService;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.digest.MD5;
import com.proper.enterprise.platform.core.utils.digest.SHA;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Primary
@Service
public class ISJPasswordEncryptServiceImpl implements PasswordEncryptService {

    @Override
    public String encrypt(String password) {
        Assert.notNull(password, "Password should NOT NULL!");
        String reversePwd = StringUtil.reverse(password);
        String md5 = MD5.md5Hex(reversePwd);
        return SHA.sha256(md5.substring(16, 32) + password + md5.substring(0, 16));
    }

}
