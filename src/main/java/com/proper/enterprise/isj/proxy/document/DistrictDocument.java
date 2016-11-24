package com.proper.enterprise.isj.proxy.document;

import java.io.Serializable;

/**
 * Created by think on 2016/8/16 0016.
 * 院区
 */
public class DistrictDocument implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * 院区ID
     */
    private String id;
    /**
     * 院区名称
     */
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}