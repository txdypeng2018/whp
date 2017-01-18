package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.service.UserService;
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

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_INFOTYPE_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

/**
 * 温馨提示Controller
 */
@RestController
@RequestMapping(path = "/prompt")
public class PromptTipsController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptTipsController.class);

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    PromptTipsService tipService;

    @Autowired
    BaseInfoRepository baseInfoRepo;

    /**
     * 取得温馨提示列表.
     *
     * @param infoType
     *        温馨提示类型编码,非必填.
     * @param typeName
     *        温馨提示类型名称,非必填.
     * @param info
     *        温馨提示内容,非必填.
     * @param pageNo
     *        当前页码.
     * @param pageSize
     *        每页数量.
     * @return 意见列表.
     * @throws Exception
     *         异常.
     */
    @GetMapping(path = "/tips")
    public ResponseEntity<PromptTipsEntity> getTipsInfo(@RequestParam(required = false) String infoType,
           @RequestParam(required = false) String typeName, @RequestParam(required = false) String info,
           @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {

        // 取得温馨提示列表
        PromptTipsEntity tipsInfo = tipService.getTipsInfo(infoType, typeName, info, pageNo, pageSize);
        return responseOfGet(tipsInfo);
    }

    /**
     * 新增温馨提示信息.
     *
     * @param tipInfo
     *        温馨提示对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @PostMapping(path = "/tips", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveTipInfo(@RequestBody BaseInfoEntity tipInfo) throws Exception {
        String retValue = "";
        try {
            String infoType = tipInfo.getInfoType();
            if(baseInfoRepo.findByInfoType(infoType).size() == 0) {
                tipService.saveTipInfo(tipInfo);
            } else {
                throw new RuntimeException(APP_INFOTYPE_ERR);
            }

        } catch (Exception e) {
            LOGGER.debug("PromptTipsController.saveTipInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR);
        }
        return responseOfPost(retValue);
    }

    /**
     * 更新温馨提示信息.
     *
     * @param tipInfo
     *        温馨提示对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @PutMapping(path = "/tips", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateTipInfo(@RequestBody BaseInfoEntity tipInfo) throws Exception {
        String retValue = "";
        if(StringUtil.isNotNull(tipInfo.getId())) {
            try {
                tipService.saveTipInfo(tipInfo);
            } catch (Exception e) {
                LOGGER.debug("PromptTipsController.updateTipInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR);
            }
        }
        return responseOfPut(retValue);
    }

    /**
     * 删除温馨提示信息.
     *
     * @param ids
     *        id列表.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @DeleteMapping(path = "/tips")
    public ResponseEntity deleteTipInfo(@RequestParam String ids) throws Exception {
        boolean retValue = false;
        if(StringUtil.isNotNull(ids)) {
            String[] idArr = ids.split(",");
            List<String> idList = new ArrayList<>();
            Collections.addAll(idList, idArr);
            try {
                tipService.deleteTipInfo(idList);
                retValue = true;
            } catch (Exception e) {
                LOGGER.debug("PromptTipsController.deleteTipInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR);
            }
        }
        return responseOfDelete(retValue);
    }

    /**
     * 取得指定温馨提示信息.
     *
     * @param id 提示编号.
     * @return 返回给调用方的应答.
     * @exception Exception 异常.
     */
    @GetMapping(path = "/tips/{id}")
    public ResponseEntity<BaseInfoEntity> getTipInfo(@PathVariable String id) throws Exception {
        BaseInfoEntity tipInfo = new BaseInfoEntity();
        if(StringUtil.isNotEmpty(id)) {
            tipInfo = tipService.getTipInfoById(id);
        }
        return responseOfGet(tipInfo);
    }
}
