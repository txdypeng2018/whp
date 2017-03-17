package com.proper.enterprise.isj.proxy.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.CatalogueContext;
import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.context.IdsContext;
import com.proper.enterprise.isj.context.NameContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.RuleContext;
import com.proper.enterprise.isj.context.RuleEntityContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.registration.RegistrationRulesDeleteRuleInfoBusiness;
import com.proper.enterprise.isj.proxy.business.registration.RegistrationRulesGetRuleInfoBusiness;
import com.proper.enterprise.isj.proxy.business.registration.RegistrationRulesGetRulesInfoBusiness;
import com.proper.enterprise.isj.proxy.business.registration.RegistrationRulesSaveRuleInfoBusiness;
import com.proper.enterprise.isj.proxy.business.registration.RegistrationRulesUpdateRuleInfoBusiness;
import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.rule.entity.RuleEntity;

/**
 * 挂号规则Controller.
 */
@RestController
@RequestMapping(path = "/registration")
public class RegistrationRulesController extends IHosBaseController {

    /**
     * 取得挂号规则列表.
     *
     * @param catalogue
     *            挂号规则分类.
     * @param name
     *            挂号规则名称.
     * @param rule
     *            挂号规则内容.
     * @param pageNo
     *            当前页码.
     * @param pageSize
     *            每页数量.
     * @return 意见列表.
     * @throws Exception
     *             异常.
     */
    @GetMapping(path = "/rules")
    public ResponseEntity<RegistrationRulesEntity> getrulesInfo(@RequestParam(required = false) String catalogue,
            @RequestParam(required = false) String name, @RequestParam(required = false) String rule,
            @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {
        return responseOfGet(toolkit.execute(RegistrationRulesGetRulesInfoBusiness.class, (c) -> {
            ((CatalogueContext<?>) c).setCatalogue(catalogue);
            ((NameContext<?>) c).setName(name);
            ((RuleContext<?>) c).setRule(rule);
            ((PageNoContext<?>) c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>) c).setPageSize(Integer.parseInt(pageSize));
        }));

    }

    /**
     * 新增挂号规则信息.
     *
     * @param ruleInfo
     *            挂号规则对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @PostMapping(path = "/rules", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveRuleInfo(@RequestBody RuleEntity ruleInfo) throws Exception {
        return responseOfPost(toolkit.execute(RegistrationRulesSaveRuleInfoBusiness.class, (c) -> {
            ((RuleEntityContext<?>) c).setRuleEntity(ruleInfo);
        }));
    }

    /**
     * 更新挂号规则信息.
     *
     * @param ruleInfo
     *            挂号规则对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @PutMapping(path = "/rules", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateRuleInfo(@RequestBody RuleEntity ruleInfo) throws Exception {
        return responseOfPut(toolkit.execute(RegistrationRulesUpdateRuleInfoBusiness.class, (c) -> {
            ((RuleEntityContext<?>) c).setRuleEntity(ruleInfo);
        }));
    }

    /**
     * 删除挂号规则信息.
     *
     * @param ids
     *            id列表.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @SuppressWarnings("rawtypes")
    @DeleteMapping(path = "/rules")
    public ResponseEntity deleteRuleInfo(@RequestParam String ids) throws Exception {
        return responseOfDelete(toolkit.execute(RegistrationRulesDeleteRuleInfoBusiness.class, (c) -> {
            ((IdsContext<?>) c).setIds(ids);
        }));
    }

    /**
     * 取得指定挂号规则信息.
     *
     * @param id .
     * @return 返回给调用方的应答.
     */
    @GetMapping(path = "/rules/{id}")
    public ResponseEntity<RuleEntity> getRuleInfo(@PathVariable String id) throws Exception {
        return responseOfGet(toolkit.execute(RegistrationRulesGetRuleInfoBusiness.class, (c) -> {
            ((IdContext<?>) c).setId(id);
        }));
    }
}
