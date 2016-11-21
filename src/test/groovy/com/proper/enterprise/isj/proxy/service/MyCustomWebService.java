package com.proper.enterprise.isj.proxy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.res.DeptInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.deptinfo.Dept;
import com.proper.enterprise.platform.core.utils.JSONUtil;

/**
 * Created by think on 2016/8/31 0031.
 */
//@Service
//@Primary
public class MyCustomWebService extends WebServicesClient {

    public ResModel<DeptInfo> getDeptInfoByParentID(String hosId, String parentId) throws Exception {
        List<Dept> tempDeptList = new ArrayList<>();
        List<Dept> dtList = this.getDeptList();
        for (Dept dept : dtList) {
            if (dept.getParentId().equals(parentId)) {
                tempDeptList.add(dept);
            }
        }
        ResModel<DeptInfo> deptModel = new ResModel<>();
        DeptInfo info = new DeptInfo();
        deptModel.setRes(info);
        deptModel.getRes().setDeptList(tempDeptList);
        return deptModel;
    }

    private List<Dept> getDeptList() {
        List<Dept> list = new ArrayList<>();
        Dept dept = new Dept();
        dept.setDeptId("1");
        dept.setDeptName("南湖院区");
        dept.setParentId("0");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("2");
        dept.setDeptName("滑翔院区");
        dept.setParentId("0");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("3");
        dept.setDeptName("沈北院区");
        dept.setParentId("0");
        list.add(dept);

        dept = new Dept();
        dept.setDeptId("11");
        dept.setDeptName("内科");
        dept.setParentId("1");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("12");
        dept.setDeptName("外科");
        dept.setParentId("1");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("13");
        dept.setDeptName("口腔科");
        dept.setParentId("1");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("21");
        dept.setDeptName("内科");
        dept.setParentId("2");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("22");
        dept.setDeptName("外科(滑翔)");
        dept.setParentId("2");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("23");
        dept.setDeptName("口腔");
        dept.setParentId("2");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("31");
        dept.setDeptName("急诊");
        dept.setParentId("3");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("32");
        dept.setDeptName("血液科");
        dept.setParentId("3");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("111");
        dept.setDeptName("内科一");
        dept.setParentId("11");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("112");
        dept.setDeptName("内科二");
        dept.setParentId("11");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("113");
        dept.setDeptName("内科三");
        dept.setParentId("11");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("211");
        dept.setDeptName("内科四");
        dept.setParentId("21");
        list.add(dept);
        dept = new Dept();
        dept.setDeptId("212");
        dept.setDeptName("内科二");
        dept.setParentId("21");
        list.add(dept);
        try {
            System.out.println(JSONUtil.toJSON(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
