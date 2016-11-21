package com.proper.enterprise.isj.proxy.controller
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@Sql
public class HospitalNavigationControllerTest extends AbstractTest {

    @Test
    public void testGetBuilds() throws Exception {
        String obj = (String) getAndReturn("/hospitalNavigation/builds", "",
                HttpStatus.OK);
        List<Map<String, Object>> docList = JSONUtil.parse(obj, List.class);
        assert docList.size() == 3
        assert docList.get(0).get("builds").size == 4
        assert docList.get(1).get("builds").size == 6
        assert docList.get(2).get("builds").size == 1
    }

    @Test
    public void testGetFloors() throws Exception {
        String result = get("/hospitalNavigation/builds/floors?buildId=120701", HttpStatus.OK).getResponse().getContentAsString();
        def res = JSONUtil.parse(result, List.class)
        assert res.size() == 23
    }

    @Test
    public void testGetBuildList() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/hospitalNavigation/build?pageNo=1&pageSize=20&districtCode=1207&buildingName=1", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("buildingCode");
        assert count == 2
        assert value == "120701"
    }

    @Test
    public void testGetFloorList() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/hospitalNavigation/floor?pageNo=1&pageSize=20&floorParentId=120701&deptName=", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("floorName");
        assert count == 23
        assert retList.size() == 20
        assert value == "B1F"
    }

    @Test
    public void testGetBuildDetail() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/hospitalNavigation/build/120702", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        String buildingName = String.valueOf(doc.get("buildingName"));
        assert buildingName == "1号楼B座"
    }

    @Test
    public void testGetFloorDetail() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/hospitalNavigation/floor/12070108", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        String floorParentId = String.valueOf(doc.get("floorParentId"));
        String fid = String.valueOf(doc.get("fid"));
        String floorId = String.valueOf(doc.get("floorId"));
        String floorName = String.valueOf(doc.get("floorName"));
        String did = String.valueOf(doc.get("did"));
        String deptId = String.valueOf(doc.get("deptId"));
        String deptName = String.valueOf(doc.get("deptName"));
        assert floorParentId == "120701"
        assert fid == "eeb0e16e-95d9-11e6-83da-fbb35e195804"
        assert floorId == "12070108"
        assert floorName == "07F"
        assert did == "eeb156b5-95d9-11e6-83da-fbb35e195804"
        assert deptId == "1207010108"
        assert deptName == "第一手术室"
    }

    @Test
    public void testDelBuild() {
        mockUser('id', 'name', 'pwd', true);
        delete("/hospitalNavigation/build?ids=eeae494d-95d9-11e6-83da-fbb35e195804", HttpStatus.NO_CONTENT);
        String retObj = (String) getAndReturn("/hospitalNavigation/build?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        String retObj1 = (String) getAndReturn("/hospitalNavigation/build?pageNo=1&pageSize=20&districtCode=1207&buildingName=1号楼B座", "", HttpStatus.OK);
        Map<String, Object> doc1 = JSONUtil.parse(retObj1, Object.class);
        int count1 = Integer.parseInt(String.valueOf(doc1.get("count")));
        assert count == 10
        assert count1 == 0
    }

    @Test
    public void testDelFloor() {
        mockUser('id', 'name', 'pwd', true);
        delete("/hospitalNavigation/floor?ids=eeb0e16e-95d9-11e6-83da-fbb35e195804,eeb156b5-95d9-11e6-83da-fbb35e195804", HttpStatus.NO_CONTENT);
        String retObj = (String) getAndReturn("/hospitalNavigation/floor?pageNo=1&pageSize=20&floorParentId=120701&deptName=", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        String retObj1 = (String) getAndReturn("/hospitalNavigation/floor?pageNo=1&pageSize=20&floorParentId=120701&deptName=第四普通外科^结直肠肿瘤外科病房^第八", "", HttpStatus.OK);
        Map<String, Object> doc1 = JSONUtil.parse(retObj1, Object.class);
        int count1 = Integer.parseInt(String.valueOf(doc1.get("count")));
        assert count == 22
        assert count1 == 0
    }

    @Test
    public void testAddBuild() {
        mockUser('id', 'name', 'pwd', true);
        post("/hospitalNavigation/build", '{"districtCode": "1207", "buildingName": "测试", "createUserId": "testUserId"}', HttpStatus.CREATED);
        String retObj = (String) getAndReturn("/hospitalNavigation/build?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 12
        String retObj2 = (String) getAndReturn("/hospitalNavigation/build?pageNo=1&pageSize=2&districtCode=1207&buildingName=测试", "", HttpStatus.OK);
        Map<String, Object> doc2 = JSONUtil.parse(retObj2, Object.class);
        int count2 = Integer.parseInt(String.valueOf(doc2.get("count")));
        assert count2 == 1
        List<Map<String, String>> retList2 = (List<Map<String, Object>>) doc2.get("data");
        String value3 = retList2.get(0).get("buildingName");
        String value4 = retList2.get(0).get("buildingCode");
        assert value3 == "测试"
        assert value4 == "120705"
    }

    @Test
    public void testAddFloor() {
        mockUser('id', 'name', 'pwd', true);
        post("/hospitalNavigation/floor", '{"floorParentId": "120702", "floorName": "25F", "deptName": "计算机中心", "createUserId": "testUserId"}', HttpStatus.CREATED);
        String retObj = (String) getAndReturn("/hospitalNavigation/floor?pageNo=1&pageSize=20&floorParentId=120702&deptName=", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 25
        String retObj2 = (String) getAndReturn("/hospitalNavigation/floor?pageNo=1&pageSize=2&floorParentId=120702&deptName=计算机中心", "", HttpStatus.OK);
        Map<String, Object> doc2 = JSONUtil.parse(retObj2, Object.class);
        int count2 = Integer.parseInt(String.valueOf(doc2.get("count")));
        assert count2 == 1
        List<Map<String, String>> retList2 = (List<Map<String, Object>>) doc2.get("data");
        String value3 = retList2.get(0).get("deptName");
        String value4 = retList2.get(0).get("floorId");
        String value5 = retList2.get(0).get("deptId");
        assert value3 == "计算机中心"
        assert value4 == "12070226"
        assert value5 == "1207020126"
    }

    @Test
    public void testUpdateBuild() {
        mockUser('id', 'name', 'pwd', true);
        put("/hospitalNavigation/build", '{"districtCode":"1207", "buildingCode": "120703", "buildingName": "测试楼"}', HttpStatus.OK);
        String retObj = (String) getAndReturn("/hospitalNavigation/build?pageNo=1&pageSize=20&buildingName=测试", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("buildingName");
        assert count == 1
        assert value == "测试楼"
    }

    @Test
    public void testUpdateFloor() {
        mockUser('id', 'name', 'pwd', true);
        put("/hospitalNavigation/floor", '{"floorId":"12070213", "floorName": "测试楼层", "deptId": "1207020113", "deptName": "测试科室"}', HttpStatus.OK);
        String retObj = (String) getAndReturn("/hospitalNavigation/floor?pageNo=1&pageSize=20&floorParentId=120702&deptName=科室", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value2 = retList.get(0).get("deptName");
        assert count == 1
        assert value2 == "测试科室"
    }
}