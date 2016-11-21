package com.proper.enterprise.isj.proxy.entity;

import java.io.Serializable;

/**
 * 医院导航楼宇对象
 */
public class NavigationBuildDetailEntity implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * ID
     */
    private String id;

    /**
     * 院区ID
     */
    private String districtCode;

    /**
     * 院区名称
     */
    private String districtName;

    /**
     * 楼宇ID
     */
    private String buildingCode;

    /**
     * 楼宇名称
     */
    private String buildingName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
