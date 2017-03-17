package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.context.DoctorIdSetContext;
import com.proper.enterprise.isj.webservices.WebServicesClient;

@Service
public class HisFetchOA2HISDoctorInfoBusiness<M extends DoctorIdSetContext<Map<String, Map<String, Object>>> & ModifiedResultBusinessContext<Map<String, Map<String, Object>>>>
        implements IBusiness<Map<String, Map<String, Object>>, M> {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    @Qualifier("oa2HisDataSource")
    DataSource oa2HisDataSource;

    @Override
    public void process(M ctx) throws Exception {

        JdbcTemplate jdbcTemplateObject = new JdbcTemplate(oa2HisDataSource);
        StringBuilder querySql = new StringBuilder();
        querySql.append(" where 1 = 1 and ( oh_zgh  in (");
        Set<String> ids = ctx.getDoctorIds();
        int i = 0;
        int len = ids.size();
        int maxLen = len - 1;
        for (String id : ids) {
            if (i / 100 > 0 && i % 100 == 0) {
                querySql.append(") or oh_zgh  in (");
            }
            querySql.append("'1000").append(id).append("'");
            if ((i + 1) % 100 != 0 && i != maxLen) {
                querySql.append(",");
            }
            i++;
        }
        querySql.append("))");
        /*-------------查询emr信息(主)----------*/
        String sql = "select emr.zgh ZGH,emr.k0 KS,emr.jj JJ,emr.tc TC,h.oh_zp ZP,zc ZC "
                + " from oa2emr_doc emr left join OA2HIS h on h.oh_zgh = emr.zgh " + querySql;
        List<Map<String, Object>> ermList = jdbcTemplateObject.queryForList(sql);
        /*-------------查询his信息(次)----------*/
        String sql2 = "select h.oh_zgh  ZGH, h.oh_ks3  KS, emr.jj JJ, emr.tc TC, h.oh_zp  ZP, h.oh_zc ZC "
                + " from OA2HIS h left join oa2emr_doc emr on h.oh_zgh = emr.zgh  " + querySql;
        List<Map<String, Object>> hisList = jdbcTemplateObject.queryForList(sql2);

        List<Map<String, Object>> resList = new ArrayList<>();
        resList.addAll(ermList);
        resList.addAll(hisList);
        ctx.setResult(this.convertInfo2HisInfo(resList));
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