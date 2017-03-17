package com.proper.enterprise.isj.proxy.entity;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 基本信息
 */
@Entity
@Table(name = "ISJ_BASE_INFO")
@CacheEntity
public class BaseInfoEntity extends BaseEntity {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 信息类型
     */
    @Column(nullable = false, unique = true)
    private String infoType = "";

    /**
     * 基本信息
     */
    @Column(nullable = false, length = 4000)
    private String info = "";

    /**
     * 名称
     */
    @Column(nullable = false)
    private String typeName = "";

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
