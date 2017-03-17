package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by think on 2016/8/16 0016.
 */
public class HospitalNavigationDocument extends BaseDocument{

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 院区ID
     */
    private String id;
    /**
     * 院区名称
     */
    private String name;
    /**
     * 楼列表
     */
    private List<BuildDocument> buiders = new ArrayList<BuildDocument>();

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

    public List<BuildDocument> getBuiders() {
        return buiders;
    }

    public void setBuiders(List<BuildDocument> buiders) {
        this.buiders = buiders;
    }
}
