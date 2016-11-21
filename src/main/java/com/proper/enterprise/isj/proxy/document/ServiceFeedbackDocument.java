package com.proper.enterprise.isj.proxy.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 反馈意见查询数据
 */
public class ServiceFeedbackDocument  implements Serializable {
    private static final long serialVersionUID = -1l;
    /**
     * 总数
     */
    private int count;
    /**
     * 反馈意见列表
     */
    private List<ServiceUserOpinionDocument> data = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ServiceUserOpinionDocument> getData() {
        return data;
    }

    public void setData(List<ServiceUserOpinionDocument> data) {
        this.data = data;
    }
}
