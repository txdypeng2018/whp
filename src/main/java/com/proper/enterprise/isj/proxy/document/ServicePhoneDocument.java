package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 客服电话
 */
@Document(collection = "service_phone")
public class ServicePhoneDocument extends BaseDocument {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 电话类别
     */
    private String phoneType;
    /**
     * 客服电话
     */
    private String servicePhone;

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }
}
