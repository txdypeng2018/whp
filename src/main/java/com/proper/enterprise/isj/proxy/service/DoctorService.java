package com.proper.enterprise.isj.proxy.service;

import java.util.Map;
import java.util.Set;

/**
 * Created by think on 2016/8/19 0019. 医生相关接口
 */
public interface DoctorService {

    // List<DoctorDocument> getDoctorListFromHis(String deptId,
    // Collection<String> ids) throws Exception;
    //
    // DoctorDocument getDoctorFromHis(String doctorId) throws Exception;

    Map<String, Map<String, Object>> getOA2HISDoctorInfo(Set<String> doctorIdSet);
}
