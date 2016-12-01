package com.proper.enterprise.isj.webservices.utils;

import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

public class ReqEncryptedAdapter extends XmlAdapter<String, Map<String, Object>> {

    private static final com.proper.enterprise.platform.core.utils.cipher.AES AES;

    static {
        AES = new com.proper.enterprise.platform.core.utils.cipher.AES(
                ConfCenter.get("isj.his.aes.mode"),
                ConfCenter.get("isj.his.aes.padding"),
                ConfCenter.get("isj.his.aes.key"));
    }

    @Override
    public Map<String, Object> unmarshal(String v) throws Exception {
        return null;
    }

    @Override
    public String marshal(Map<String, Object> v) throws Exception {
        return marshal(v, true);
    }

    public String marshal(Map<String, Object> v, boolean needCDATA) throws Exception {
        String str = "<REQ>" + iterateCollection(v) + "</REQ>";
        String result = new String(AES.encrypt(str.getBytes(PEPConstants.DEFAULT_CHARSET)), PEPConstants.DEFAULT_CHARSET);
        return needCDATA ? CDATAAdapter.wrapCDATA(result) : result;
    }

    private String iterateCollection(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                sb.append(MessageFormat.format(ConfCenter.get("isj.his.template.req"), entry.getKey(), entry.getValue()));
            } else if (entry.getValue() instanceof Collection) {
                for (Object ele : (Collection) entry.getValue()) {
                    if (ele instanceof Map) {
                        sb.append("<" + entry.getKey() + ">")
                          .append(iterateCollection((Map<String, Object>) ele))
                          .append("</" + entry.getKey() + ">");
                    }
                }
            }
        }
        return sb.toString();
    }

}
