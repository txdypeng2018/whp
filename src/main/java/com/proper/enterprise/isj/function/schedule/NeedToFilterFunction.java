package com.proper.enterprise.isj.function.schedule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.SpELParser;

/**
 * com.proper.enterprise.isj.proxy.service.impl.ScheduleServiceImpl.needToFilter(boolean, RegDoctor)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class NeedToFilterFunction implements IFunction<Boolean>, ILoggable {

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    SpELParser parser;

    @Override
    public Boolean execute(Object... params) throws Exception {
        return needToFilter((boolean) params[0], (RegDoctor) params[1]);
    }

    /**
     * 根据当日挂号规则及参数，判断医生是否需要从当日挂号医生列表中过滤掉.
     *
     * @param isAppointment 查询结束日期.
     * @param doctor 医生信息.
     * @return true：需要过滤；false：不需要过滤.
     */
    public boolean needToFilter(boolean isAppointment, RegDoctor doctor) {
        Collection<RuleEntity> rules = ruleRepository.findByCatalogue("SAME_DAY_FILTER");
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        Map<String, Object> vars = new HashMap<>(3);
        vars.put("isAppointment", isAppointment);
        vars.put("doctor", doctor);
        for (RuleEntity rule : rules) {
            try {
                debug("Parsing {} rule({}) with {}", rule.getName(), rule.getRule(), vars);
                if (!parser.parse(rule.getRule(), vars, Boolean.class)) {
                    return false;
                }
            } catch (ExpressionException ee) {
                debug("Parse {} with {} throw exception!", rule.getRule(), vars, ee);
            }
        }
        return true;
    }

}
