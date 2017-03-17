package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.SearchStatusContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.recipe.RecipeGetAgreementBusiness;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;

/**
 * 缴费.
 * Created by think on 2016/9/13 0013.
 */

@RestController
@RequestMapping(path = "/recipes")
public class RecipeController extends IHosBaseController {

    /**
     * 缴费记录查询.
     * 
     * @param memberId
     *            家庭成员Id.
     * @param searchStatus
     *            缴费状态.
     * @return 返回给调用方的应答.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RecipeDocument>> getAgreement(@RequestParam String memberId, String searchStatus)
            throws Exception {
        return responseOfGet((List<RecipeDocument>) toolkit.execute(RecipeGetAgreementBusiness.class, c -> {
            ((MemberIdContext<List<RecipeDocument>>) c).setMemberId(memberId);
            ((SearchStatusContext<List<RecipeDocument>>) c).setSearchStatus(searchStatus);
        }));

    }

}
