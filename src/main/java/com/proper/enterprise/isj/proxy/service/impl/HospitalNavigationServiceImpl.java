package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.entity.*;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 医院导航ServiceImpl
 */
@Service
public class HospitalNavigationServiceImpl implements HospitalNavigationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalNavigationServiceImpl.class);

    @Autowired
    NavInfoRepository navRepo;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    /**
     * 取得手机端院区楼宇列表
     *
     * @param districtId
     *        院区ID
     * @return retList
     */
    @Override
    public List<Map<String, Object>> getAppDistrictsList(String districtId) throws Exception {
        List<Map<String, Object>> retList = new ArrayList<>();

        List<NavInfoEntity> ditrictList;
        if(StringUtil.isNotNull(districtId)) {
            ditrictList = navRepo.findByNavIdOrderByNavIdAsc(districtId);
        } else {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "navId"));
            ditrictList = navRepo.findAll(sort);
        }
        List<SubjectDocument> disList = getDisList();
        for(SubjectDocument subject : disList) {
            NavInfoEntity navInfo = new NavInfoEntity();
            navInfo.setParentType("HISDISTRICT");
            navInfo.setNavId(subject.getId());
            navInfo.setNavName(subject.getName());
            ditrictList.add(navInfo);
        }
        if(ditrictList.size() > 0) {
            for (NavInfoEntity detail : ditrictList) {
                Map<String, Object> districtObj = new LinkedHashMap<>();
                // 院区级别
                if (detail.getParentType().equals("HISDISTRICT")) {
                    districtObj.put("id", detail.getNavId());
                    districtObj.put("name", detail.getNavName());
                    List<Map<String, String>> detailList = new ArrayList<>();
                    for (NavInfoEntity buildDetail : ditrictList) {
                        // 当前院区并且是建筑级别
                        if (buildDetail.getParentType().equals(ConfCenter.get("isj.info.district"))
                                && buildDetail.getParentId().equals(detail.getNavId())) {
                            Map<String, String> buildObj = new LinkedHashMap<>();
                            buildObj.put("id", buildDetail.getNavId());
                            buildObj.put("name", buildDetail.getNavName());
                            buildObj.put("function", buildDetail.getNavFunction());
                            detailList.add(buildObj);
                        }
                    }
                    districtObj.put("builds", detailList);
                }
                if(!districtObj.isEmpty()) {
                    retList.add(districtObj);
                }
            }
        }
        return retList;
    }

    /**
     * 取得手机端指定楼宇各楼层科室信息
     *
     * @param buildId
     *        楼宇ID
     * @return retList
     *         手机端指定楼宇各楼层科室信息
     */
    @Override
    public List<Map<String, Object>> getAppFloorList(String buildId) throws Exception {
        List<Map<String, Object>> retList = new ArrayList<>();
        String buildType = ConfCenter.get("isj.info.build");
        String floorType = ConfCenter.get("isj.info.floor");

        List<NavInfoEntity> floorList = new ArrayList<>();
        if(StringUtil.isNotNull(buildId)) {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "navId"));
            floorList = navRepo.findAll(sort);
        }
        System.out.println("floorList.size:" + floorList.size());

        if(floorList.size() > 0) {
            for (NavInfoEntity detail : floorList) {
                Map<String, Object> districtObj = new LinkedHashMap<>();
                // 指定ID的楼宇
                if (detail.getParentType().equals(buildType)
                        && detail.getParentId().equals(buildId)) {
                    districtObj.put("id", detail.getNavId());
                    districtObj.put("name", detail.getNavName());
                    List<String> detailList = new ArrayList<>();
                    for (NavInfoEntity roomDetail : floorList) {
                        // 获取科室信息
                        if (roomDetail.getParentType().equals(floorType)
                                && roomDetail.getParentId().equals(detail.getNavId())) {
                            if(StringUtil.isNotNull(roomDetail.getNavName())) {
                                String[] deptsArr = roomDetail.getNavName().split("\\^");
                                for(String dept : deptsArr) {
                                    detailList.add(dept);
                                }
                            }
                        }
                    }
                    if(!detailList.isEmpty()) {
                        districtObj.put("depts", detailList);
                    }
                }
                if(!districtObj.isEmpty()) {
                    retList.add(districtObj);
                }
            }
        }
        return retList;
    }

    /**
     * 新增Web端楼宇信息
     *
     * @param buildInfo
     *        楼宇信息
     * @throws Exception
     */
    @Override
    public void saveWebBuildInfo(NavigationBuildDetailEntity buildInfo) throws Exception {
        NavInfoEntity navObj = new NavInfoEntity();
        String disId = buildInfo.getDistrictCode();

        NavInfoEntity topObj = navRepo.findTopByParentIdOrderByNavIdDesc(disId);
        // 院区有楼宇信息
        if(topObj != null) {
            String navTopId = topObj.getNavId();
            int navId = Integer.parseInt(topObj.getNavId().substring(navTopId.length() -2, navTopId.length())) + 1;
            navObj.setNavId(disId + String.format("%02d", navId));
            // 院区没有楼宇信息
        } else {
            navObj.setNavId(disId + "01");
        }
        navObj.setNavName(buildInfo.getBuildingName());
        navObj.setParentId(disId);
        navObj.setParentType(ConfCenter.get("isj.info.district"));
        navRepo.save(navObj);
    }

    /**
     * 新增Web端楼层科室信息
     *
     * @param floorInfo
     *        楼层科室信息
     * @throws Exception
     */
    @Override
    public void saveWebFloorInfo(NavigationFloorDetailEntity floorInfo) throws Exception {
        List<NavInfoEntity> saveObjList = new ArrayList<>();
        NavInfoEntity navFloorObj = new NavInfoEntity();
        NavInfoEntity navDeptObj = new NavInfoEntity();
        String buildId = floorInfo.getFloorParentId();

        NavInfoEntity topObj = navRepo.findTopByParentIdOrderByNavIdDesc(buildId);
        if(topObj != null) {
            // 楼层信息
            String navFloorTopId = topObj.getNavId();
            StringBuilder oriFloorNavId = new StringBuilder();
            StringBuilder oriDeptNavId = new StringBuilder();
            oriFloorNavId.append(buildId);
            oriDeptNavId.append(buildId);
            int navId = Integer.parseInt(navFloorTopId.substring(navFloorTopId.length() - 2, navFloorTopId.length())) + 1;
            String strNavFloorId = String.format("%02d", navId);
            // 楼层id
            oriFloorNavId.append(strNavFloorId);
            navFloorObj.setNavId(oriFloorNavId.toString());
            // 科室id
            oriDeptNavId.append("01");
            oriDeptNavId.append(strNavFloorId);
            navDeptObj.setNavId(oriDeptNavId.toString());

            // 院区没有楼宇信息
        } else {
            navFloorObj.setNavId(buildId + "01");
            navDeptObj.setNavId(buildId + "0101");
        }
        // 楼层信息
        navFloorObj.setNavName(floorInfo.getFloorName());
        navFloorObj.setParentId(buildId);
        navFloorObj.setParentType(ConfCenter.get("isj.info.build"));
        // 科室信息
        navDeptObj.setNavName(floorInfo.getDeptName());
        navDeptObj.setParentId(navFloorObj.getNavId());
        navDeptObj.setParentType(ConfCenter.get("isj.info.floor"));

        saveObjList.add(navFloorObj);
        saveObjList.add(navDeptObj);
        navRepo.save(saveObjList);
    }

    /**
     * 更新Web端楼宇信息
     *
     * @param buildInfo
     *        楼宇信息
     * @throws Exception
     */
    @Override
    public void updateWebBuildInfo(NavigationBuildDetailEntity buildInfo) throws Exception {
        NavInfoEntity navObj = navRepo.findByNavId(buildInfo.getBuildingCode());
        if(navObj != null) {
            navObj.setNavName(buildInfo.getBuildingName());
            navObj.setParentId(buildInfo.getDistrictCode());
            navRepo.save(navObj);
        }
    }

    /**
     * 更新Web端楼层科室信息
     *
     * @param floorInfo
     *        楼层科室信息
     * @throws Exception
     */
    @Override
    public void updateWebFloorInfo(NavigationFloorDetailEntity floorInfo) throws Exception {
        List<NavInfoEntity> updateList = new ArrayList<>();
        NavInfoEntity navFloorObj = navRepo.findByNavId(floorInfo.getFloorId());
        NavInfoEntity navDeptObj = navRepo.findByNavId(floorInfo.getDeptId());
        if(navFloorObj != null && navDeptObj != null) {
            navFloorObj.setNavName(floorInfo.getFloorName());
            navDeptObj.setNavName(floorInfo.getDeptName());
            updateList.add(navFloorObj);
            updateList.add(navDeptObj);
            navRepo.save(updateList);
        }
    }

    /**
     * 删除Web端楼宇信息
     *
     * @param idList
     *        待删除ID列表
     * @throws Exception
     */
    @Override
    public void deleteWebNavInfo(List<String> idList) throws Exception {
        List<NavInfoEntity> navList = navRepo.findAll(idList);
        navRepo.delete(navList);
    }

    /**
     * 取得指定楼宇ID的楼宇信息
     *
     * @param buildingId
     *        楼宇ID
     * @return retObj
     * @throws Exception
     */
    @Override
    public NavigationBuildDetailEntity getWebBuildById(String buildingId) throws Exception {
        NavigationBuildDetailEntity retObj = new NavigationBuildDetailEntity();
        NavInfoEntity obj = navRepo.findByNavId(buildingId);
        if(obj != null) {
            retObj.setDistrictCode(obj.getParentId());
            retObj.setBuildingCode(obj.getNavId());
            retObj.setBuildingName(obj.getNavName());
        }
        return retObj;
    }

    /**
     * 取得指定楼层ID的楼宇信息
     *
     * @param floorId
     *        楼层ID
     * @return retObj
     * @throws Exception
     */
    @Override
    public NavigationFloorDetailEntity getWebFloorById(String floorId) throws Exception {
        NavigationFloorDetailEntity retObj = new NavigationFloorDetailEntity();
        NavInfoEntity obj = navRepo.findByNavId(floorId);
        if(obj != null) {
            // 楼层所在楼宇ID
            retObj.setFloorParentId(obj.getParentId());
            // 楼层记录ID
            retObj.setFid(obj.getId());
            // 楼层ID
            retObj.setFloorId(obj.getNavId());
            // 楼层名称
            retObj.setFloorName(obj.getNavName());
            NavInfoEntity deptObj = navRepo.findByParentId(floorId).get(0);
            if(deptObj != null) {
                // 科室记录ID
                retObj.setDid(deptObj.getId());
                // 楼层科室信息ID
                retObj.setDeptId(deptObj.getNavId());
                // 楼层科室信息
                retObj.setDeptName(deptObj.getNavName());
            }
        }
        return retObj;
    }

    /**
     * Web端取得各院区楼列表
     *
     * @param districtCode
     *        院区ID
     * @param buildingName
     *        楼宇名称
     * @param pageNo
     *        当前页码
     * @param pageSize
     *        每页数量
     * @return retObj
     * @throws Exception
     */
    @Override
    public NavigationBuildEntity getWebBuildInfo(String districtCode, String buildingName,
                                                 String pageNo, String pageSize) throws Exception {
        NavigationBuildEntity retObj = new NavigationBuildEntity();
        List<NavigationBuildDetailEntity> dataList = new ArrayList<>();
        PageRequest pageReq = buildPageRequest(Integer.parseInt(pageNo), Integer.parseInt(pageSize));

        if(StringUtil.isEmpty(districtCode)) {
            districtCode = "%%";
        } else {
            districtCode = "%" + districtCode + "%";
        }

        if(StringUtil.isEmpty(buildingName)) {
            buildingName = "%%";
        } else {
            buildingName = "%" + buildingName + "%";
        }
        String parentType = ConfCenter.get("isj.info.district");
        int count = navRepo.findByParentTypeAndParentIdLikeAndNavNameLike(parentType, districtCode, buildingName).size();
        Page<NavInfoEntity> pageInfo = navRepo.findByParentTypeAndParentIdLikeAndNavNameLike(parentType, districtCode, buildingName, pageReq);
        List<NavInfoEntity> navList = pageInfo.getContent();

        for(NavInfoEntity detail : navList) {
            NavigationBuildDetailEntity obj = new NavigationBuildDetailEntity();
            // id
            obj.setId(detail.getId());
            // 父节点id
            obj.setDistrictCode(detail.getParentId());
            // 导航id
            obj.setBuildingCode(detail.getNavId());
            // 导航名称
            obj.setBuildingName(detail.getNavName());
            dataList.add(obj);
        }

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(dataList);

        return retObj;
    }

    /**
     * Web端取得楼层科室列表
     *
     * @param floorParentId
     *        楼层所在楼宇ID
     * @param deptName
     *        科室名称
     * @param pageNo
     *        当前页码
     * @param pageSize
     *        每页数量
     * @return retObj
     * @throws Exception
     */
    @Override
    public NavigationFloorEntity getWebFloorInfo(String floorParentId, String deptName,
                                                 String pageNo, String pageSize) throws Exception {
        NavigationFloorEntity retObj = new NavigationFloorEntity();
        List<NavigationFloorDetailEntity> dataList = new ArrayList<>();
        PageRequest pageReq = buildPageRequest(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        List<NavInfoEntity> countList;
        Page<NavInfoEntity> pageInfo;
        String parentType = "";
        String buildType = ConfCenter.get("isj.info.build");
        String floorType = ConfCenter.get("isj.info.floor");
        boolean flg = false;
        if(StringUtil.isEmpty(deptName)) {
            deptName = "%%";
            parentType = buildType;
        } else {
            deptName = "%" + deptName + "%";
            flg = true;
            parentType = floorType;
        }
        floorParentId = "%" + floorParentId + "%";
        int count = navRepo.findByParentIdLikeAndParentTypeAndNavNameLike(floorParentId, parentType, deptName).size();
        pageInfo = navRepo.findByParentIdLikeAndParentTypeAndNavNameLike(floorParentId, parentType, deptName, pageReq);

        List<NavInfoEntity> navList = pageInfo.getContent();
        List<NavInfoEntity> deptList = navRepo.findByParentType(floorType);
        List<NavInfoEntity> floorList = navRepo.findByParentType(buildType);

        for(NavInfoEntity detail : navList) {
            if(!flg) {
                for(NavInfoEntity deptDetail : deptList) {
                    if(detail.getNavId().equals(deptDetail.getParentId())) {
                        NavigationFloorDetailEntity obj = new NavigationFloorDetailEntity();
                        // 楼层所在楼宇ID
                        obj.setFloorParentId(floorParentId);
                        // 楼层记录ID
                        obj.setFid(detail.getId());
                        // 楼层ID
                        obj.setFloorId(detail.getNavId());
                        // 楼层名称
                        obj.setFloorName(detail.getNavName());
                        // 科室记录ID
                        obj.setDid(deptDetail.getId());
                        // 楼层科室信息ID
                        obj.setDeptId(deptDetail.getNavId());
                        // 楼层科室信息
                        obj.setDeptName(deptDetail.getNavName());
                        dataList.add(obj);
                    }
                }
            } else {
                for(NavInfoEntity floorDetail : floorList) {
                    if(detail.getParentId().equals(floorDetail.getNavId())) {
                        NavigationFloorDetailEntity obj = new NavigationFloorDetailEntity();
                        // 楼层所在楼宇ID
                        obj.setFloorParentId(floorParentId);
                        // 楼层记录ID
                        obj.setFid(floorDetail.getId());
                        // 楼层ID
                        obj.setFloorId(floorDetail.getNavId());
                        // 楼层名称
                        obj.setFloorName(floorDetail.getNavName());
                        // 科室记录ID
                        obj.setDid(detail.getId());
                        // 楼层科室信息ID
                        obj.setDeptId(detail.getNavId());
                        // 楼层科室信息
                        obj.setDeptName(detail.getNavName());
                        dataList.add(obj);
                    }

                }
            }

        }

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(dataList);

        return retObj;
    }

    /**
     * 创建分页请求.
     */
    private PageRequest buildPageRequest(int pageNo, int pageSize) {
        Sort sort = null;
        sort = new Sort(Sort.Direction.ASC, "navId");
        return new PageRequest(pageNo - 1, pageSize, sort);
    }

    /**
     * 通过HIS接口获取院区列表信息
     *
     * @return
     * @throws Exception
     */
    private List<SubjectDocument> getDisList() throws Exception {
        List<SubjectDocument> disList;
        try {
            disList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                    .get(String.valueOf(DeptLevel.CHILD.getCode())).get("0");
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            e.printStackTrace();
            throw new HisLinkException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return disList;
    }
}
