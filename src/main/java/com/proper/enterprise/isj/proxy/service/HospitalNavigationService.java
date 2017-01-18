package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorEntity;

import java.util.List;
import java.util.Map;

/**
 * 医院导航Service.
 */
public interface HospitalNavigationService {

    List<Map<String, Object>> getAppDistrictsList(String districtId) throws Exception;

    List<Map<String, Object>> getAppFloorList(String buildId) throws Exception;

    void saveWebBuildInfo(NavigationBuildDetailEntity buildInfo) throws Exception;

    void saveWebFloorInfo(NavigationFloorDetailEntity floorInfo) throws Exception;

    void updateWebBuildInfo(NavigationBuildDetailEntity buildInfo) throws Exception;

    void updateWebFloorInfo(NavigationFloorDetailEntity floorInfo) throws Exception;

    void deleteWebNavInfo(List<String> idList) throws Exception;

    NavigationBuildDetailEntity getWebBuildById(String buildingId) throws Exception;

    NavigationFloorDetailEntity getWebFloorById(String floorId) throws Exception;

    NavigationBuildEntity getWebBuildInfo(String districtCode, String buildingName,
                                          String pageNo, String pageSize) throws Exception;

    public NavigationFloorEntity getWebFloorInfo(String floorParentId, String deptName,
                                                 String pageNo, String pageSize) throws Exception;

}
