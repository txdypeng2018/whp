package com.proper.enterprise.isj.proxy.service.notx;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.ChannelIdContext;
import com.proper.enterprise.isj.context.ClinicCodeContext;
import com.proper.enterprise.isj.context.EndDateContext;
import com.proper.enterprise.isj.context.InfoObjContext;
import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.OrderAmountContext;
import com.proper.enterprise.isj.context.OrderNumContext;
import com.proper.enterprise.isj.context.PayChannelContext;
import com.proper.enterprise.isj.context.PayStatusContext;
import com.proper.enterprise.isj.context.RecipeOrderDocIdContext;
import com.proper.enterprise.isj.context.RecipeOrderDocumentContext;
import com.proper.enterprise.isj.context.RefundByHisContext;
import com.proper.enterprise.isj.context.StartDateContext;
import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.function.message.SendRecipeMsgFunction;
import com.proper.enterprise.isj.function.recipe.CheckRecipeFailCanRefundFunction;
import com.proper.enterprise.isj.function.recipe.ConvertAppInfo2PayOrderFunction;
import com.proper.enterprise.isj.function.recipe.FetchRecipeRequestOrderNoMapFunction;
import com.proper.enterprise.isj.function.recipe.FindPayListModelFunction;
import com.proper.enterprise.isj.function.recipe.RefundMoney2UserFunction;
import com.proper.enterprise.isj.function.recipe.SaveOrUpdateOrderFailInfoFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.business.recipe.CheckRecipeAmountBusiness;
import com.proper.enterprise.isj.proxy.business.recipe.FetchRecipeOrderDocumentByIdBusiness;
import com.proper.enterprise.isj.proxy.business.recipe.FindRecipeDocumentByUserAndDateBusiness;
import com.proper.enterprise.isj.proxy.business.recipe.GenerateOrderAndRecipeOrderDocumentBusiness;
import com.proper.enterprise.isj.proxy.business.recipe.SaveRefundAndUpdateRecipeOrderDocumentBusiness;
import com.proper.enterprise.isj.proxy.business.recipe.SaveUpdateRecipeAndOrderBusiness;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;

@Service
public class RecipeServiceNotxImpl extends AbstractService implements RecipeService {

    /**
     * 通过ID获取缴费订单信息.
     *
     * @param id 缴费订单ID.
     * @return 缴费对象.
     */
    public RecipeOrderDocument getRecipeOrderDocumentById(String id) {
        return toolkit.execute(FetchRecipeOrderDocumentByIdBusiness.class, (c) -> {
            ((RecipeOrderDocIdContext<?>) c).setRecipeOrderDocId(id);
        });
    }

    /**
     * 通过门诊流水号获取缴费信息.
     *
     * @param clinicCode 门诊流水号.
     * @return 缴费对象.
     */
    @Override
    public RecipeOrderDocument getRecipeOrderDocumentByClinicCode(String clinicCode) {
        return toolkit.executeRepositoryFunction(RecipeOrderRepository.class, "getByClinicCode", clinicCode);
    }

    /**
     * 推送挂号相关信息
     *
     * @param recipeInfo 缴费信息.
     * @param pushMsgType 推送消息类别.
     * @param pushObj 推送消息对象.
     * @throws Exception
     */
    @Override
    public void sendRecipeMsg(RecipeOrderDocument recipeInfo, SendPushMsgEnum pushMsgType, Object pushObj)
            throws Exception {
        toolkit.executeFunction(SendRecipeMsgFunction.class, recipeInfo, pushMsgType, pushObj);
    }

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
    @Override
    public List<RecipeDocument> findRecipeDocumentByUserAndDate(BasicInfoDocument basic, String payStatus, String sDate,
            String eDate) throws Exception {
        return toolkit.execute(FindRecipeDocumentByUserAndDateBusiness.class, ctx -> {
            ((BasicInfoDocumentContext<?>) ctx).setBasicInfoDocument(basic);
            ((PayStatusContext<?>) ctx).setPayStatus(payStatus);
            ((StartDateContext<?>) ctx).setStartDate(sDate);
            ((EndDateContext<?>) ctx).setEndDate(eDate);
        });
    }

    /**
     * 生成缴费订单.
     *
     * @param memberId 成员ID.
     * @param clinicCode 门诊流水号.
     * @return 缴费对象.
     * @throws Exception 异常.
     */
    @Override
    public RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception {
        return toolkit.execute(GenerateOrderAndRecipeOrderDocumentBusiness.class, ctx -> {
            ((MemberIdContext<?>) ctx).setMemberId(memberId);
            ((ClinicCodeContext<?>) ctx).setClinicCode(clinicCode);
        });
    }

