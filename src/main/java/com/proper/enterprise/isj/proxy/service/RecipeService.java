package com.proper.enterprise.isj.proxy.service;

import java.util.List;
import java.util.Map;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;

/**
 * 缴费服务.
 * Created by think on 2016/9/13 0013.
 */
public interface RecipeService {

    /**
     * 通过ID获取缴费订单信息.
     *
     * @param id 缴费订单ID.
     * @return 缴费对象.
     */
    RecipeOrderDocument getRecipeOrderDocumentById(String id);

    /**
     * 通过门诊流水号获取缴费信息.
     *
     * @param clinicCode 门诊流水号.
     * @return 缴费对象.
     */
    RecipeOrderDocument getRecipeOrderDocumentByClinicCode(String clinicCode);

    /**
     * 获得人员的缴费信息(Controller调用).
     *
     * @param basic 基础信息.
     * @param payStatus (0: 未支付, 1: 已支付, 2:已退款, 其他: 全部)支付状态.
     * @param sDate 开始时间.
     * @param eDate 结束时间.
     * @return 缴费报文.
     * @throws Exception 异常.
     */
    List<RecipeDocument> findRecipeDocumentByUserAndDate(BasicInfoDocument basic, String payStatus, String sDate,
            String eDate) throws Exception;

    /**
     * 生成缴费订单.
     *
     * @param memberId 成员ID.
     * @param clinicCode 门诊流水号.
     * @return 缴费对象.
     * @throws Exception 异常.
     */
    RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception;

    /**
     * 保存缴费及订单信息(异步通知调用).
     *
     * @param orderNo 订单号.
     * @param channelId 支付方式.
     * @param infoObj 支付对象.
     * @return order 订单信息.
     * @throws Exception 异常.
     * @throws Throwable 
     */
    Order saveUpdateRecipeAndOrder(String orderNo, String channelId, Object infoObj) throws Throwable;

    /**
     * 将APP转化为向HIS请求对象.
     *
     * @param order 订单信息.
     * @param infoObj 支付对象.
     * @return 向HIS请求对象.
     */
    PayOrderReq convertAppInfo2PayOrder(Order order, Object infoObj) throws Exception;

    /**
     * 获取支付详情信息.
     *
     * @param basic 用户基本信息.
     * @param clinicCode 门诊流水号.
     * @param payStatus 支付状态.
     * @param sDate 开始日期.
     * @param eDate 结算日期.
     * @param userCache 缓存.
     * @return 获取支付详情信息.
     * @throws Exception 异常.
     */
    ResModel<PayList> findPayListModel(BasicInfoDocument basic, String clinicCode, String payStatus, String sDate,
            String eDate, boolean userCache) throws Exception;

    /**
     * (HIS线下退费)保存退款以及更新缴费信息.
     *
     * @param recipe 缴费对象.
     * @param refund 退款对象.
     * @throws Exception 异常.
     */
    void saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument recipe, RefundByHis refund) throws Exception;

    /**
     * 校验订单下的金额与待缴费的金额是否一致.
     *
     * @param orderNum 订单编号.
     * @param orderAmount 订单金额.
     * @param payWay 支付渠道.
     * @return 校验结果.
     * @throws Exception 异常.
     */
    boolean checkRecipeAmount(String orderNum, String orderAmount, PayChannel payWay) throws Exception;

    /**
     * 推送挂号相关信息
     *
     * @param recipeInfo 缴费信息.
     * @param pushMsgType 推送消息类别.
     * @param pushObj 推送消息对象.
     * @throws Exception
     */
    void sendRecipeMsg(RecipeOrderDocument recipeInfo, SendPushMsgEnum pushMsgType, Object pushObj) throws Exception;

    Map<String, String> getRecipeRequestOrderNoMap(RecipeOrderDocument regBack);

    boolean refundMoney2User(Order order, String refundNo, boolean refundFlag) throws Exception;

    /**
     * 通知HIS缴费成功,HIS返回失败后,校验支付平台与HIS已缴费的金额是否相等,相等的条件在将多余的金额进行退款.
     *
     * @param order 订单.
     * @param regBack 挂号退款信息.
     * @param requestOrderNoMap 订单请求.
     * @param basicInfo 基本信息.
     * @param detail 详细信息.
     * @return 结果.
     * @throws Exception 异常.
     */
    boolean checkRecipeFailCanRefund(Order order, RecipeOrderDocument regBack, Map<String, String> requestOrderNoMap,
            BasicInfoDocument basicInfo, RecipePaidDetailDocument detail) throws Exception;

    /**
     * 支付平台返回结果后,更新表中的字段,并推送消息.
     *
     * @param order 订单.
     * @param channelId 渠道编号.
     * @param regBack 退款报文.
     * @param refundNo 退款编号.
     * @param detail 详细信息.
     * @param refundFlag 退款标识(1:退款成功,0:退款失败).
     * @throws Exception 异常.
     */
    Order saveOrUpdateOrderFailInfo(Order order, String channelId, RecipeOrderDocument regBack, String refundNo,
            RecipePaidDetailDocument detail, boolean refundFlag) throws Exception;

    /**
     * 通过患者ID查询缴费信息.
     */
    List<RecipeOrderDocument> findRecipeOrderDocumentList(String patientId);
}
