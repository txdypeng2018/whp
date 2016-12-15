package com.proper.enterprise.isj.rule.entity;

import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ISJ_RULES")
@CacheEntity
public class RuleEntity extends BaseEntity {

    /**
     * 规则描述，使用 Spring EL 表达式描述
     */
    @Column(nullable = false, length = 4000)
    private String rule;

    /**
     * 规则名称
     */
    @Column(name = "RULE_NAME")
    private String name;

    /**
     * 规则分类
     */
    private String catalogue;

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(String catalogue) {
        this.catalogue = catalogue;
    }

}