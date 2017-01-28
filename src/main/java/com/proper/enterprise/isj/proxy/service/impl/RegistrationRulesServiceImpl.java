package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 挂号规则ServiceImpl.
 */
@Service
public class RegistrationRulesServiceImpl implements RegistrationRulesService {

    @Autowired
    RuleRepository ruleRepo;

    @Override
    public void saveRuleInfo(RuleEntity ruleInfo) throws Exception {
        ruleRepo.save(ruleInfo);
    }

    @Override
    public void deleteRuleInfo(List<String> idList) throws Exception {
        List<RuleEntity> baseInfoList = ruleRepo.findAll(idList);
        ruleRepo.delete(baseInfoList);
    }

    @Override
    public RuleEntity getRuleInfoById(String id) throws Exception {
        return ruleRepo.findById(id);
    }

    /**
     * 获取挂号规则信息
     *
     * @param catalogue
     *        挂号规则分类
     * @param name
     *        挂号规则名称
     * @param rule
     *        挂号规则内容
     * @param pageNo
     *        当前页码
     * @param pageSize
     *        每页数量
     * @return retObj
     *         挂号规则信息
     * @throws Exception 异常.
     */
    @Override
    public RegistrationRulesEntity getRulesInfo(String catalogue, String name, String rule, String pageNo, String pageSize)
            throws Exception {

        RegistrationRulesEntity retObj = new RegistrationRulesEntity();

        PageRequest pageReq = buildPageRequest(Integer.parseInt(pageNo), Integer.parseInt(pageSize));

        if(StringUtil.isEmpty(catalogue)) {
            catalogue = "%%";
        } else {
            catalogue = "%" + catalogue + "%";
        }

        if(StringUtil.isEmpty(name)) {
            name = "%%";
        } else {
            name = "%" + name + "%";
        }

        if(StringUtil.isEmpty(rule)) {
            rule = "%%";
        } else {
            rule = "%" + rule + "%";
        }

        int count = ruleRepo.findByCatalogueLikeAndNameLikeAndRuleLike(catalogue, name, rule).size();

        Page<RuleEntity> pageInfo = ruleRepo.findByCatalogueLikeAndNameLikeAndRuleLike(catalogue, name, rule, pageReq);

        List<RuleEntity> rulesList = pageInfo.getContent();

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(rulesList);

        return retObj;
    }

    /**
     * 创建分页请求.
     */
    private PageRequest buildPageRequest(int pageNo, int pageSize) {
        return new PageRequest(pageNo - 1, pageSize,
                new Sort(Sort.Direction.ASC, "catalogue"));
    }
}
