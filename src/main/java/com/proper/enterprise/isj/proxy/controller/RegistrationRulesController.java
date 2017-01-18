package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

/**
 * 挂号规则Controller.
 */
@RestController
@RequestMapping(path = "/registration")
public class RegistrationRulesController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptTipsController.class);

    @Autowired
    RegistrationRulesService rulesService;

    /**
     * 取得挂号规则列表.
     *
     * @param catalogue
     *        挂号规则分类.
     * @param name
     *        挂号规则名称.
     * @param rule
     *        挂号规则内容.
     * @param pageNo
     *        当前页码.
     * @param pageSize
     *        每页数量.
     * @return 意见列表.
     * @throws Exception
     *         异常.
     */
    @GetMapping(path = "/rules")
    public ResponseEntity<RegistrationRulesEntity> getrulesInfo(@RequestParam(required = false) String catalogue,
            @RequestParam(required = false) String name, @RequestParam(required = false) String rule,
            @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {

        // 取得挂号规则列表
        RegistrationRulesEntity rulesInfo = rulesService.getRulesInfo(catalogue, name, rule, pageNo, pageSize);
        return responseOfGet(rulesInfo);
    }

    /**
     * 新增挂号规则信息.
     *
     * @param ruleInfo
     *        挂号规则对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @PostMapping(path = "/rules", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveRuleInfo(@RequestBody RuleEntity ruleInfo) throws Exception {
        try {
            rulesService.saveRuleInfo(ruleInfo);
            return responseOfPost("");
        } catch (Exception e) {
            LOGGER.debug("RegistrationRulesController.saveRuleInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
    }

    /**
     * 更新挂号规则信息.
     *
     * @param ruleInfo
     *        挂号规则对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @PutMapping(path = "/rules", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateRuleInfo(@RequestBody RuleEntity ruleInfo) throws Exception {
        if(StringUtil.isNotNull(ruleInfo.getId())) {
            try {
                rulesService.saveRuleInfo(ruleInfo);
            } catch (Exception e) {
                LOGGER.debug("RegistrationRulesController.updateRuleInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR, e);
            }
        }
        return responseOfPut("");
    }

    /**
     * 删除挂号规则信息.
     *
     * @param ids
     *        id列表.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @DeleteMapping(path = "/rules")
    public ResponseEntity deleteRuleInfo(@RequestParam String ids) throws Exception {
        boolean retValue = false;
        if(StringUtil.isNotNull(ids)) {
            try {
                String[] idArr = ids.split(",");
                List<String> idList = new ArrayList<>();
                Collections.addAll(idList, idArr);
                rulesService.deleteRuleInfo(idList);
                retValue = true;
            } catch (Exception e) {
                LOGGER.debug("RegistrationRulesController.deleteRuleInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR, e);
            }
        }
        return responseOfDelete(retValue);
    }

    /**
     * 取得指定挂号规则信息.
     *
     * @param id .
     * @return 返回给调用方的应答.
     */
    @GetMapping(path = "/rules/{id}")
    public ResponseEntity<RuleEntity> getRuleInfo(@PathVariable String id) throws Exception {
        return responseOfGet(StringUtil.isNotEmpty(id)?rulesService.getRuleInfoById(id):new RuleEntity());
    }
}
