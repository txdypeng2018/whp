package com.proper.enterprise.isj.proxy.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildInfoEntityContext;
import com.proper.enterprise.isj.context.BuildingIdContext;
import com.proper.enterprise.isj.context.BuildingNameContext;
import com.proper.enterprise.isj.context.DeptNameContext;
import com.proper.enterprise.isj.context.DistrictCodeContext;
import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.FloorIdContext;
import com.proper.enterprise.isj.context.FloorInfoEntityContext;
import com.proper.enterprise.isj.context.FloorParentIdContext;
import com.proper.enterprise.isj.context.NavBuildDetailEntityContext;
import com.proper.enterprise.isj.context.NavFloorDetailEntityContext;
import com.proper.enterprise.isj.context.NavInfoIdsContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.proxy.business.navinfo.DelWebNavInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.FetchAppDistrictsListBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.FetchAppFloorListBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.FetchWebBuildByIdBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.FetchWebBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.FetchWebFloorByIdBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.FetchWebFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.SaveWebBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.SaveWebFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.UpdateWebBuildInfoBusiness;
import com.proper.enterprise.isj.proxy.business.navinfo.UpdateWebFloorInfoBusiness;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * 医院导航ServiceImpl
 */
@Service
public class HospitalNavigationServiceImpl extends AbstractService implements HospitalNavigationService {

    /**
     * 取得手机端院区楼宇列表
     *
     * @param districtId
     *            院区ID
     * @return retList
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getAppDistrictsList(String districtId) throws Exception {
        return toolkit.execute(FetchAppDistrictsListBusiness.class, ctx->
        ((DistrictIdContext<List<Map<String, Object>>>)ctx).setDistrictId(districtId));
    }

    /**
     * 取得手机端指定楼宇各楼层科室信息.
     *
     * @param buildId
     *            楼宇ID.
     * @return retList
     *         手机端指定楼宇各楼层科室信息.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getAppFloorList(String buildId) throws Exception {
        return toolkit.execute(FetchAppFloorListBusiness.class, ctx->((BuildingIdContext<Collection<Map<String, Object>>>)ctx).setBuildingId(buildId));
        
    }

    /**
     * 新增Web端楼宇信息.
     *
     * @param buildInfo
     *            楼宇信息.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void saveWebBuildInfo(NavigationBuildDetailEntity buildInfo) throws Exception {
        toolkit.execute(SaveWebBuildInfoBusiness.class, ctx->((BuildInfoEntityContext<Object>)ctx).setBuildInfo(buildInfo));
    }

    /**
     * 新增Web端楼层科室信息.
     *
     * @param floorInfo
     *            楼层科室信息.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void saveWebFloorInfo(NavigationFloorDetailEntity floorInfo) throws Exception {
        toolkit.execute(SaveWebFloorInfoBusiness.class, ctx->((FloorInfoEntityContext<Object>)ctx).setFloorInfo(floorInfo));
    }

    /**
     * 更新Web端楼宇信息.
     *
     * @param buildInfo
     *            楼宇信息.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateWebBuildInfo(NavigationBuildDetailEntity buildInfo) throws Exception {
        toolkit.execute(UpdateWebBuildInfoBusiness.class, ctx->((NavBuildDetailEntityContext<Object>)ctx).setNavBuildDetail(buildInfo));
    }

    /**
     * 更新Web端楼层科室信息.
     *
     * @param floorInfo
     *            楼层科室信息.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateWebFloorInfo(NavigationFloorDetailEntity floorInfo) throws Exception {
        toolkit.execute(UpdateWebFloorInfoBusiness.class, ctx->((NavFloorDetailEntityContext<Object>)ctx).setNavFloorDetail(floorInfo));
    }

    /**
     * 删除Web端楼宇信息.
     *
     * @param idList
     *            待删除ID列表.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void deleteWebNavInfo(List<String> idList) throws Exception {
        toolkit.execute(DelWebNavInfoBusiness.class, ctx->((NavInfoIdsContext<Object>)ctx).setNavInfoIds(idList));
    }

    /**
     * 取得指定楼宇ID的楼宇信息.
     * 
     * @param buildingId
     *            楼宇ID.
     * @return retObj.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public NavigationBuildDetailEntity getWebBuildById(String buildingId) throws Exception {
        return toolkit.execute(FetchWebBuildByIdBusiness.class, ctx->((BuildingIdContext<NavigationBuildDetailEntity>)ctx).setBuildingId(buildingId));
    }

    /**
     * 取得指定楼层ID的楼宇信息.
     *
     * @param floorId
     *            楼层ID.
     * @return retObj.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @Override
    public NavigationFloorDetailEntity getWebFloorById(String floorId) throws Exception {
        return toolkit.execute(FetchWebFloorByIdBusiness.class, ctx->((FloorIdContext<NavigationFloorDetailEntity>)ctx).setFloorId(floorId));

    }

    /**
     * Web端取得各院区楼列表.
     *
     * @param districtCode
     *            院区ID.
     * @param buildingName
     *            楼宇名称.
     * @param pageNo
     *            当前页码.
     * @param pageSize
     *            每页数量.
     * @return retObj.
     * @throws Exception 异常.
     */
    @Override
    public NavigationBuildEntity getWebBuildInfo(String districtCode, String buildingName, String pageNo,
            String pageSize) throws Exception {
        return toolkit.execute(FetchWebBuildInfoBusiness.class, ctx->{
            ((DistrictCodeContext<?>) ctx).setDistrictCode(districtCode);
            ((BuildingNameContext<?>) ctx).setBuildingName(buildingName);
            ((PageNoContext<?>) ctx).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>) ctx).setPageSize(Integer.parseInt(pageSize));
        });
    }

    /**
     * Web端取得楼层科室列表.
     *
     * @param floorParentId
     *            楼层所在楼宇ID.
     * @param deptName
     *            科室名称.
     * @param pageNo
     *            当前页码.
     * @param pageSize
     *            每页数量.
     * @return retObj.
     * @throws Exception 异常.
     */
    @Override
    public NavigationFloorEntity getWebFloorInfo(String floorParentId, String deptName, String pageNo, String pageSize)
            throws Exception {
        return toolkit.execute(FetchWebFloorInfoBusiness.class, ctx->{
            ((FloorParentIdContext<?>) ctx).setFloorParentId(floorParentId);
            ((DeptNameContext<?>) ctx).setDeptName(deptName);
            ((PageNoContext<?>) ctx).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>) ctx).setPageSize(Integer.parseInt(pageSize));
        });
    }
}
