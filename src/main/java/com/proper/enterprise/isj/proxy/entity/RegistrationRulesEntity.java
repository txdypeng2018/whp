package com.proper.enterprise.isj.proxy.entity;

import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.support.VersionEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 挂号规则对象
 */
public class RegistrationRulesEntity implements Serializable {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 总数
     */
    private int count;
    /**
     * 挂号规则列表
     */
    private List<RuleEntity> data = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<RuleEntity> getData() {
        return data;
    }

    public void setData(List<RuleEntity> data) {
        this.data = data;
    }
}
