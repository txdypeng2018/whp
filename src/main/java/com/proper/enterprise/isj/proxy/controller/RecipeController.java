package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.order.service.OrderService;
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
 * 缴费.
 * Created by think on 2016/9/13 0013.
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
    OrderService orderService;

    /**
     * 缴费记录查询.
     * 
     * @param memberId
     *            家庭成员Id.
     * @param searchStatus
     *            缴费状态.
     * @return 返回给调用方的应答.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RecipeDocument>> getAgreement(@RequestParam String memberId,
            String searchStatus) throws Exception {
        User user = userService.getCurrentUser();
        List<RecipeDocument> recipeList = new ArrayList<>();
        if (user != null) {
            BasicInfoDocument basicInfo;
            if (StringUtil.isEmpty(memberId)) {
                basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
            } else {
                basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
            }
            if (basicInfo != null) {
                if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
                    // 在线建档
                    userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), basicInfo.getId(), null);
                    // 没有有家庭成员ID,获取用户基本信息
                    if (StringUtil.isEmpty(memberId)) {
                        basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
                        // 有家庭成员ID,获取用户基本信息
                    } else {
                        basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
                    }
                }
                // 获取用户缴费信息列表
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
