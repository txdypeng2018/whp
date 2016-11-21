package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 挂号规则Controller
 */
@RestController
@RequestMapping(path = "/registration")
public class RegistrationRulesController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptTipsController.class);

    @Autowired
    RegistrationRulesService rulesService;

    /**
     * 取得挂号规则列表
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
     * @return opinionList
     *         意见列表
     * @throws Exception
     *         异常
     */
    @GetMapping(path = "/rules")
    public ResponseEntity<RegistrationRulesEntity> getrulesInfo(@RequestParam(required = false) String catalogue,
            @RequestParam(required = false) String name, @RequestParam(required = false) String rule,
            @RequestParam(required = true) String pageNo, @RequestParam(required = true) String pageSize) throws Exception {

        // 取得挂号规则列表
        RegistrationRulesEntity rulesInfo = rulesService.getRulesInfo(catalogue, name, rule, pageNo, pageSize);
        return responseOfGet(rulesInfo);
    }

    /**
     * 新增挂号规则信息
     *
     * @param ruleInfo
     *        挂号规则对象
     * @return retValue
     * @throws Exception
     */
    @PostMapping(path = "/rules", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveRuleInfo(@RequestBody RuleEntity ruleInfo) throws Exception {
        String retValue = "";
        try {
            rulesService.saveRuleInfo(ruleInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfPost(retValue);
    }

    /**
     * 更新挂号规则信息
     *
     * @param ruleInfo
     *        挂号规则对象
     * @return retValue
     * @throws Exception
     */
    @PutMapping(path = "/rules", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateRuleInfo(@RequestBody RuleEntity ruleInfo) throws Exception {
        String retValue = "";
        if(StringUtil.isNotNull(ruleInfo.getId())) {
            try {
                rulesService.saveRuleInfo(ruleInfo);
            } catch (Exception e) {
                e.printStackTrace();
                return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return responseOfPut(retValue);
    }

    /**
     * 删除挂号规则信息
     *
     * @param ids
     *        id列表
     * @return retValue
     * @throws Exception
     */
    @DeleteMapping(path = "/rules")
    public ResponseEntity<String> deleteRuleInfo(@RequestParam(required = true) String ids) throws Exception {
        boolean retValue = false;
        if(StringUtil.isNotNull(ids)) {
            try {
                String[] idArr = ids.split(",");
                List<String> idList = new ArrayList<>();
                for(String id : idArr) {
                    idList.add(id);
                }
                rulesService.deleteRuleInfo(idList);
                retValue = true;
            } catch (Exception e) {
                e.printStackTrace();
                return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return responseOfDelete(retValue);
    }

    /**
     * 取得指定挂号规则信息
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/rules/{id}")
    public ResponseEntity<RuleEntity> getRuleInfo(@PathVariable String id) throws Exception {
        RuleEntity ruleInfo = new RuleEntity();
        if(StringUtil.isNotEmpty(id)) {
            ruleInfo = rulesService.getRuleInfoById(id);
        }
        return responseOfGet(ruleInfo);
    }
}
