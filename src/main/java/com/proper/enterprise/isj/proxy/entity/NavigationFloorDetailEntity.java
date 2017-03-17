package com.proper.enterprise.isj.proxy.entity;

import java.io.Serializable;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 医院导航楼层科室对象
 */
public class NavigationFloorDetailEntity implements Serializable {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 楼层所在楼宇ID
     */
    private String floorParentId;

    /**
     * 楼层记录ID
     */
    private String fid;

    /**
     * 楼层ID
     */
    private String floorId;

    /**
     * 楼层名称
     */
    private String floorName;

    /**
     * 科室记录ID
     */
    private String did;

    /**
     * 楼层科室信息ID
     */
    private String deptId;

    /**
     * 楼层科室信息
     */
    private String deptName;

    public String getFloorParentId() {
        return floorParentId;
    }

    public void setFloorParentId(String floorParentId) {
        this.floorParentId = floorParentId;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
