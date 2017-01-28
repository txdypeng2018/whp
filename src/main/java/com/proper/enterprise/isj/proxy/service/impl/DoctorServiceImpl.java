package com.proper.enterprise.isj.proxy.service.impl;

import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.webservices.WebServicesClient;

/**
 * 医生信息服务实现.
 * Created by think on 2016/8/19 0019.
 */
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    @Qualifier("oa2HisDataSource")
    DataSource oa2HisDataSource;


    @Override
    public Map<String, Map<String, Object>> getOA2HISDoctorInfo(Set<String> doctorIdSet) {
        JdbcTemplate jdbcTemplateObject = new JdbcTemplate(oa2HisDataSource);
        StringBuilder querySql = new StringBuilder();
        querySql.append(" where 1 = 1 and ( oh_zgh  in (");
        int i = 0;
        for (String id : doctorIdSet) {
            if (i / 100 > 0 && i % 100 == 0) {
                querySql.append(") or oh_zgh  in (");
            }
            querySql.append("'1000").append(id).append("'");
            if ((i + 1) % 100 != 0 && i != doctorIdSet.size() - 1) {
                querySql.append(",");
            }
            i++;
        }
        querySql.append("))");
        /*-------------查询emr信息(主)----------*/
        String sql = "select emr.zgh ZGH,emr.k0 KS,emr.jj JJ,emr.tc TC,h.oh_zp ZP,zc ZC " + " from oa2emr_doc emr left join OA2HIS h on h.oh_zgh = emr.zgh " + querySql;
        List<Map<String, Object>> ermList = jdbcTemplateObject.queryForList(sql);
        /*-------------查询his信息(次)----------*/
        String sql2 = "select h.oh_zgh  ZGH, h.oh_ks3  KS, emr.jj JJ, emr.tc TC, h.oh_zp  ZP, h.oh_zc ZC " + " from OA2HIS h left join oa2emr_doc emr on h.oh_zgh = emr.zgh  " + querySql;
        List<Map<String, Object>> hisList = jdbcTemplateObject.queryForList(sql2);

        List<Map<String, Object>> resList = new ArrayList<>();
        resList.addAll(ermList);
        resList.addAll(hisList);
        return this.convertInfo2HisInfo(resList);
    }

    private Map<String, Map<String, Object>> convertInfo2HisInfo(List<Map<String, Object>> resList) {
        Map<String, Map<String, Object>> resMap = new HashMap<>();
        Map<String, Object> tempMap;
        String hisJobNumber;
        for (Map<String, Object> map : resList) {
            hisJobNumber = ((String) map.get("ZGH")).substring(4);
            tempMap = resMap.get(hisJobNumber);
            if (tempMap == null) {
                resMap.put(hisJobNumber, map);
            }
        }
        return resMap;
    }

}
