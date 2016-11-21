package com.proper.enterprise.isj.webservices.model.req;

import com.proper.enterprise.isj.webservices.utils.CDATAAdapter;
import com.proper.enterprise.isj.webservices.utils.ReqEncryptedAdapter;
import com.proper.enterprise.isj.webservices.utils.SignAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

@XmlRootElement(name = "ROOT")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReqModel {

    @XmlElement(name = "FUN_CODE")
    @XmlJavaTypeAdapter(CDATAAdapter.class)
    private String funCode;

    @XmlElement(name = "USER_ID")
    @XmlJavaTypeAdapter(CDATAAdapter.class)
    private String userId;

    @XmlElement(name = "SIGN_TYPE")
    @XmlJavaTypeAdapter(CDATAAdapter.class)
    private String signType = "MD5";

    @XmlElement(name = "SIGN")
    @XmlJavaTypeAdapter(SignAdapter.class)
    private ReqModel sign = this;

    @XmlElement(name = "REQ_ENCRYPTED")
    @XmlJavaTypeAdapter(ReqEncryptedAdapter.class)
    private Map<String, Object> req;

    public String getFunCode() {
        return funCode;
    }

    public void setFunCode(String funCode) {
        this.funCode = funCode;
    }

    public Map<String, Object> getReq() {
        return req;
    }

    public void setReq(Map<String, Object> req) {
        this.req = req;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public ReqModel getSign() {
        return sign;
    }

    public void setSign(ReqModel sign) {
        this.sign = sign;
    }
}
