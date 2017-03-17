package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

import java.util.List;

/**
 * Created by think on 2016/8/16 0016.
 */
public class FloorDocument extends BaseDocument {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 楼层Id
     */
    private String id;
    /**
     * 楼层名称
     */
    private String name;
    /**
     * 层内科室
     */
    private List<String> depts;

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

    public List<String> getDepts() {
        return depts;
    }

    public void setDepts(List<String> depts) {
        this.depts = depts;
    }
}
