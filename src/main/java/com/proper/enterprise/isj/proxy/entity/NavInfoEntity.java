package com.proper.enterprise.isj.proxy.entity;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 医院导航信息
 */
@Entity
@Table(name = "ISJ_NAV_INFO")
@CacheEntity
public class NavInfoEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 医院导航使用id
     */
    @Column(nullable = false, unique = true)
    private String navId = "";

    /**
     * 导航页面使用name
     */
    private String navName = "";

    /**
     * 导航页面使用function
     */
    private String navFunction = "";

    /**
     * 导航页面门诊字符串
     */
    private String navDepts = "";

    /**
     * 导航页面父节点id
     */
    private String parentId = "";

    /**
     * 导航页面父节点类型
     */
    private String parentType = "";

    public String getNavId() {
        return navId;
    }

    public void setNavId(String navId) {
        this.navId = navId;
    }

    public String getNavName() {
        return navName;
    }

    public void setNavName(String navName) {
        this.navName = navName;
    }

    public String getNavFunction() {
        return navFunction;
    }

    public void setNavFunction(String navFunction) {
        this.navFunction = navFunction;
    }

    public String getNavDepts() {
        return navDepts;
    }

    public void setNavDepts(String navDepts) {
        this.navDepts = navDepts;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }
}
