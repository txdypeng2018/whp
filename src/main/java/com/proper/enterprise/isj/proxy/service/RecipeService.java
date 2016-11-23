package com.proper.enterprise.isj.proxy.service;

import java.util.List;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;

/**
 * Created by think on 2016/9/13 0013.
 */
public interface RecipeService {

    /**
     * 获得人员的缴费信息
     */
    List<RecipeDocument> findRecipeDocumentByUserAndDate(BasicInfoDocument basic, String payStatus, String sDate,

            String eDate) throws Exception;

    /**
     * 生成缴费订单
     */
    RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception;

    // RecipeOrderDocument createRecipeOrder(UserInfoDocument userInfo, String
    // outpatientNum, String orderNum,
    // BigDecimal totalBig, String recipeNumStr);

    Order saveUpdateRecipeAndOrder(String orderNo, String channelId, Object infoObj) throws Exception;

    RecipeOrderDocument getRecipeOrderDocumentById(String id);

    public PayOrderReq convertAppInfo2PayOrder(Order order, Object infoObj) throws Exception;

    ResModel<PayList> findPayListModel(BasicInfoDocument basic, String clinicCode, String payStatus, String sDate,

            String eDate, boolean userCache) throws Exception;



    void saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument recipe, RefundByHis refund) throws Exception;

    RecipeOrderDocument getRecipeOrderDocumentByClinicCode(String clinicCode);

    /**
     * 校验订单下的金额与待缴费的金额是否一致
     *
     * @param orderNum
     * @return
     */
    boolean checkRecipeAmount(String orderNum, String orderAmount, PayChannel payWay) throws Exception;

    /**
     * 根据患者Id获得缴费订单(一个月内)
     */
    List<RecipeOrderDocument> findPatientRecipeOrderList(String patientId);

    void checkRecipeOrderIsPay(RecipeOrderDocument recipeOrderDocument) throws Exception;
}
