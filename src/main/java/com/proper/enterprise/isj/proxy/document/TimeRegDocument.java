package com.proper.enterprise.isj.proxy.document;

import java.io.Serializable;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * Created by think on 2016/8/31 0031.
 * 
 */
public class TimeRegDocument implements Serializable {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    private String id;

    /**
     * 时间", "example": "09:10"
     */
    private String time;

    /**
     * 可挂号总数
     */
    private int total = 99;

    /**
     * 剩余挂号数
     */
    private int overCount = 99;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOverCount() {
        return overCount;
    }

    public void setOverCount(int overCount) {
        this.overCount = overCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
