package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/8/16 0016.
 */
public class BuildDocument extends BaseDocument {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 楼ID
     */
    private String id;
    /**
     * 楼名称
     */
    private String name;
    /**
     * 楼功能
     */
    private String function;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
