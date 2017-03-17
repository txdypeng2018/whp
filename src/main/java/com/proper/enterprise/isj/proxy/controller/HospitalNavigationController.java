package com.proper.enterprise.isj.proxy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.BuildIdContext;
import com.proper.enterprise.isj.context.BuildInfoEntityContext;
import com.proper.enterprise.isj.context.BuildingIdContext;
import com.proper.enterprise.isj.context.BuildingNameContext;
import com.proper.enterprise.isj.context.DeptNameContext;
import com.proper.enterprise.isj.context.DistrictCodeContext;
import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.FloorIdContext;
import com.proper.enterprise.isj.context.FloorInfoEntityContext;
import com.proper.enterprise.isj.context.FloorParentIdContext;
import com.proper.enterprise.isj.context.IdsContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavDeleteBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavDeleteFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavGetBuildDetailInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavGetBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavGetBuildsBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavGetFloorBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavGetFloorDetailInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavGetFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavSaveFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavUpdateBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalNavUpdateFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalSaveBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorEntity;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 医院导航相关Controller.
 */
@RestController
@RequestMapping(path = "/hospitalNavigation")
public class HospitalNavigationController extends IHosBaseController {

    /**
     * 取得各院区楼列表.
     *
     * @param districtId 院区ID（没有时返回所有院区的楼）.
     * @return 楼信息.
     */
    @SuppressWarnings("unchecked")
    @AuthcIgnore
    @RequestMapping(path = "/builds", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getBuilds(@RequestParam(required = false) String districtId)
            throws Exception {
        return responseOfGet(toolkit.execute(HospitalNavGetBuildsBusiness.class,
                c -> ((DistrictIdContext<List<Map<String, Object>>>) c).setDistrictId(districtId)));
    }

    /**
     * 取得各楼层介绍列表.
     *
     * @param buildId 楼ID.
     * @return 楼层信息及包含科室.
     */
    @SuppressWarnings("unchecked")
    @AuthcIgnore
    @RequestMapping(path = "/builds/floors", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getFloor(@RequestParam String buildId) throws Exception {
        return responseOfGet(toolkit.execute(HospitalNavGetFloorBusiness.class,
                c -> ((BuildIdContext<List<Map<String, Object>>>) c).setBuildId(buildId)));

    }

    /**
     * Web端取得各院区楼列表.
     *
     * @param districtCode 院区ID.
     * @param buildingName 楼宇名称.
     * @param pageNo 当前页码.
     * @param pageSize 每页数量.
     * @return opinionList
     *         意见列表.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/build")
    public ResponseEntity<NavigationBuildEntity> getBuildInfo(@RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String buildingName, @RequestParam String pageNo,
            @RequestParam String pageSize) throws Exception {
        return responseOfGet(toolkit.execute(HospitalNavGetBuildInfoBusiness.class, c -> {
            ((DistrictCodeContext<NavigationBuildEntity>) c).setDistrictCode(districtCode);
            ((BuildingNameContext<NavigationBuildEntity>) c).setBuildingName(buildingName);
            ((PageNoContext<NavigationBuildEntity>) c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<NavigationBuildEntity>) c).setPageSize(Integer.parseInt(pageSize));
        }));
    }

    /**
     * Web端取得楼层科室列表.
     *
     * @param floorParentId 楼层所在楼宇ID.
     * @param deptName 科室名称.
     * @param pageNo 当前页码.
     * @param pageSize 每页数量.
     * @return opinionList
     *         意见列表.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/floor")
    public ResponseEntity<NavigationFloorEntity> getFloorInfo(@RequestParam(required = false) String floorParentId,
            @RequestParam String deptName, @RequestParam String pageNo, @RequestParam String pageSize)
            throws Exception {
        return responseOfGet(toolkit.execute(HospitalNavGetFloorInfoBusiness.class, c -> {
            ((FloorParentIdContext<NavigationFloorEntity>) c).setFloorParentId(floorParentId);
            ((DeptNameContext<NavigationFloorEntity>) c).setDeptName(deptName);
            ((PageNoContext<NavigationFloorEntity>) c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<NavigationFloorEntity>) c).setPageSize(Integer.parseInt(pageSize));
        }));
    }

    /**
     * 新增Web端楼宇信息.
     *
     * @param buildInfo 楼宇对象.
     * @return retValue.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping(path = "/build", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveBuildInfo(@RequestBody NavigationBuildDetailEntity buildInfo) throws Exception {
        return responseOfPost(toolkit.execute(HospitalSaveBuildInfoBusiness.class,
                c -> ((BuildInfoEntityContext<String>) c).setBuildInfo(buildInfo)));
    }

    /**
     * 新增Web端楼层科室信息.
     *
     * @param floorInfo 楼宇对象.
     * @return retValue.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping(path = "/floor", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveFloorInfo(@RequestBody NavigationFloorDetailEntity floorInfo) throws Exception {
        return responseOfPost(toolkit.execute(HospitalNavSaveFloorInfoBusiness.class,
                c -> ((FloorInfoEntityContext<String>) c).setFloorInfo(floorInfo)));
    }

    /**
     * 更新Web端楼宇信息.
     *
     * @param buildInfo 楼宇对象.
     * @return retValue.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping(path = "/build", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateBuildInfo(@RequestBody NavigationBuildDetailEntity buildInfo) throws Exception {
        return responseOfPut(toolkit.execute(HospitalNavUpdateBuildInfoBusiness.class,
                c -> ((BuildInfoEntityContext<String>) c).setBuildInfo(buildInfo)));
    }

    /**
     * 更新Web端楼层科室信息.
     *
     * @param floorInfo 楼层科室信息.
     * @return retValue.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping(path = "/floor", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateFloorInfo(@RequestBody NavigationFloorDetailEntity floorInfo) throws Exception {
        return responseOfPut(toolkit.execute(HospitalNavUpdateFloorInfoBusiness.class,
                c -> ((FloorInfoEntityContext<String>) c).setFloorInfo(floorInfo)));
    }

    /**
     * 删除Web端楼宇信息.
     *
     * @param ids id列表.
     * @return retValue.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @DeleteMapping(path = "/build")
    public ResponseEntity deleteBuildInfo(@RequestParam String ids) throws Exception {
        return responseOfDelete(
                toolkit.execute(HospitalNavDeleteBuildInfoBusiness.class, c -> ((IdsContext<Boolean>) c).setIds(ids)));

    }

    /**
     * 删除Web端楼层科室信息.
     *
     * @param ids id列表.
     * @return retValue.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @DeleteMapping(path = "/floor")
    public ResponseEntity deleteFloorInfo(@RequestParam(required = true) String ids) throws Exception {
        return responseOfDelete(
                toolkit.execute(HospitalNavDeleteFloorInfoBusiness.class, c -> ((IdsContext<Boolean>) c).setIds(ids)));
    }

    /**
     * 取得指定Web端楼宇信息.
     *
     * @param buildingId 楼宇ID.
     * @return buildInfo.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/build/{buildingId}")
    public ResponseEntity<NavigationBuildDetailEntity> getBuildDetailInfo(@PathVariable String buildingId)
            throws Exception {
        return responseOfGet(toolkit.execute(HospitalNavGetBuildDetailInfoBusiness.class,
                c -> ((BuildingIdContext<NavigationBuildDetailEntity>) c).setBuildingId(buildingId)));

    }

    /**
     * 取得指定Web端楼层科室信息.
     *
     * @param floorId 楼层ID.
     * @return floorInfo.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/floor/{floorId}")
    public ResponseEntity<NavigationFloorDetailEntity> getFloorDetailInfo(@PathVariable String floorId)
            throws Exception {
        return responseOfGet(toolkit.execute(HospitalNavGetFloorDetailInfoBusiness.class,
                c -> ((FloorIdContext<NavigationFloorDetailEntity>) c).setFloorId(floorId)));
    }

}
