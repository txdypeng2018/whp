package com.proper.enterprise.isj.proxy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 医院导航楼宇对象
 */
public class NavigationBuildEntity implements Serializable {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 总数
     */
    private int count;

    /**
     * 反馈意见列表
     */
    private List<NavigationBuildDetailEntity> data = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<NavigationBuildDetailEntity> getData() {
        return data;
    }

    public void setData(List<NavigationBuildDetailEntity> data) {
        this.data = data;
    }
}
