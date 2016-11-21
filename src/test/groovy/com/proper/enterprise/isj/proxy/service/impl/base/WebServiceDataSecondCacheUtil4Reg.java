package com.proper.enterprise.isj.proxy.service.impl.base;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.webservices.model.res.deptinfo.Dept;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by think on 2016/11/1 0001.
 */
@Component
public class WebServiceDataSecondCacheUtil4Reg extends WebServiceDataSecondCacheUtil{



    @Override
    public Map<String, String> cacheSubjectMap() throws Exception {
        List<Dept> deptInfoList = new ArrayList<>();
        Dept dept = new Dept();
        dept.setDeptId("1207");
        dept.setDeptName("南湖院区");
        deptInfoList.add(dept);
        dept = new Dept();
        dept.setDeptId("1221");
        dept.setDeptName("滑翔院区");
        deptInfoList.add(dept);
        dept = new Dept();
        dept.setDeptId("1222");
        dept.setDeptName("沈北院区");
        deptInfoList.add(dept);
        Map<String, String> subMap = new HashMap<>();
        for (Dept dt : deptInfoList) {
            subMap.put(dt.getDeptId(), dt.getDeptName());
        }
        return subMap;
    }

    @Override
    public Map<String, String> getCacheSubjectMap() throws Exception {
        return super.getCacheSubjectMap();
    }

    @Override
    public Map<String, Map<String, List<SubjectDocument>>> cacheSubjectDocument() throws Exception {
        return super.cacheSubjectDocument();
    }

    @Override
    public Map<String, Map<String, List<SubjectDocument>>> getCacheSubjectAndDoctorDocument() throws Exception {
        return super.getCacheSubjectAndDoctorDocument();
    }

    @Override
    public Map<String, Map<String, Object>> cacheOA2HISInfo() throws Exception {
        return super.cacheOA2HISInfo();
    }

    @Override
    public Map<String, Map<String, Object>> getCacheOA2HISInfo() throws Exception {
        return super.getCacheOA2HISInfo();
    }
}
