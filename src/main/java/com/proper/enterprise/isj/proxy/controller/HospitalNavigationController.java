package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_PARAM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

/**
 * 医院导航相关Controller.
 */
@RestController
@RequestMapping(path = "/hospitalNavigation")
public class HospitalNavigationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalNavigationController.class);

    @Autowired
    NavInfoRepository navRepo;

    @Autowired
    HospitalNavigationService navService;

    /**
     * 取得各院区楼列表.
     *
     * @param districtId 院区ID（没有时返回所有院区的楼）.
     * @return 楼信息.
     */
    @AuthcIgnore
    @RequestMapping(path = "/builds", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getBuilds(@RequestParam(required = false) String districtId) throws Exception {
        List<Map<String, Object>> retList = navService.getAppDistrictsList(districtId);
        return responseOfGet(retList);
    }

    /**
     * 取得各楼层介绍列表.
     *
     * @param buildId 楼ID.
     * @return 楼层信息及包含科室.
     */
    @AuthcIgnore
    @RequestMapping(path = "/builds/floors", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getFloor(@RequestParam String buildId) throws Exception {
        List<Map<String, Object>> retList = new ArrayList<>();
        if (StringUtil.isNotEmpty(buildId)) {
            retList = navService.getAppFloorList(buildId);
        }
        return responseOfGet(retList);
    }

    /**
     * Web端取得各院区楼列表.
     *
     * @param districtCode 院区ID.
     * @param buildingName 楼宇名称.
     * @param pageNo       当前页码.
     * @param pageSize     每页数量.
     * @return opinionList
     * 意见列表.
     * @throws Exception 异常.
     */
    @GetMapping(path = "/build")
    public ResponseEntity<NavigationBuildEntity> getBuildInfo(@RequestParam(required = false) String districtCode, @RequestParam(required = false) String buildingName, @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {

        // 取得Web端各院区楼列表
        NavigationBuildEntity buildInfo = navService.getWebBuildInfo(districtCode, buildingName, pageNo, pageSize);
        return responseOfGet(buildInfo);
    }

    /**
     * Web端取得楼层科室列表.
     *
     * @param floorParentId 楼层所在楼宇ID.
     * @param deptName      科室名称.
     * @param pageNo        当前页码.
     * @param pageSize      每页数量.
     * @return opinionList
     * 意见列表.
     * @throws Exception 异常.
     */
    @GetMapping(path = "/floor")
    public ResponseEntity<NavigationFloorEntity> getFloorInfo(@RequestParam(required = false) String floorParentId, @RequestParam String deptName, @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {
        NavigationFloorEntity floorInfo = new NavigationFloorEntity();
        if (StringUtil.isNotEmpty(floorParentId)) {
            // 取得Web端楼层科室列表
            floorInfo = navService.getWebFloorInfo(floorParentId, deptName, pageNo, pageSize);
        }
        return responseOfGet(floorInfo);
    }

    /**
     * 新增Web端楼宇信息.
     *
     * @param buildInfo 楼宇对象.
     * @return retValue.
     * @throws Exception 异常.
     */
    @PostMapping(path = "/build", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveBuildInfo(@RequestBody NavigationBuildDetailEntity buildInfo) throws Exception {
        String retValue = "";
        try {
            if (StringUtil.isNotEmpty(buildInfo.getDistrictCode())) {
                navService.saveWebBuildInfo(buildInfo);
            } else {
                throw new RuntimeException(APP_PARAM_ERR);
            }
        } catch (Exception e) {
            LOGGER.debug("HospitalNavigationController.saveBuildInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        return responseOfPost(retValue);
    }

    /**
     * 新增Web端楼层科室信息.
     *
     * @param floorInfo 楼宇对象.
     * @return retValue.
     * @throws Exception 异常.
     */
    @PostMapping(path = "/floor", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveFloorInfo(@RequestBody NavigationFloorDetailEntity floorInfo) throws Exception {
        String retValue = "";
        try {
            if (StringUtil.isNotEmpty(floorInfo.getFloorParentId())) {
                navService.saveWebFloorInfo(floorInfo);
            } else {
                throw new RuntimeException(APP_PARAM_ERR);
            }
        } catch (Exception e) {
            LOGGER.debug("HospitalNavigationController.saveFloorInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        return responseOfPost(retValue);
    }

    /**
     * 更新Web端楼宇信息.
     *
     * @param buildInfo 楼宇对象.
     * @return retValue.
     * @throws Exception 异常.
     */
    @PutMapping(path = "/build", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateBuildInfo(@RequestBody NavigationBuildDetailEntity buildInfo) throws Exception {
        String retValue = "";
        try {
            if (StringUtil.isNotNull(buildInfo.getDistrictCode())) {
                navService.updateWebBuildInfo(buildInfo);
            } else {
                throw new RuntimeException(APP_PARAM_ERR);
            }
        } catch (Exception e) {
            LOGGER.debug("HospitalNavigationController.updateBuildInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        return responseOfPut(retValue);
    }

    /**
     * 更新Web端楼层科室信息.
     *
     * @param floorInfo 楼层科室信息.
     * @return retValue.
     * @throws Exception 异常.
     */
    @PutMapping(path = "/floor", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateFloorInfo(@RequestBody NavigationFloorDetailEntity floorInfo) throws Exception {
        String retValue = "";
        try {
            navService.updateWebFloorInfo(floorInfo);
        } catch (Exception e) {
            LOGGER.debug("HospitalNavigationController.updateFloorInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        return responseOfPut(retValue);
    }

    /**
     * 删除Web端楼宇信息.
     *
     * @param ids id列表.
     * @return retValue.
     * @throws Exception 异常.
     */
    @DeleteMapping(path = "/build")
    public ResponseEntity deleteBuildInfo(@RequestParam String ids) throws Exception {
        boolean retValue = false;
        if (StringUtil.isNotNull(ids)) {
            String[] idArr = ids.split(",");
            List<String> idList = new ArrayList<>();
            Collections.addAll(idList, idArr);
            try {
                navService.deleteWebNavInfo(idList);
                retValue = true;
            } catch (Exception e) {
                LOGGER.debug("HospitalNavigationController.deleteBuildInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR, e);
            }
        }
        return responseOfDelete(retValue);
    }

    /**
     * 删除Web端楼层科室信息.
     *
     * @param ids id列表.
     * @return retValue.
     * @throws Exception 异常.
     */
    @DeleteMapping(path = "/floor")
    public ResponseEntity deleteFloorInfo(@RequestParam(required = true) String ids) throws Exception {
        boolean retValue = false;
        if (StringUtil.isNotNull(ids)) {
            String[] idArr = ids.split(",");
            List<String> idList = new ArrayList<>();
            Collections.addAll(idList, idArr);
            try {
                navService.deleteWebNavInfo(idList);
                retValue = true;
            } catch (Exception e) {
                LOGGER.debug("HospitalNavigationController.deleteFloorInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR, e);
            }
        }
        return responseOfDelete(retValue);
    }

    /**
     * 取得指定Web端楼宇信息.
     *
     * @param buildingId 楼宇ID.
     * @return buildInfo.
     */
    @GetMapping(path = "/build/{buildingId}")
    public ResponseEntity<NavigationBuildDetailEntity> getBuildDetailInfo(@PathVariable String buildingId) throws Exception {
        NavigationBuildDetailEntity buildInfo = new NavigationBuildDetailEntity();
        if (StringUtil.isNotEmpty(buildingId)) {
            buildInfo = navService.getWebBuildById(buildingId);
        }
        return responseOfGet(buildInfo);
    }

    /**
     * 取得指定Web端楼层科室信息.
     *
     * @param floorId 楼层ID.
     * @return floorInfo.
     */
    @GetMapping(path = "/floor/{floorId}")
    public ResponseEntity<NavigationFloorDetailEntity> getFloorDetailInfo(@PathVariable String floorId) throws Exception {
        NavigationFloorDetailEntity floorInfo = new NavigationFloorDetailEntity();
        if (StringUtil.isNotEmpty(floorId)) {
            floorInfo = navService.getWebFloorById(floorId);
        }
        return responseOfGet(floorInfo);
    }

}
