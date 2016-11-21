package com.proper.enterprise.isj.proxy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 医院导航楼层科室对象
 */
public class NavigationFloorEntity implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * 总数
     */
    private int count;

    /**
     * 反馈意见列表
     */
    private List<NavigationFloorDetailEntity> data = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<NavigationFloorDetailEntity> getData() {
        return data;
    }

    public void setData(List<NavigationFloorDetailEntity> data) {
        this.data = data;
    }
}
