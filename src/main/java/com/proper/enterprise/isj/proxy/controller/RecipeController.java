package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by think on 2016/9/13 0013.
 * 
 * 缴费
 */

@RestController
@RequestMapping(path = "/recipes")
public class RecipeController extends BaseController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    AliService aliService;

    @Autowired
    OrderService orderService;

    @Autowired
    WeixinService weixinService;

    /**
     * 缴费记录查询
     * 
     * @param memberId
     *            家庭成员Id
     * @param searchStatus
     *            缴费状态
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RecipeDocument>> getAgreement(@RequestParam(required = true) String memberId,
            String searchStatus) throws Exception {
        User user = userService.getCurrentUser();
        List<RecipeDocument> recipeList = new ArrayList<>();
        if (user != null) {
            BasicInfoDocument basicInfo = null;
            if (StringUtil.isEmpty(memberId)) {
                basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
            } else {
                basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
            }
            if (basicInfo != null) {
                if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
                    userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), basicInfo.getId(), null);
                    if (StringUtil.isEmpty(memberId)) {
                        basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
                    } else {
                        basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
                    }
                }
//                List<RecipeOrderDocument> orderList = recipeService.findPatientRecipeOrderList(basicInfo.getId());
//
//                for (RecipeOrderDocument recipeOrderDocument : orderList) {
//                    if (StringUtil.isEmpty(recipeOrderDocument.getRecipeNonPaidDetail().getPayChannelId())) {
//                        continue;
//                    }
//                    try {
//                        recipeService.checkRecipeOrderIsPay(recipeOrderDocument);
//                    } catch (Exception e) {
//                        LOGGER.debug("缴费单初始化校验失败,门诊流水号:" + recipeOrderDocument.getClinicCode(), e);
//                    }
//
//                }
                recipeList = recipeService.findRecipeDocumentByUserAndDate(basicInfo, searchStatus, null, null);
                if (recipeList.size() > 0) {
                    Collections.sort(recipeList, new Comparator<RecipeDocument>() {
                        @Override
                        public int compare(RecipeDocument o1, RecipeDocument o2) {
                            return 0 - o1.getOutpatientDate().compareTo(o2.getOutpatientDate());
                        }
                    });
                }
            }
        }
        return responseOfGet(recipeList);
    }

}