    /**
     * 保存缴费及订单信息(异步通知调用).
     *
     * @param orderNo 订单号.
     * @param channelId 支付方式.
     * @param infoObj 支付对象.
     * @return order 订单信息.
     * @throws Exception 异常.
     */
    @Override
    public Order saveUpdateRecipeAndOrder(String orderNo, String channelId, Object infoObj) throws Throwable {
        return toolkit.execute(SaveUpdateRecipeAndOrderBusiness.class, c -> {
            ((OrderNumContext<?>) c).setOrderNum(orderNo);
            ((ChannelIdContext<?>) c).setChannelId(channelId);
            ((InfoObjContext<?>) c).setInfoObj(infoObj);
        }, e -> {
            if(e instanceof DelayException){
                throw e;
            }else{
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 调用支付平台的退款接口.
     *
     * @param order 订单.
     * @param refundNo 退款编号.
     * @param refundFlag 退款标识.
     * @return 是否成功退款.
     * @throws Exception 异常.
     */
    @Override
    public boolean refundMoney2User(Order order, String refundNo, boolean refundFlag) throws Exception {
        return toolkit.executeFunction(RefundMoney2UserFunction.class, order, refundNo, refundFlag);
    }

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
    @Override
    public Order saveOrUpdateOrderFailInfo(Order order, String channelId, RecipeOrderDocument regBack, String refundNo,
            RecipePaidDetailDocument detail, boolean refundFlag) throws Exception {
        return toolkit.executeFunction(SaveOrUpdateOrderFailInfoFunction.class, order, channelId, regBack, refundNo,
                detail, refundFlag);
    }

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
    @Override
    public boolean checkRecipeFailCanRefund(Order order, RecipeOrderDocument regBack,
            Map<String, String> requestOrderNoMap, BasicInfoDocument basicInfo, RecipePaidDetailDocument detail)
            throws Exception {
        return toolkit.executeFunction(CheckRecipeFailCanRefundFunction.class, order, regBack, requestOrderNoMap,
                basicInfo, detail);
    }

    /**
     * 获取已经缴费的Map信息.
     *
     * @param regBack 缴费信息.
     * @return 已经缴费的Map信息.
     */
    @Override
    public Map<String, String> getRecipeRequestOrderNoMap(RecipeOrderDocument regBack) {
        return toolkit.executeFunction(FetchRecipeRequestOrderNoMapFunction.class, regBack);
    }

    /**
     * 将APP转化为向HIS请求对象.
     *
     * @param order 订单信息.
     * @param infoObj 支付对象.
     * @return 向HIS请求对象.
     */
    @Override
    public PayOrderReq convertAppInfo2PayOrder(Order order, Object infoObj) {
        return toolkit.executeSimpleFunction(ConvertAppInfo2PayOrderFunction.class, order, infoObj);
    }

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
    @Override
    public ResModel<PayList> findPayListModel(BasicInfoDocument basic, String clinicCode, String payStatus,
            String sDate, String eDate, boolean userCache) throws Exception {
        return toolkit.executeFunction(FindPayListModelFunction.class, basic, clinicCode, payStatus, sDate, eDate,
                userCache);
    }

    /**
     * (HIS线下退费)保存退款以及更新缴费信息.
     *
     * @param recipe 缴费对象.
     * @param refund 退款对象.
     * @throws Exception 异常.
     */
    @Override
    public void saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument recipe, RefundByHis refund)
            throws Exception {
        toolkit.execute(SaveRefundAndUpdateRecipeOrderDocumentBusiness.class, ctx -> {
            ((RecipeOrderDocumentContext<?>) ctx).setRecipeOrderDocument(recipe);
            ((RefundByHisContext<?>) ctx).setRefundByHis(refund);
        });
    }

    /**
     * 校验订单下的金额与待缴费的金额是否一致.
     *
     * @param orderNum 订单编号.
     * @param orderAmount 订单金额.
     * @param payWay 支付渠道.
     * @return 校验结果.
     * @throws Exception 异常.
     */
    @Override
    public boolean checkRecipeAmount(String orderNum, String orderAmount, PayChannel payWay) throws Exception {
        return toolkit.execute(CheckRecipeAmountBusiness.class, ctx -> {
            ((OrderNumContext<?>) ctx).setOrderNum(orderNum);
            ((OrderAmountContext<?>) ctx).setOrderAmount(orderAmount);
            ((PayChannelContext<?>) ctx).setPayChannel(payWay);
        });
    }

    @Override
    public List<RecipeOrderDocument> findRecipeOrderDocumentList(String patientId) {
        return toolkit.executeRepositoryFunction(RecipeOrderRepository.class, "findByPatientIdOrderByCreateTimeDesc", patientId);
    }

}
