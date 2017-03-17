package com.proper.enterprise.isj.proxy.business.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class RecipeGetAgreementBusiness<T, M extends MemberIdContext<Object> & SearchStatusContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Autowired
    RecipeService recipeService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Override
    public void process(M ctx) throws Exception {
        String memberId = ctx.getMemberId();
        String searchStatus = ctx.getSearchStatus();

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
        ctx.setResult(recipeList);
    }

}
