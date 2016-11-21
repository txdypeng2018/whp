package com.proper.enterprise.isj.webservices.utils;

import com.proper.enterprise.isj.webservices.model.req.ReqModel;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.digest.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.MessageFormat;

public class SignAdapter extends XmlAdapter<String, ReqModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignAdapter.class);

    @Override
    public ReqModel unmarshal(String v) throws Exception {
        return null;
    }

    @Override
    public String marshal(ReqModel v) throws Exception {
        String sign = MessageFormat.format(ConfCenter.get("isj.his.template.sign.req"),
                v.getFunCode(),
                new ReqEncryptedAdapter().marshal(v.getReq(), false),
                v.getUserId(),
                ConfCenter.get("isj.his.aes.key"));

        LOGGER.debug("Sign before MD5: {}",
                StringUtil.abbreviate(sign,
                        Integer.parseInt(ConfCenter.get("isj.abbreviate.maxWidth"))));
        // 东软接口只识别大写 MD5
        return CDATAAdapter.wrapCDATA(MD5.md5Hex(sign).toUpperCase());
    }

}
