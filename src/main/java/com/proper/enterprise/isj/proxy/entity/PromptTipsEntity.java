package com.proper.enterprise.isj.proxy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 温馨提示对象
 */
public class PromptTipsEntity implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * 总数
     */
    private int count;
    /**
     * 反馈意见列表
     */
    private List<BaseInfoEntity> data = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BaseInfoEntity> getData() {
        return data;
    }

    public void setData(List<BaseInfoEntity> data) {
        this.data = data;
    }
}