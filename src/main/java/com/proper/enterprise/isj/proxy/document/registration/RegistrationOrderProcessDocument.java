package com.proper.enterprise.isj.proxy.document.registration;

import java.io.Serializable;

/**
 * Created by think on 2016/12/13 0013. 挂号流程状态
 */
public class RegistrationOrderProcessDocument implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * 步骤图标
     */
    private String img;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 详细信息
     */
    private String detail;

    /**
     * 步骤状态
     */
    private String status;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
