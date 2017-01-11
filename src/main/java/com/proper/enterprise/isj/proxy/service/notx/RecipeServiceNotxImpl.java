package com.proper.enterprise.isj.proxy.service.notx;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.entity.AliEntity;
import com.proper.enterprise.isj.pay.ali.model.Ali;
import com.proper.enterprise.isj.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.model.AliRefundRes;
import com.proper.enterprise.isj.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbQueryRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.*;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.entity.WeixinEntity;
import com.proper.enterprise.isj.pay.weixin.model.Weixin;
import com.proper.enterprise.isj.pay.weixin.model.WeixinPayQueryRes;
import com.proper.enterprise.isj.pay.weixin.model.WeixinRefundReq;
import com.proper.enterprise.isj.pay.weixin.model.WeixinRefundRes;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.document.recipe.*;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.enmus.QueryType;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.PayListReq;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.PayOrder;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/9/18 0018.
 */
@Service
public class RecipeServiceNotxImpl implements RecipeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeServiceNotxImpl.class);

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    RecipeServiceImpl recipeServiceImpl;

    @Autowired
    WeixinService weixinService;

    @Autowired
    AliService aliService;

    @Autowired
    CmbService cmbService;

    @Autowired
    MessagesService messagesService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;


    @Override
    public List<RecipeDocument> findRecipeDocumentByUserAndDate(BasicInfoDocument basic, String payStatus, String sDate,
            String eDate) throws Exception {
        DecimalFormat df = new DecimalFormat("0.00");
        List<RecipeDocument> recipeList = new ArrayList<>();
        try {
            ResModel<PayList> payListRes = this.findPayListModel(basic, null, payStatus, sDate, eDate, true);
            ResModel<PayList> refundPayListRes = null;
            if (StringUtil.isNull(payStatus) || payStatus.equals(String.valueOf(1))) {
                refundPayListRes = this.findPayListModel(basic, null, String.valueOf(2), sDate, eDate, true);
            }
            Map<String, RecipeDetailDocument> detailMap = new LinkedHashMap<>();
            Map<String, RecipeDocument> recipeMap = new LinkedHashMap<>();
            RecipeDetailDocument detail = null;
            List<RecipeDetailItemDocument> itemList = null;
            RecipeDetailItemDocument item = null;
            BigDecimal totalBig = null;
            RecipeOrderDocument recipeOrder = null;
            if (payListRes.getReturnCode() == ReturnCode.SUCCESS) {
                List<Pay> payList = new ArrayList<>();
                payList.addAll(payListRes.getRes().getPayList());
                if (refundPayListRes != null) {
                    List<Pay> refundList = refundPayListRes.getRes().getPayList();
                    for (Pay refundPay : refundList) {
                        Pay rPay = new Pay();
                        BeanUtils.copyProperties(refundPay, rPay);
                        rPay.setOwnCost(refundPay.getOwnCost().replace("-", ""));
                        rPay.setUnitPrice(Math.abs(refundPay.getUnitPrice()));
                        rPay.setQty(refundPay.getQty().replace("-", ""));
                        payList.add(rPay);
                    }
                    payList.addAll(refundList);
                }
                RecipeDocument recipe = null;
                List<RecipeDetailDocument> detailList = null;
                String recipeKey = null;
                for (Pay pay : payList) {
                    recipe = recipeMap.get(pay.getClinicCode());
                    if (recipe == null) {
                        recipe = new RecipeDocument();
                        recipeMap.put(pay.getClinicCode(), recipe);
                    }
                    recipe.setOutpatientNum(pay.getClinicCode());
                    recipe.setOutpatientDate(
                            DateUtil.toString(DateUtil.toDate(pay.getRegDate().split(" ")[0]), "yyyy年MM月dd日"));
                    detailList = recipe.getRecipes();
                    if (pay.getOwnCost().contains("-")) {
                        recipeKey = pay.getRecipeNo().concat("_").concat(pay.getExecDpnm()).concat("-");
                    } else {
                        recipeKey = pay.getRecipeNo().concat("_").concat(pay.getExecDpnm());
                    }
                    detail = detailMap.get(recipeKey);
                    if (detail == null) {
                        detail = new RecipeDetailDocument();
                        detail.setDept(pay.getExecDpnm());
                        detail.setRecipeNum(pay.getRecipeNo());
                        detail.setLocation("");
                        if (pay.getOwnCost().contains("-")) {
                            detail.setStatusCode(String.valueOf(2));

                        } else {
                            detail.setStatusCode(pay.getPayFlag());
                        }
                        detail.setStatus(CenterFunctionUtils.getRecipeItemStatusName(detail.getStatusCode()));
                        detailMap.put(recipeKey, detail);
                        detailList.add(detail);
                    }
                    itemList = detail.getItems();
                    item = new RecipeDetailItemDocument();
                    item.setName(pay.getItemName());
                    String num = new BigDecimal(pay.getQty())
                            .divide(new BigDecimal(pay.getPackQty()), BigDecimal.ROUND_HALF_UP).toString();
                    item.setNumber(num);
                    item.setAmount(df
                            .format(new BigDecimal(String.valueOf(pay.getUnitPrice())).divide(new BigDecimal("100"))));
                    itemList.add(item);
                    if (StringUtil.isEmpty(detail.getTotal())) {
                        totalBig = new BigDecimal(String.valueOf(0));
                    } else {
                        totalBig = new BigDecimal(detail.getTotal());
                    }
                    totalBig = totalBig.add(new BigDecimal(pay.getOwnCost()).divide(new BigDecimal("100")));
                    detail.setTotal(totalBig.toString());
                }

                for (RecipeDocument recipeDocument : recipeMap.values()) {
                    recipeList.add(recipeDocument);
                }
            } else {
                if (payListRes.getReturnCode() != ReturnCode.EMPTY_RETURN) {
                    throw new HisReturnException(payListRes.getReturnMsg());
                }
            }
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS返回结果异常", e);
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        }
        return recipeList;
    }

    @Override
    public RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception {
        RecipeOrderDocument recipeOrder = null;
        try {
            recipeOrder = recipeServiceImpl.saveOrderAndRecipeOrderDocument(memberId, clinicCode);
            BasicInfoDocument basicInfo = userInfoService
                    .getFamilyMemberByUserIdAndMemberId(recipeOrder.getCreateUserId(), memberId);
            if (basicInfo != null) {
                for (QueryType queryType : QueryType.values()) {
                    webService4HisInterfaceCacheUtil.evictCachePayListRes(recipeOrder.getPatientId(), queryType.name(),
                            basicInfo.getMedicalNum());
                }
            }
        } catch (Exception e) {
            RecipeOrderDocument order = this.getRecipeOrderDocumentByClinicCode(clinicCode);
            if (order != null) {
                recipeOrderRepository.delete(order);
            }
            LOGGER.debug("保存缴费订单信息出现异常,门诊流水号:" + clinicCode, e);
            throw e;
        }
        return recipeOrder;
    }

    @Override
    public Order saveUpdateRecipeAndOrder(String orderNo, String channelId, Object infoObj) throws Exception {
        synchronized (orderNo) {
            Order order = orderService.findByOrderNo(orderNo);
            if (order == null) {
                LOGGER.debug("未查到缴费订单,调用缴费前校验,订单号:" + orderNo);
                return null;
            }
            RecipeOrderDocument regBack = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
            if (regBack == null) {
                LOGGER.debug("未查到缴费单,调用缴费前校验,订单号:" + orderNo);
                return null;
            }
            BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(regBack.getCreateUserId(),
                    regBack.getPatientId());
            if (basicInfo == null) {
                LOGGER.debug("未查到患者信息,调用缴费前校验,门诊流水号:" + regBack.getClinicCode());
                return null;
            }
            if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
                LOGGER.debug(
                        "患者信息的病历号为空,不能进行调用,调用缴费前校验,门诊流水号:" + regBack.getClinicCode() + ",患者Id:" + basicInfo.getId());
                return null;
            }
            // 手动清空患者缴费缓存
            for (QueryType queryType : QueryType.values()) {
                webService4HisInterfaceCacheUtil.evictCachePayListRes(regBack.getPatientId(), queryType.name(),
                        basicInfo.getMedicalNum());
            }
            Map<String, String> requestOrderNoMap = getRecipeRequestOrderNoMap(regBack);
            if (requestOrderNoMap.containsKey(order.getOrderNo())) {
                LOGGER.debug("订单号重复调用缴费接口,直接返回,不对HIS接口进行调用,门诊流水号:" + regBack.getClinicCode());
                return null;
            }
            try {
                PayOrderReq payOrderReq = this.convertAppInfo2PayOrder(order, infoObj);
                if(payOrderReq==null){
                    LOGGER.debug("诊间缴费请求参数转换异常,订单号:"+order.getOrderNo());
                    throw new RecipeException("诊间缴费请求参数转换异常,订单号:"+order.getOrderNo());
                }
                RecipeOrderReqDocument payReq = new RecipeOrderReqDocument();
                BeanUtils.copyProperties(payOrderReq, payReq);
                Map<String, RecipeOrderReqDocument> reqMap = regBack.getRecipeOrderReqMap();
                reqMap.put(payOrderReq.getOrderId(), payReq);
                regBack.setRecipeOrderReqMap(reqMap);
                regBack = recipeServiceImpl.saveRecipeOrderDocument(regBack);
                order = this.updateRegistrationAndOrder(order, payOrderReq, regBack, channelId);
            } catch (Exception e) {
                LOGGER.info("诊间缴费出现异常", e);
                String refundNo = order.getOrderNo() + "001";
                if(order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                    refundNo = order.getOrderNo().substring(0, 18) + "01";
                }
                RecipePaidDetailDocument detail = regBack.getRecipeNonPaidDetail();
                if (detail == null) {
                    detail = new RecipePaidDetailDocument();
                }
                detail.setRefundNum(refundNo);
                LOGGER.debug("退款单号:" + refundNo, e);
                if (StringUtil.isEmpty(detail.getDescription())) {
                    detail.setDescription("");
                }
                LOGGER.debug("保存异常消息前,退款单号:" + refundNo);
                if(StringUtil.isNotEmpty(e.getMessage())){
                    detail.setDescription(detail.getDescription().concat(",").concat(e.getMessage()));
                }
                LOGGER.debug("保存异常消息后,退款单号:" + refundNo);
                requestOrderNoMap.put(order.getOrderNo(), String.valueOf(order.getPayWay()));
                LOGGER.debug("将订单号添加到计算平台缴费情况的Map中,退款单号:" + refundNo + ",订单号:" + order.getOrderNo());
                if (!checkRecipeFailCanRefund(order, regBack, requestOrderNoMap, basicInfo, detail)) {
                    LOGGER.debug("诊间缴费失败后,核对平台与HIS已缴金额不一致,不能进行退款,退款单号:" + refundNo + ",订单号:" + order.getOrderNo());
                    return null;
                }
                try {
                    boolean refundFlag = refundMoney2User(order, refundNo, false);
                    order = saveOrUpdateOrderFailInfo(order, channelId, regBack, refundNo, detail, refundFlag);
                    regBack.getRecipePaidFailDetailList().add(detail);
                    RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
                    regBack.setRecipeNonPaidDetail(nonPaid);
                    recipeServiceImpl.saveRecipeOrderDocument(regBack);
                } catch (Exception e2) {
                    LOGGER.debug("RecipeServiceNotxImpl.saveUpdateRecipeAndOrder[Exception]:", e2);
                    LOGGER.debug("诊间缴费HIS抛异常,调用支付平台退费失败,订单号:" + order.getOrderNo() + ",退费单号:" + refundNo);
                    detail.setDescription(detail.getDescription()
                            .concat(",诊间缴费HIS抛异常,调用支付平台退费失败,订单号:" + order.getOrderNo() + ",退费单号:" + refundNo));
                    regBack.getRecipePaidFailDetailList().add(detail);
                    RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
                    regBack.setRecipeNonPaidDetail(nonPaid);
                    recipeServiceImpl.saveRecipeOrderDocument(regBack);
                    sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL);
                    // throw e2;
                }
                // throw e;
            }
            // 手动清空患者缴费缓存
            for (QueryType queryType : QueryType.values()) {
                webService4HisInterfaceCacheUtil.evictCachePayListRes(regBack.getPatientId(), queryType.name(),
                        basicInfo.getMedicalNum());
            }
            return order;
        }
    }

    public Order updateRegistrationAndOrder(Order order, PayOrderReq payOrderReq, RecipeOrderDocument recipeOrder,
            String channelId) throws Exception {
        ResModel<PayOrder> payOrderResModel = null;
        try {
            payOrderResModel = webServicesClient.payOrder(payOrderReq);
        } catch (InvocationTargetException ite) {
            if(ite.getCause() != null && ite.getCause() instanceof RemoteAccessException) {
                LOGGER.debug("缴费网络连接异常", ite);
                return null;
            }
            throw ite;
        }
        if (payOrderResModel == null) {
            LOGGER.debug("诊间缴费返回信息解析失败,不能确定缴费是否成功");
            RecipeOrderHisDocument payHis = new RecipeOrderHisDocument();
            payHis.setClientReturnMsg("诊间缴费返回信息解析失败,不能确定缴费是否成功");
            Map<String, RecipeOrderHisDocument> hisMap = recipeOrder.getRecipeOrderHisMap();
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            throw new HisReturnException("诊间缴费返回信息解析失败,不能确定缴费是否成功");
        }
        RecipeOrderHisDocument payHis = new RecipeOrderHisDocument();
        Map<String, RecipeOrderHisDocument> hisMap = recipeOrder.getRecipeOrderHisMap();
        payHis.setClientReturnMsg(payOrderResModel.getReturnMsg() + "(" + payOrderResModel.getReturnCode() + ")");
        if (payOrderResModel.getReturnCode() == ReturnCode.SUCCESS) {
            BeanUtils.copyProperties(payOrderResModel.getRes(), payHis);
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeOrder.getRecipePaidDetailList().add(recipeOrder.getRecipeNonPaidDetail());
            recipeOrder.setRecipeNonPaidDetail(new RecipePaidDetailDocument());
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            order.setOrderStatus(String.valueOf(2));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
            order.setPayWay(String.valueOf(channelId));
            // order = orderService.save(order);
            sendRecipeSuccessMsg(order.getFormId());
        } else {
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            throw new HisReturnException(payOrderResModel.getReturnMsg());
        }
        return order;
    }

    /**
     * 调用支付平台的退款接口
     *
     * @param order
     * @param refundNo
     * @param refundFlag
     * @return
     * @throws Exception
     */
    private boolean refundMoney2User(Order order, String refundNo, boolean refundFlag)
            throws Exception {
        if (order.getPayWay().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(order.getOrderAmount()))
                    .divide(new BigDecimal("100"));
            AliRefundRes refunRes = aliService.saveAliRefundResProcess(order.getOrderNo(), refundNo,
                    bigDecimal.toString(), null);
            if (refunRes != null && refunRes.getCode().equals("10000")) {
                refundFlag = true;
            } else {
                LOGGER.debug("未查到需要退费的项目,或者退费接口返回异常,支付宝订单号:" + order.getOrderNo());
            }
        } else if (order.getPayWay().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            WeixinRefundReq weixinReq = new WeixinRefundReq();
            weixinReq.setOutRefundNo(refundNo);
            weixinReq.setRefundFee(Integer.parseInt(order.getOrderAmount()));
            weixinReq.setTotalFee(Integer.parseInt(order.getOrderAmount()));
            weixinReq.setOutTradeNo(order.getOrderNo());
            PayResultRes resultRes = weixinService.saveWeixinRefund(weixinReq);
            if (resultRes.getResultCode().equals("0")) {
                refundFlag = true;
            } else {
                LOGGER.debug("未查到需要退费的项目,或者退费接口返回异常,微信订单号:" + order.getOrderNo());
            }
            // 一网通缴费退款操作
        }  else if (order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(order.getOrderAmount()))
                    .divide(new BigDecimal("100"));
            // 生成一网通退款请求对象
            RefundNoDupBodyReq refundInfo = new RefundNoDupBodyReq();
            CmbPayEntity cmbInfo = cmbService.getQueryInfo(order.getOrderNo());
            // 原订单号
            refundInfo.setBillNo(cmbInfo.getBillNo());
            // 交易日期
            refundInfo.setDate(cmbInfo.getDate());
            // 退款流水号
            refundInfo.setRefundNo(refundNo);
            // 退款金额
            refundInfo.setAmount(bigDecimal.toString());
            RefundNoDupRes refunRes = cmbService.saveRefundResult(refundInfo);
            if (refunRes != null && StringUtil.isNull(refunRes.getHead().getCode())) {
                refundFlag = true;
            } else {
                LOGGER.debug("未查到需要退费的项目,或者退费接口返回异常,一网通订单号:" + order.getOrderNo());
            }
        } else {
            LOGGER.debug("未查到订单号对应的支付平台,订单号:" + order.getOrderNo());
        }
        return refundFlag;
    }

    /**
     * 支付平台返回结果后,更新表中的字段,并推送消息
     *
     * @param order
     * @param channelId
     * @param regBack
     * @param refundNo
     * @param detail
     * @param refundFlag
     * @throws Exception
     */
    private Order saveOrUpdateOrderFailInfo(Order order, String channelId, RecipeOrderDocument regBack, String refundNo,
            RecipePaidDetailDocument detail, boolean refundFlag) throws Exception {
        if (refundFlag) {
            LOGGER.debug("诊间缴费HIS抛异常,对缴费进行退费,退费成功,订单号:" + order.getOrderNo() + ",退费单号:" + refundNo);
            order.setOrderStatus(String.valueOf(3));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(channelId));
            // orderService.save(order);
            sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_FAIL);
            detail.setRefundStatus(String.valueOf(1));
        } else {
            LOGGER.debug("诊间缴费HIS抛异常,对缴费进行退费,退费失败,订单号:" + order.getOrderNo() + ",退费单号:" + refundNo);
            order.setOrderStatus(String.valueOf(5));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(channelId));
            // orderService.save(order);
            sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL);
            detail.setRefundStatus(String.valueOf(0));
        }
        return order;
    }

    /**
     * 通知HIS缴费成功,HIS返回失败后,校验支付平台与HIS已缴费的金额是否相等,相等的条件在将多余的金额进行退款
     *
     * @param order
     * @param regBack
     * @param requestOrderNoMap
     * @param basicInfo
     * @param detail
     * @return
     * @throws Exception
     */
    private boolean checkRecipeFailCanRefund(Order order, RecipeOrderDocument regBack,
            Map<String, String> requestOrderNoMap, BasicInfoDocument basicInfo, RecipePaidDetailDocument detail)
            throws Exception {
        if(regBack==null||StringUtil.isEmpty(regBack.getClinicCode())){
            LOGGER.debug("缴费项目为空,或者是缴费流水号为空,订单号:" + order.getOrderNo());
            return false;
        }
        ResModel<PayList> paidRes = this.findPayListModel(basicInfo, regBack.getClinicCode(), String.valueOf(1), null,
                null, false);
        List<Pay> refundPayList = new ArrayList<>();
        if (paidRes == null) {
            LOGGER.debug("查询已缴费信息返回值不能进行解析,门诊流水号:" + regBack.getClinicCode()+",订单号:"+order.getOrderNo());
            sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL);
            return false;
        }
        if(paidRes.getReturnCode()==ReturnCode.EMPTY_RETURN){
            LOGGER.debug("HIS未查到已缴费项目,HIS返回信息:"+paidRes.getReturnMsg());
        } else if (paidRes.getReturnCode() == ReturnCode.SUCCESS) {
            refundPayList = paidRes.getRes().getPayList();
        }else{
            LOGGER.debug("HIS查询已缴费信息出现异常,HIS返回信息:" + paidRes.getReturnMsg());
            detail.setDescription("门诊流水号:" + regBack.getClinicCode() + "HIS查询已缴费信息出现异常,不进行自动退费,HIS返回信息:" + paidRes.getReturnMsg());
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeOrderRepository.save(regBack);
            sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL);
            return false;
        }
        List<RecipePaidDetailDocument> paidSuccessList = regBack.getRecipePaidDetailList();
        Set<String> seqSet = new HashSet<String>();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidSuccessList) {
            LOGGER.debug("HIS查询记录的已缴项目的序号:" + recipePaidDetailDocument.getHospSequence());
            seqSet.add(recipePaidDetailDocument.getHospSequence());
        }
        BigDecimal hisPaidBig = new BigDecimal("0");
        for (Pay pay : refundPayList) {
            if (!seqSet.contains(pay.getHospSequence())) {
                continue;
            }
            LOGGER.debug("HIS查询记录的已缴项目:" + pay.getItemCode() + ",缴费金额" + pay.getOwnCost());
            hisPaidBig = hisPaidBig.add(new BigDecimal(pay.getOwnCost()).abs());
        }
        BigDecimal queryBig = new BigDecimal(String.valueOf(0));
        try {
            for (Map.Entry<String, String> stringStringEntry : requestOrderNoMap.entrySet()) {
                if (stringStringEntry.getValue().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                    AliPayTradeQueryRes query = aliService.getAliPayTradeQueryRes(stringStringEntry.getKey());
                    if (query != null) {
                        queryBig = queryBig.add(new BigDecimal(query.getTotalAmount()).multiply(new BigDecimal("100")));
                    } else {
                        LOGGER.debug("未查到支付宝支付信息,订单号:" + stringStringEntry.getKey());
                    }
                    AliRefundTradeQueryRes refund = aliService.getAliRefundTradeQueryRes(stringStringEntry.getKey(),
                            stringStringEntry.getKey() + "001");
                    if (refund != null && StringUtil.isNotEmpty(refund.getRefundAmount())) {
                        queryBig = queryBig
                                .subtract(new BigDecimal(refund.getRefundAmount()).multiply(new BigDecimal("100")));
                    }
                } else if (stringStringEntry.getValue().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                    WeixinPayQueryRes query = weixinService.getWeixinPayQueryRes(stringStringEntry.getKey());
                    if (query != null) {
                        queryBig = queryBig.add(new BigDecimal(query.getTotalFee()));
                    } else {
                        LOGGER.debug("未查到微信支付信息,订单号:" + stringStringEntry.getKey());
                    }
                    WeixinRefundRes refund = weixinService.getWeixinRefundRes(stringStringEntry.getKey() + "001");
                    if (refund != null&&StringUtil.isNotEmpty(refund.getRefundFee())) {
                        queryBig = queryBig.subtract(new BigDecimal(refund.getRefundFee()));
                    }
                } else if(stringStringEntry.getValue().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                    // 查询缴费订单信息
                    QuerySingleOrderRes query = cmbService.getCmbPayQueryRes(stringStringEntry.getKey());
                    // 查询支付成功
                    if(query != null && StringUtil.isEmpty(query.getHead().getCode())) {
                        queryBig = queryBig.add(new BigDecimal(query.getBody().getBillAmount()).multiply(new BigDecimal("100")));
                    } else {
                        LOGGER.debug("未查到一网通支付信息,订单号:" + stringStringEntry.getKey());
                    }
                    // 退款信息查询
                    String orderNo = stringStringEntry.getKey();
                    CmbQueryRefundEntity queryRefundInfo = new CmbQueryRefundEntity();
                    CmbPayEntity cmbInfo = cmbService.getQueryInfo(orderNo);
                    // 订单号
                    queryRefundInfo.setBillNo(cmbInfo.getBillNo());
                    // 交易日期
                    queryRefundInfo.setDate(cmbInfo.getDate());
                    // 退款流水号
                    StringBuilder sb = new StringBuilder();
                    sb.append(cmbInfo.getDate()).append(cmbInfo.getBillNo()).append("01");
                    queryRefundInfo.setRefundNo(sb.toString());
                    QueryRefundRes refund = cmbService.queryRefundResult(queryRefundInfo);
                    if (refund != null && refund.getBody().getBillRecord() != null
                            && StringUtil.isNotEmpty(refund.getBody().getBillRecord().get(0).getAmount())) {
                        queryBig = queryBig.subtract(new BigDecimal(
                                refund.getBody().getBillRecord().get(0).getAmount()).multiply(new BigDecimal("100")));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("计算支付平台已缴金额发生异常,门诊流水号:" + regBack.getClinicCode() + ",订单号:" + order.getOrderNo(), e);
            sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL);
            return false;
        }

        LOGGER.debug("支付平台结余金额:" + queryBig + ",当前缴费金额:" + order.getOrderAmount() + ",HIS已缴费金额:" + hisPaidBig);
        detail.setDescription(detail.getDescription().concat(
                ",支付平台结余金额:" + queryBig + ",当前缴费金额:" + order.getOrderAmount() + ",HIS已缴费金额:" + hisPaidBig));
        if (queryBig.subtract(new BigDecimal(order.getOrderAmount())).compareTo(hisPaidBig) != 0) {
            LOGGER.debug(
                    "缴费失败后,HIS已缴费金额与支付平台的金额不相等,不能进行退款,门诊流水号:" + regBack.getClinicCode() + ",订单号:" + order.getOrderNo());
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeOrderRepository.save(regBack);
            sendRecipePaidFailMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL);
            return false;
        }
        return true;
    }

    private Map<String, String> getRecipeRequestOrderNoMap(RecipeOrderDocument regBack) {
        Map<String, String> requestOrderNoMap = new HashMap<String, String>();
        List<RecipePaidDetailDocument> paidList = regBack.getRecipePaidDetailList();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidList) {
            requestOrderNoMap.put(recipePaidDetailDocument.getOrderNum(), recipePaidDetailDocument.getPayChannelId());
        }
        if (regBack.getRecipeOrderReqMap() != null) {
            for (Map.Entry<String, RecipeOrderReqDocument> stringRecipeOrderReqDocumentEntry : regBack
                    .getRecipeOrderReqMap().entrySet()) {
                requestOrderNoMap.put(stringRecipeOrderReqDocumentEntry.getKey(),
                        String.valueOf(stringRecipeOrderReqDocumentEntry.getValue().getPayChannelId().getCode()));
            }
        }
        return requestOrderNoMap;
    }

    public void sendRecipePaidFailMsg(RecipeOrderDocument recipeOrder, SendPushMsgEnum msg) throws Exception {
        /*---------成功挂号提示-------*/
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(msg, recipeOrder));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(recipeOrder.getCreateUserId());
        regMsg.setUserName(recipeOrder.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }

    @Override
    public RecipeOrderDocument getRecipeOrderDocumentById(String id) {
        return recipeServiceImpl.getRecipeOrderDocumentById(id);
    }

    @Override
    public PayOrderReq convertAppInfo2PayOrder(Order order, Object infoObj) {
        LOGGER.debug("将订单转换成缴费参数对象传递给HIS,订单号:" + order.getOrderNo());
        PayOrderReq payOrderReq = null;
        int fee = 0;
        try {
            RecipeOrderDocument recipeOrder = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
            String hosId = CenterFunctionUtils.getHosId();
            RecipePaidDetailDocument nonPaid = recipeOrder.getRecipeNonPaidDetail();
            BigDecimal totalFee = new BigDecimal(nonPaid.getAmount());
            payOrderReq = new PayOrderReq();
            payOrderReq.setHosId(hosId);
            payOrderReq.setHospClinicCode(recipeOrder.getClinicCode());
            payOrderReq.setHospSequence(nonPaid.getHospSequence());
            fee = totalFee.intValue();
            if (infoObj instanceof AliEntity) {
                AliEntity aliEntity = (AliEntity) infoObj;
                payOrderReq.setOrderId(aliEntity.getOutTradeNo());
                payOrderReq.setSerialNum(aliEntity.getTradeNo());
                payOrderReq.setPayDate(aliEntity.getNotifyTime().split(" ")[0]);
                payOrderReq.setPayTime(aliEntity.getNotifyTime().split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.ALIPAY);
                payOrderReq.setPayResCode(aliEntity.getTradeStatus());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(aliEntity.getBuyerId());
            } else if (infoObj instanceof AliPayTradeQueryRes) {
                AliPayTradeQueryRes aliPayQuery = (AliPayTradeQueryRes) infoObj;
                payOrderReq.setOrderId(aliPayQuery.getOutTradeNo());
                payOrderReq.setSerialNum(aliPayQuery.getTradeNo());
                payOrderReq.setPayDate(aliPayQuery.getSendPayDate().split(" ")[0]);
                payOrderReq.setPayTime(aliPayQuery.getSendPayDate().split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.ALIPAY);
                payOrderReq.setPayResCode(aliPayQuery.getTradeStatus());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(aliPayQuery.getBuyerLogonId());
            } else if (infoObj instanceof WeixinEntity) {
                WeixinEntity weixinEntity = (WeixinEntity) infoObj;
                payOrderReq.setOrderId(weixinEntity.getOutTradeNo());
                payOrderReq.setSerialNum(weixinEntity.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinEntity.getTimeEnd(), "yyyyMMddHHmmss");
                payOrderReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payOrderReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.WECHATPAY);
                payOrderReq.setPayResCode(weixinEntity.getResultCode());
                payOrderReq.setMerchantId(weixinEntity.getMchId());
                payOrderReq.setTerminalId(weixinEntity.getDeviceInfo());
                payOrderReq.setPayAccount(weixinEntity.getAppid());
            } else if (infoObj instanceof WeixinPayQueryRes) {
                WeixinPayQueryRes weixinPayQuery = (WeixinPayQueryRes) infoObj;
                payOrderReq.setOrderId(weixinPayQuery.getOutTradeNo());
                payOrderReq.setSerialNum(weixinPayQuery.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinPayQuery.getTimeEnd(), "yyyyMMddHHmmss");
                payOrderReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payOrderReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payOrderReq.setPayChannelId(PayChannel.WECHATPAY);
                payOrderReq.setPayResCode(weixinPayQuery.getResultCode());
                payOrderReq.setMerchantId(weixinPayQuery.getMchId());
                payOrderReq.setTerminalId(weixinPayQuery.getDeviceInfo());
                payOrderReq.setPayAccount(weixinPayQuery.getAppid());
            } else if (infoObj instanceof CmbPayEntity) {
                CmbPayEntity cmbEntity = (CmbPayEntity) infoObj;
                payOrderReq.setOrderId(order.getOrderNo());
                // 支付信息
                String account = cmbEntity.getMsg();
                payOrderReq.setSerialNum(account.substring(account.length() - 20, account.length()));
                // 交易日期
                String date = DateUtil.toString(DateUtil.toDate(cmbEntity.getDate(), "yyyyMMdd"), "yyyy-MM-dd");

                payOrderReq.setPayDate(date);
                payOrderReq.setPayTime(cmbEntity.getTime());
                payOrderReq.setPayChannelId(PayChannel.WEB_UNION);
                payOrderReq.setPayResCode(cmbEntity.getSucceed());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(cmbEntity.getBillNo());
            } else if (infoObj instanceof QuerySingleOrderRes) {
                QuerySingleOrderRes cmbPayQuery = (QuerySingleOrderRes) infoObj;
                payOrderReq.setOrderId(order.getOrderNo());
                payOrderReq.setSerialNum(cmbPayQuery.getBody().getBankSeqNo());
                // 日期
                String date = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptDate(), "yyyyMMdd"), "yyyy-MM-dd");
                // 时间
                String time = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptTime(), "HHmmss"), "HH:mm:ss");
                payOrderReq.setPayDate(date);
                payOrderReq.setPayTime(time);
                payOrderReq.setPayChannelId(PayChannel.WEB_UNION);
                payOrderReq.setPayResCode(cmbPayQuery.getHead().getCode());
                payOrderReq.setMerchantId("");
                payOrderReq.setTerminalId("");
                payOrderReq.setPayAccount(cmbPayQuery.getBody().getBillNo());
            }
            payOrderReq.setBankNo("");
            payOrderReq.setPayResDesc("");
            payOrderReq.setPayTotalFee(fee);
            payOrderReq.setPayBehooveFee(fee);
            payOrderReq.setPayActualFee(fee);
            payOrderReq.setPayMiFee(0);
            payOrderReq.setOperatorId("");
            payOrderReq.setReceiptId("");
            // } catch (RuntimeException e1) {
            // LOGGER.debug("RecipeServiceNotxImpl.convertAppInfo2PayOrder[Exception]:", e1);
            // LOGGER.debug("将订单转换成缴费参数对象异常,订单号:"+order.getOrderNo());
            // //throw e1;
            // payOrderReq = null;
        } catch (Exception e) {
            LOGGER.debug("将订单转换成缴费参数对象异常,订单号:" + order.getOrderNo(), e);
            // throw e;
            payOrderReq = null;
        }
        return payOrderReq;
    }

    @Override
    public ResModel<PayList> findPayListModel(BasicInfoDocument basic, String clinicCode, String payStatus,
            String sDate, String eDate, boolean userCache) throws Exception {
        PayListReq listReq = recipeServiceImpl.getPayListReq(basic, clinicCode, payStatus, sDate, eDate);
        if (userCache) {
            return webService4HisInterfaceCacheUtil.getCachePayListRes(listReq);
        }
        return webServicesClient.getPayDetailAll(listReq);
    }

    @Override
    public synchronized void saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument recipe, RefundByHis refund)
            throws Exception {
        recipe = recipeServiceImpl.getRecipeOrderDocumentById(recipe.getId());
        if (recipe != null) {
            boolean canRefundFlag = true;
            Map<String, RecipeRefundDetailDocument> refundMap = recipe.getRecipeRefundDetailDocumentMap();
            RecipeRefundDetailDocument detail = null;
            for (Map.Entry<String, RecipeRefundDetailDocument> stringRecipeRefundDetailDocumentEntry : refundMap
                    .entrySet()) {
                if (stringRecipeRefundDetailDocumentEntry.getKey().split("_")[0].equals(refund.getId())) {
                    LOGGER.debug("线下退费已经将此记录退回,不再重复退费,退费Id:"+refund.getId());
                    canRefundFlag = false;
                    break;
                }
                detail =  stringRecipeRefundDetailDocumentEntry.getValue();
                if (detail != null) {
                    if (StringUtil.isEmpty(detail.getRecipeNo()) || StringUtil.isEmpty(detail.getSequenceNo())) {
                        continue;
                    }
                    if (detail.getRecipeNo().equals(refund.getRecipeNo())
                            && detail.getSequenceNo().equals(refund.getSequenceNo())) {
                        LOGGER.debug("线下退费已经将此处方号下的序号进行了退费,不再重复退费,退费Id:" + refund.getId() + ",处方号:"
                                + refund.getRecipeNo() + ",序号:" + refund.getSequenceNo());
                        canRefundFlag = false;
                        break;
                    }
                }
            }
            if (canRefundFlag) {
                String refunReturnMsg = "";
                LOGGER.debug("根据退款记录准备获得已缴费的对象,退款Id:" + refund.getId());
                RecipePaidDetailDocument recipePaidOrder = getRecipePaidOrder(recipe, refund);
                if (recipePaidOrder != null) {
                    LOGGER.debug("获得已缴费对象成功,订单号:" + recipePaidOrder.getOrderNum() + ",支付平台:"
                            + recipePaidOrder.getPayChannelId());
                    String refundNo = checkRecipeAndRefundIsEqual(recipe, recipePaidOrder, refund);
                    if (StringUtil.isNotEmpty(refundNo)) {
                        if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                            refunReturnMsg = aliPayRefund(refund, recipePaidOrder, refundNo);
                        } else if (recipePaidOrder.getPayChannelId()
                                .equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                            refunReturnMsg = weixinPayRefund(refund, recipePaidOrder, refundNo);
                        } else if (recipePaidOrder.getPayChannelId()
                                .equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                            refunReturnMsg = cmbPayRefund(refund, recipePaidOrder, refundNo);
                        }
                        saveRecipeRefundResult(recipe, refund, refundMap, refundNo, refunReturnMsg);
                    }
                }
            }
        } else {
            LOGGER.debug("平台退费Id:" + refund.getId() + ",门诊流水号:" + refund.getClinicCode() + ",处方号:"
                    + refund.getRecipeNo() + ",未找到缴费单");
        }
    }

    private void saveRecipeRefundResult(RecipeOrderDocument recipe, RefundByHis refund,
            Map<String, RecipeRefundDetailDocument> refundMap, String refundNo, String refunReturnMsg)
            throws Exception {
        RecipeRefundDetailDocument refundDetail = new RecipeRefundDetailDocument();
        BeanUtils.copyProperties(refund, refundDetail);
        refundDetail.setClinicCode(recipe.getClinicCode());
        refundDetail.setRefundNo(refundNo);
        refundDetail.setRefundReturnMsg(refunReturnMsg);
        refundMap.put(refund.getId() + "_" + DateUtil.toString(new Date(), PEPConstants.DEFAULT_DATETIME_FORMAT),
                refundDetail);
        recipe.setRecipeRefundDetailDocumentMap(refundMap);
        recipeServiceImpl.saveRecipeOrderDocument(recipe);
        if (refunReturnMsg.equalsIgnoreCase("Success")) {
            sendRecipeRefundMsg(recipe.getId(), refundDetail, SendPushMsgEnum.RECIPE_REFUND_SUCCESS);
        } else {
            sendRecipeRefundMsg(recipe.getId(), refundDetail, SendPushMsgEnum.RECIPE_REFUND_FAIL);
        }
    }

    private String weixinPayRefund(RefundByHis refund, RecipePaidDetailDocument recipePaidOrder, String refundNo)
            throws Exception {
        WeixinRefundReq weixinReq = new WeixinRefundReq();
        weixinReq.setOutRefundNo(refundNo);
        weixinReq.setRefundFee(Math.abs(Integer.parseInt(refund.getCost())));
        weixinReq.setTotalFee(Integer.parseInt(recipePaidOrder.getAmount()));
        weixinReq.setOutTradeNo(recipePaidOrder.getOrderNum());
        LOGGER.debug("线下退费,请求微信参数:" + JSONUtil.toJSON(weixinReq));
        PayResultRes resultRes = weixinService.saveWeixinRefund(weixinReq);
        String refundReturnMsg = resultRes.getResultMsg();
        if (resultRes.getResultCode().equals("0")) {
            refundReturnMsg = "SUCCESS";
        } else {
            refundReturnMsg = resultRes.getResultMsg();
        }
        return refundReturnMsg;
    }

    private String aliPayRefund(RefundByHis refund, RecipePaidDetailDocument recipePaidOrder, String refundNo) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(refund.getCost()));
        bigDecimal = bigDecimal.divide(new BigDecimal("100"));
        AliRefundRes refundRes = aliService.saveAliRefundResProcess(recipePaidOrder.getOrderNum(), refundNo,
                bigDecimal.abs().toString(), null);
        String refundReturnMsg = "";
        if (refundRes != null) {
            refundReturnMsg = refundRes.getMsg();
        }
        return refundReturnMsg;
    }

    private String cmbPayRefund(RefundByHis refund, RecipePaidDetailDocument recipePaidOrder, String refundNo) throws Exception {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(refund.getCost()));
        bigDecimal = bigDecimal.divide(new BigDecimal("100"));
        // 生成一网通退款请求对象
        RefundNoDupBodyReq refundInfo = new RefundNoDupBodyReq();
        CmbPayEntity cmbInfo = cmbService.getQueryInfo(recipePaidOrder.getOrderNum());
        // 原订单号
        refundInfo.setBillNo(cmbInfo.getBillNo());
        // 交易日期
        refundInfo.setDate(cmbInfo.getDate());
        // 退款流水号
        refundInfo.setRefundNo(refundNo);
        // 退款金额
        refundInfo.setAmount(bigDecimal.toString());
        RefundNoDupRes refundRes = cmbService.saveRefundResult(refundInfo);
        String refundReturnMsg = "";
        if (refundRes != null) {
            if(StringUtil.isNull(refundRes.getHead().getCode())) {
                refundReturnMsg = "Success";
            } else {
                refundReturnMsg = refundRes.getHead().getErrMsg();
            }
        }
        return refundReturnMsg;
    }

    private RecipePaidDetailDocument getRecipePaidOrder(RecipeOrderDocument recipe, RefundByHis refund) {
        RecipePaidDetailDocument recipePaidOrder = null;
        List<RecipeDetailAllDocument> detailList = null;
        List<RecipePaidDetailDocument> paidList = recipe.getRecipePaidDetailList();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidList) {
            detailList = recipePaidDetailDocument.getDetailList();
            for (RecipeDetailAllDocument recipeDetailAllDocument : detailList) {
                if (recipeDetailAllDocument.getRecipeNo().equals(refund.getRecipeNo())) {
                    recipePaidOrder = recipePaidDetailDocument;
                    break;
                }
            }
        }
        return recipePaidOrder;
    }

    /**
     * 支付平台某个订单的退款总数小于等于HIS的退款总数
     *
     * @param recipe
     * @param recipePaidOrder
     * @param refund
     * @return
     * @throws Exception
     */
    private String checkRecipeAndRefundIsEqual(RecipeOrderDocument recipe, RecipePaidDetailDocument recipePaidOrder,
            RefundByHis refund) throws Exception {
        BasicInfoDocument basic = userInfoService.getFamilyMemberByUserIdAndMemberId(recipe.getCreateUserId(),
                recipe.getPatientId());
        ResModel<PayList> refundPayRes = this.findPayListModel(basic, recipe.getClinicCode(), String.valueOf(2), null,
                null, false);
        List<Pay> refundPayList = refundPayRes.getRes().getPayList();
        LOGGER.debug("HIS查询记录的退费集合数:" + refundPayList.size() + ",退款Id:" + refund.getId() + ",订单号:"
                + recipePaidOrder.getOrderNum() + ",门诊流水号:" + recipe.getClinicCode());
        BigDecimal hisRefundBig = new BigDecimal("0");
        for (Pay pay : refundPayList) {
            LOGGER.debug("HIS查询记录的退费项目:" + pay.getItemCode() + ",退费金额" + pay.getOwnCost());
            hisRefundBig = hisRefundBig.add(new BigDecimal(pay.getOwnCost()).abs());
        }
        BigDecimal finishBig = new BigDecimal("0");
        // 支付宝以及微信
        DecimalFormat df = new DecimalFormat("000");
        // 一网通因为只允许20为退款流水号,所以format为00
        DecimalFormat dfCmb = new DecimalFormat("00");
        String refundNo = null;
        if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                refundNo = recipePaidOrder.getOrderNum() + df.format(tempIndex);
                AliRefundTradeQueryRes refundQuery = aliService.getAliRefundTradeQueryRes(recipePaidOrder.getOrderNum(),
                        refundNo);
                if (refundQuery != null) {
                    if (refundQuery.getCode().equals("10000")) {
                        if (StringUtil.isNotEmpty(refundQuery.getRefundAmount())) {
                            finishBig = finishBig
                                    .add(new BigDecimal(refundQuery.getRefundAmount()).multiply(new BigDecimal("100")));
                        } else {
                            break;
                        }
                    } else {
                        LOGGER.debug("支付宝缴费查询失败,订单号" + recipePaidOrder.getOrderNum() + ",退费单号:" + refundNo + ",失败消息:"
                                + refundQuery.getMsg());
                        return null;
                    }
                } else {
                    LOGGER.debug("支付宝缴费查询失败,订单号" + recipePaidOrder.getOrderNum() + ",退费单号:" + refundNo + ",查询返回值为空");
                    return null;
                }
            }
        } else if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                refundNo = recipePaidOrder.getOrderNum() + df.format(tempIndex);
                ResponseEntity<byte[]> responseEntity = weixinService.getWeixinResponseEntity(refundNo);
                if (responseEntity != null) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(new ByteArrayInputStream(responseEntity.getBody()));
                    Element rootElement = document.getRootElement();
                    if (rootElement.element("return_code").getData().equals("SUCCESS")) {
                        Element feeEle = rootElement.element("refund_fee_0");
                        if (feeEle != null) {
                            finishBig = finishBig.add(new BigDecimal((String) feeEle.getData()));
                        } else {
                            break;
                        }
                    } else {
                        LOGGER.debug(
                                "微信缴费查询失败,订单号" + recipePaidOrder.getOrderNum() + ",退费单号:" + refundNo + ",未找到退费的标签");
                        return null;
                    }
                } else {
                    LOGGER.debug("微信缴费查询失败,订单号" + recipePaidOrder.getOrderNum() + ",退费单号:" + refundNo + ",查询返回值为空");
                    return null;
                }
            }
        } else if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                // 退款信息查询
                String orderNo = recipePaidOrder.getOrderNum();
                CmbQueryRefundEntity queryRefundInfo = new CmbQueryRefundEntity();
                CmbPayEntity cmbInfo = cmbService.getQueryInfo(orderNo);
                // 订单号
                queryRefundInfo.setBillNo(cmbInfo.getBillNo());
                // 交易日期
                queryRefundInfo.setDate(cmbInfo.getDate());
                // 退款流水号
                refundNo = cmbInfo.getDate().concat(cmbInfo.getBillNo()).concat(dfCmb.format(tempIndex));
                queryRefundInfo.setRefundNo(refundNo);
                QueryRefundRes refundQuery = cmbService.queryRefundResult(queryRefundInfo);
                if (refundQuery != null) {
                    // 如果查询有该订单的退款信息
                    if(StringUtil.isEmpty(refundQuery.getHead().getCode())) {
                        BillRecordRes billRecord = refundQuery.getBody().getBillRecord().get(0);
                        // 如果订单已经直接退款成功
                        if (billRecord.getBillState().equals("210")) {
                            if (StringUtil.isNotEmpty(billRecord.getAmount())) {
                                finishBig = finishBig
                                        .add(new BigDecimal(billRecord.getAmount()).multiply(new BigDecimal("100")));
                            } else {
                                break;
                            }
                        } else {
                            LOGGER.debug("一网通缴费查询失败,订单号" + recipePaidOrder.getOrderNum() + ",退费单号:" + refundNo + ",失败消息:"
                                    + refundQuery.getHead().getErrMsg());
                            return null;
                        }
                    } else {
                        LOGGER.debug("一网通退款查询失败,无此退款订单号");
                        break;
                    }
                } else {
                    LOGGER.debug("一网通缴费查询失败,订单号" + recipePaidOrder.getOrderNum() + ",退费单号:" + refundNo + ",查询返回值为空");
                    return null;
                }
            }
        }
        LOGGER.debug("查询支付平台结束,获得对应的退款钱数:" + finishBig.toString());
        if (finishBig.add(new BigDecimal(refund.getCost()).abs()).compareTo(hisRefundBig) > 0) {
            LOGGER.debug("已退金额:" + finishBig.intValue() + ",待退金额:" + new BigDecimal(refund.getCost()).abs().intValue()
                    + ",退款总额:" + hisRefundBig.intValue());
            LOGGER.debug("平台退费Id:" + refund.getId() + ",门诊流水号:" + refund.getClinicCode() + ",处方号:"
                    + refund.getRecipeNo() + ",退款金额大于总退款金额");
            return null;
        } else {
            LOGGER.debug("退费金额校验成功后,返回的支付平台的退款单号:" + refundNo);
            return refundNo;
        }
    }

    public void sendRecipeRefundMsg(String registrationId, RecipeRefundDetailDocument refundDetail,
            SendPushMsgEnum pushMsgType) throws Exception {
        RecipeOrderDocument updateReg = this.getRecipeOrderDocumentById(registrationId);
        /*---------成功挂号提示-------*/
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushMsgType, refundDetail));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(updateReg.getCreateUserId());
        regMsg.setUserName(updateReg.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }

    @Override
    public RecipeOrderDocument getRecipeOrderDocumentByClinicCode(String clinicCode) {
        return recipeOrderRepository.getByClinicCode(clinicCode);
    }

    @Override
    public boolean checkRecipeAmount(String orderNum, String orderAmount, PayChannel payWay) throws Exception {
        boolean flag = false;
        User user = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(orderNum);
        if (order != null) {
            RecipeOrderDocument recipe = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
            if (recipe != null) {
                LOGGER.debug("预支付前校验,找到订单和缴费单,门诊流水号:" + recipe.getClinicCode());
                flag = getFlagByRecipeAmount(orderAmount, payWay, user, recipe);
                if (flag) {
                    recipe.getRecipeNonPaidDetail().setPayChannelId(String.valueOf(payWay.getCode()));
                    recipeServiceImpl.saveRecipeOrderDocument(recipe);
                    order.setPayWay(String.valueOf(payWay.getCode()));
                    orderService.save(order);
                }else{
                    LOGGER.debug("预支付前校验金额是否一致,返回否,门诊流水号:" + recipe.getClinicCode());
                }
                recipe = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                if(StringUtil.isEmpty(recipe.getRecipeNonPaidDetail().getPayChannelId())){
                    flag = false;
                }
            }else{
                LOGGER.debug("预支付前校验,根据订单未找到对应的缴费单,订单号:" + orderNum);
            }
        }else{
            LOGGER.debug("预支付前校验,未查到订单号对应的订单,订单号:" + orderNum);
        }
        return flag;
    }

    /**
     * 校验缴费与支付是否相等
     *
     * @param orderAmount
     * @param payWay
     * @param user
     * @param recipe
     * @return
     * @throws Exception
     */
    private boolean getFlagByRecipeAmount(String orderAmount, PayChannel payWay, User user, RecipeOrderDocument recipe)
            throws Exception {
        boolean flag = false;
        BasicInfoDocument basic = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(),
                recipe.getPatientId());
        ResModel<PayList> payList = this.findPayListModel(basic, recipe.getClinicCode(), String.valueOf(0), null, null,
                false);
        if (payList.getReturnCode() == ReturnCode.SUCCESS) {
            if (payList.getRes().getPayList() != null) {
                List<Pay> list = payList.getRes().getPayList();
                BigDecimal total = new BigDecimal("0");
                for (Pay pay : list) {
                    if (pay.getClinicCode().equals(recipe.getClinicCode())) {
                        total = total.add(new BigDecimal(pay.getOwnCost()));
                    }
                }
                // if (payWay == PayChannel.ALIPAY) {
                // orderAmount = (new BigDecimal(orderAmount).multiply(new
                // BigDecimal("100"))).toString();
                // }

                if (total.compareTo(new BigDecimal(recipe.getRecipeNonPaidDetail().getAmount())) == 0
                        && total.compareTo(new BigDecimal(orderAmount)) == 0) {
                    flag = true;
                }
                LOGGER.debug("预支付前对金额金额进行校验,门诊流水号:" + recipe.getClinicCode() + ",支付金额:" + orderAmount + ",HIS端待缴金额:"
                        + total.toString());
//                if (!flag) {
//                    LOGGER.debug("待缴金额与支付金额不一致,门诊流水号:" + recipe.getClinicCode() + ",支付金额:" + orderAmount + ",HIS端待缴金额:"
//                            + total.toString());
//                }
            }
        }else{
            LOGGER.debug("预支付前校验,获得HIS的待缴费接口返回值有问题,返回消息:" + payList.getReturnMsg());
        }
        return flag;
    }

    @Override
    public List<RecipeOrderDocument> findPatientRecipeOrderList(String patientId) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(patientId).and("createTime")
                .gte(DateUtil.toTimestamp(cal.getTime(), true)));
        return mongoTemplate.find(query, RecipeOrderDocument.class);
    }

    /**
     * 检验是否已经支付
     *
     * @param recipeOrderDocument
     * @throws Exception
     */
    @Override
    public void checkRecipeOrderIsPay(RecipeOrderDocument recipeOrderDocument) throws Exception {
        // PayOrderReq payOrderReq = null;
        String orderNum = recipeOrderDocument.getRecipeNonPaidDetail().getOrderNum();
        boolean canUpdate = false;
        try {
            canUpdate = getPayPlatformRecordFlag(orderNum);
        } catch (Exception e) {
            LOGGER.debug("查询支付平台异步回调的保存记录异常,订单号:" + orderNum, e);
        }
        if (canUpdate) {
            Order order = orderService.findByOrderNo(orderNum);
            String payWay = recipeOrderDocument.getRecipeNonPaidDetail().getPayChannelId();
            if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                AliPayTradeQueryRes queryRes = aliService
                        .getAliPayTradeQueryRes(recipeOrderDocument.getRecipeNonPaidDetail().getOrderNum());
                if (queryRes != null && queryRes.getCode().equals("10000")
                        && queryRes.getTradeStatus().equals("TRADE_SUCCESS")) {
                    // payOrderReq = this.convertAppInfo2PayOrder(order,
                    // queryRes);
                    order = this.saveUpdateRecipeAndOrder(order.getOrderNo(), payWay, queryRes);
                }
            } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                WeixinPayQueryRes weixinPayQueryRes = weixinService.getWeixinPayQueryRes(orderNum);
                if (weixinPayQueryRes != null && weixinPayQueryRes.getTradeState().equals("SUCCESS")) {
                    // payOrderReq = this.convertAppInfo2PayOrder(order,
                    // weixinPayQueryRes);
                    order = this.saveUpdateRecipeAndOrder(order.getOrderNo(), payWay, weixinPayQueryRes);

                }
            } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                // 一网通
                QuerySingleOrderRes cmbPayQueryRes = cmbService.getCmbPayQueryRes(orderNum);
                if (cmbPayQueryRes != null && cmbPayQueryRes.getBody().getStatus().equals("0")) {
                    order = this.saveUpdateRecipeAndOrder(order.getOrderNo(), payWay, cmbPayQueryRes);
                }
            }
            if (order != null) {
                orderService.save(order);
            }
        }
        // if (payOrderReq != null) {
        // order = this.saveUpdateRecipeAndOrder(order.getOrderNo(), payWay,
        // payOrderReq);
        // if (order != null) {
        // orderService.save(order);
        // }
        // }
    }




    /**
     * 检查是否已经保存异步消息
     *
     * @param orderNum
     * @return
     * @throws Exception
     */
    private boolean getPayPlatformRecordFlag(String orderNum) throws Exception {
        boolean canUpdate = false;
        Order order = orderService.findByOrderNo(orderNum);
        String payWay = order.getPayWay();
        if (StringUtil.isNotEmpty(payWay)) {
            if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                Ali ali = aliService.findByOutTradeNo(orderNum);
                if (ali != null) {
                    canUpdate = true;
                }
            } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                Weixin weixin = weixinService.findByOutTradeNo(orderNum);
                if (weixin != null) {
                    canUpdate = true;
                }
            } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                CmbPayEntity queryPanInfo = cmbService.getQueryInfo(orderNum);
                if (queryPanInfo != null) {
                    canUpdate = true;
                }
            }
        }
        return canUpdate;
    }

    /**
     * 付款成功后,发送挂号成功短信
     *
     * @param registrationId
     * @throws Exception
     */
    public void sendRecipeSuccessMsg(String registrationId) throws Exception {
        RecipeOrderDocument updateReg = this.getRecipeOrderDocumentById(registrationId.split("_")[0]);
        BasicInfoDocument info = userInfoService.getFamilyMemberByUserIdAndMemberId(updateReg.getCreateUserId(),
                updateReg.getPatientId());
        if (info != null) {
            updateReg.setPatientName(info.getName());
        }
        /*---------成功挂号提示-------*/
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(SendPushMsgEnum.RECIPE_PAY_SUCCESS, updateReg));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(updateReg.getCreateUserId());
        regMsg.setUserName(updateReg.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }

}
