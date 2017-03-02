package com.proper.enterprise.isj.proxy.service.notx;

import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
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
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.model.RefundReq;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;
import com.proper.enterprise.platform.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.platform.pay.cmb.model.CmbBillRecordRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQueryRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.cmb.service.CmbPayService;
import com.proper.enterprise.platform.pay.wechat.entity.WechatEntity;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundQueryRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;
import com.proper.enterprise.platform.pay.wechat.service.WechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

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
    WechatPayService wechatPayService;

    @Autowired
    AliPayService aliPayService;

    @Autowired
    CmbPayService cmbPayService;

    @Autowired
    PayFactory payFactory;

    @Autowired
    MessagesService messagesService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    /**
     * 通过ID获取缴费订单信息.
     *
     * @param id 缴费订单ID.
     * @return 缴费对象.
     */
    @Override
    public RecipeOrderDocument getRecipeOrderDocumentById(String id) {
        return recipeServiceImpl.getRecipeOrderDocumentById(id);
    }

    /**
     * 通过门诊流水号获取缴费信息.
     *
     * @param clinicCode 门诊流水号.
     * @return 缴费对象.
     */
    @Override
    public RecipeOrderDocument getRecipeOrderDocumentByClinicCode(String clinicCode) {
        return recipeOrderRepository.getByClinicCode(clinicCode);
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
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushMsgType, pushObj));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(recipeInfo.getCreateUserId());
        regMsg.setUserName(recipeInfo.getOperatorPhone());
        LOGGER.debug(JSONUtil.toJSON(regMsg));
        messagesService.saveMessage(regMsg);
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
        DecimalFormat df = new DecimalFormat("0.00");
        List<RecipeDocument> recipeList = new ArrayList<>();
        try {
            // 查询用户全部缴费信息
            ResModel<PayList> payListRes = this.findPayListModel(basic, null, payStatus, sDate, eDate, true);
            ResModel<PayList> refundPayListRes = null;
            // 查询退款的缴费信息
            if (StringUtil.isNull(payStatus) || payStatus.equals("1")) {
                refundPayListRes = this.findPayListModel(basic, null, "2", sDate, eDate, true);
            }
            Map<String, RecipeDetailDocument> detailMap = new LinkedHashMap<>();
            Map<String, RecipeDocument> recipeMap = new LinkedHashMap<>();
            RecipeDetailDocument detail;
            List<RecipeDetailItemDocument> itemList;
            RecipeDetailItemDocument item;
            BigDecimal totalBig;
            // 取得查询结果
            if (payListRes.getReturnCode() == ReturnCode.SUCCESS) {
                List<Pay> payList = new ArrayList<>();
                payList.addAll(payListRes.getRes().getPayList());
                // 获取退款列表
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
                // 定义返回给App的对象
                RecipeDocument recipe;
                // 详细列表
                List<RecipeDetailDocument> detailList;
                String recipeKey;
                for (Pay pay : payList) {
                    // 以门诊流水号为对象
                    recipe = recipeMap.get(pay.getClinicCode());
                    if (recipe == null) {
                        recipe = new RecipeDocument();
                        // 门诊流水号作为键值
                        recipeMap.put(pay.getClinicCode(), recipe);
                    }
                    recipe.setOutpatientNum(pay.getClinicCode());
                    recipe.setOutpatientDate(
                            DateUtil.toString(DateUtil.toDate(pay.getRegDate().split(" ")[0]), "yyyy年MM月dd日"));
                    detailList = recipe.getRecipes();
                    // 以单条项目总价是否为负数作为区分,以处方号以及执行科室名称作为键值
                    if (pay.getOwnCost().contains("-")) {
                        recipeKey = pay.getRecipeNo().concat("_").concat(pay.getExecDpnm()).concat("-");
                    } else {
                        recipeKey = pay.getRecipeNo().concat("_").concat(pay.getExecDpnm());
                    }
                    detail = detailMap.get(recipeKey);
                    // 设置处方详细信息
                    if (detail == null) {
                        detail = new RecipeDetailDocument();
                        detail.setDept(pay.getExecDpnm());
                        detail.setRecipeNum(pay.getRecipeNo());
                        detail.setLocation("");
                        if (pay.getOwnCost().contains("-")) {
                            detail.setStatusCode("2");

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
                    item.setAmount(df.format(new BigDecimal(String.valueOf(pay.getUnitPrice()))
                            .divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY)));
                    itemList.add(item);
                    if (StringUtil.isEmpty(detail.getTotal())) {
                        totalBig = new BigDecimal("0");
                    } else {
                        totalBig = new BigDecimal(detail.getTotal());
                    }
                    totalBig = totalBig.add(new BigDecimal(pay.getOwnCost()).divide(new BigDecimal("100"), 2,
                            RoundingMode.UNNECESSARY));
                    detail.setTotal(totalBig.toString());
                }
                // 拼接返回对象
                for (RecipeDocument recipeDocument : recipeMap.values()) {
                    recipeList.add(recipeDocument);
                }
            } else {
                // HIS返回错误信息
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
        RecipeOrderDocument recipeOrder;
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
            LOGGER.debug("保存缴费订单信息出现异常,门诊流水号:{}{}", clinicCode, e);
            throw e;
        }
        return recipeOrder;
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
    public Order saveUpdateRecipeAndOrder(String orderNo, String channelId, Object infoObj) throws Exception {
        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            LOGGER.debug("未查到缴费订单,调用缴费前校验,订单号:{}", orderNo);
            return null;
        }
        RecipeOrderDocument regBack = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
        if (regBack == null) {
            LOGGER.debug("未查到缴费单,调用缴费前校验,订单号:{}", orderNo);
            return null;
        }
        BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(regBack.getCreateUserId(),
                regBack.getPatientId());
        if (basicInfo == null) {
            LOGGER.debug("未查到患者信息,调用缴费前校验,门诊流水号:{}", regBack.getClinicCode());
            return null;
        }
        if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
            LOGGER.debug("患者信息的病历号为空,不能进行调用,调用缴费前校验,门诊流水号:{},患者Id:", regBack.getClinicCode(), basicInfo.getId());
            return null;
        }
        // 手动清空患者缴费缓存
        for (QueryType queryType : QueryType.values()) {
            webService4HisInterfaceCacheUtil.evictCachePayListRes(regBack.getPatientId(), queryType.name(),
                    basicInfo.getMedicalNum());
        }
        Map<String, String> requestOrderNoMap = getRecipeRequestOrderNoMap(regBack);
        if (requestOrderNoMap.containsKey(order.getOrderNo())) {
            LOGGER.debug("订单号重复调用缴费接口,直接返回,不对HIS接口进行调用,门诊流水号:{}", regBack.getClinicCode());
            return null;
        }
        try {
            PayOrderReq payOrderReq = this.convertAppInfo2PayOrder(order, infoObj);
            
            if (payOrderReq == null) {
                LOGGER.debug("诊间缴费请求参数转换异常,订单号:{}", order.getOrderNo());
                throw new RecipeException("诊间缴费请求参数转换异常,订单号:".concat(order.getOrderNo()));
            }
            RecipeOrderReqDocument payReq = new RecipeOrderReqDocument();
            BeanUtils.copyProperties(payOrderReq, payReq);
            Map<String, RecipeOrderReqDocument> reqMap = regBack.getRecipeOrderReqMap();
            reqMap.put(payOrderReq.getOrderId(), payReq);
            regBack.setRecipeOrderReqMap(reqMap);
            regBack = recipeServiceImpl.saveRecipeOrderDocument(regBack);
            order = this.updateRegistrationAndOrder(order, payOrderReq, regBack, channelId);
        } catch (DelayException e) {
            PayLogUtils.log(PayStepEnum.UNKNOWN, order, e.getPosition());
            Map<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("doc", regBack);
            tmp.put("order", order);
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_REFUND_FAIL_CAUSE_BY_NET, tmp);
        } catch (Exception e) {
            LOGGER.info("诊间缴费出现异常", e);
            String refundNo = order.getOrderNo() + "001";
            if (order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                refundNo = order.getOrderNo().substring(0, 18).concat("01");
            }
            RecipePaidDetailDocument detail = regBack.getRecipeNonPaidDetail();
            if (detail == null) {
                detail = new RecipePaidDetailDocument();
            }
            detail.setRefundNum(refundNo);
            LOGGER.debug("退款单号:{}{}", refundNo, e);
            if (StringUtil.isEmpty(detail.getDescription())) {
                detail.setDescription("");
            }
            LOGGER.debug("保存异常消息前,退款单号:{}", refundNo);
            if (StringUtil.isNotEmpty(e.getMessage())) {
                detail.setDescription(detail.getDescription().concat(",").concat(e.getMessage()));
            }
            LOGGER.debug("保存异常消息后,退款单号:{}", refundNo);
            requestOrderNoMap.put(order.getOrderNo(), String.valueOf(order.getPayWay()));
            LOGGER.debug("将订单号添加到计算平台缴费情况的Map中,退款单号:{},订单号:{}", refundNo, order.getOrderNo());
            if (!checkRecipeFailCanRefund(order, regBack, requestOrderNoMap, basicInfo, detail)) {
                LOGGER.debug("诊间缴费失败后,核对平台与HIS已缴金额不一致,不能进行退款,退款单号:{},订单号:{}", refundNo, order.getOrderNo());
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
                LOGGER.debug("诊间缴费HIS抛异常,调用支付平台退费失败,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
                detail.setDescription(detail.getDescription().concat(
                        ",诊间缴费HIS抛异常,调用支付平台退费失败,订单号:".concat(order.getOrderNo()).concat(",退费单号:").concat(refundNo)));
                regBack.getRecipePaidFailDetailList().add(detail);
                RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
                regBack.setRecipeNonPaidDetail(nonPaid);
                recipeServiceImpl.saveRecipeOrderDocument(regBack);
                sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            }
        }
        // 手动清空患者缴费缓存
        for (QueryType queryType : QueryType.values()) {
            webService4HisInterfaceCacheUtil.evictCachePayListRes(regBack.getPatientId(), queryType.name(),
                    basicInfo.getMedicalNum());
        }
        return order;
    }

    public static final int FUNC_RCPSVCIMPL_UPDATE_REG_AND_ODR = 0x00020000;
    public static final int FUNC_RCPSVCIMPL_UPDATE_REG_AND_ODR_ACCESS_HIS_ERROR = FUNC_RCPSVCIMPL_UPDATE_REG_AND_ODR
            | PayLogUtils.CAUSE_TYPE_EXCEPTION | 0x1;

    /**
     * 更新缴费及订单信息.
     *
     * @param order 订单信息.
     * @param payOrderReq 支付请求.
     * @param recipeOrder 缴费订单对象.
     * @param channelId 支付方式.
     * @return order 订单信息.
     * @throws Exception
     */
    private Order updateRegistrationAndOrder(Order order, PayOrderReq payOrderReq, RecipeOrderDocument recipeOrder,
            String channelId) throws Exception {
        ResModel<PayOrder> payOrderResModel;
        try {
            payOrderResModel = webServicesClient.payOrder(payOrderReq);
        } catch (InvocationTargetException ite) {
            if (ite.getCause() != null && ite.getCause() instanceof RemoteAccessException) {
                LOGGER.debug("缴费网络连接异常:{}", ite);
                throw new DelayException(FUNC_RCPSVCIMPL_UPDATE_REG_AND_ODR_ACCESS_HIS_ERROR);
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
        payHis.setClientReturnMsg(payOrderResModel.getReturnMsg().concat("(")
                .concat(String.valueOf(payOrderResModel.getReturnCode())).concat(")"));
        if (payOrderResModel.getReturnCode() == ReturnCode.SUCCESS) {
            BeanUtils.copyProperties(payOrderResModel.getRes(), payHis);
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeOrder.getRecipePaidDetailList().add(recipeOrder.getRecipeNonPaidDetail());
            recipeOrder.setRecipeNonPaidDetail(new RecipePaidDetailDocument());
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            order.setOrderStatus("2");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
            order.setPayWay(String.valueOf(channelId));
            RecipeOrderDocument recipeInfo = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
            BasicInfoDocument info = userInfoService.getFamilyMemberByUserIdAndMemberId(recipeInfo.getCreateUserId(),
                    recipeInfo.getPatientId());
            if (info != null) {
                recipeInfo.setPatientName(info.getName());
            }
            // 推送消息
            sendRecipeMsg(recipeInfo, SendPushMsgEnum.RECIPE_PAY_SUCCESS, recipeInfo);
        } else {
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            throw new HisReturnException(payOrderResModel.getReturnMsg());
        }
        return order;
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
    @SuppressWarnings("SameParameterValue")
    @Override
    public boolean refundMoney2User(Order order, String refundNo, boolean refundFlag) throws Exception {
        if (order.getPayWay().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            // 退款请求对象
            RefundReq refundInfo = new RefundReq();
            // 商户订单号
            refundInfo.setOutTradeNo(order.getOrderNo());
            // 退款流水号
            refundInfo.setOutRequestNo(refundNo);
            // 退款金额
            refundInfo.setRefundAmount(order.getOrderAmount());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes != null && refundRes.getCode().equals("10000")) {
                refundFlag = true;
            } else {
                LOGGER.debug("未查到需要退费的项目,或者退费接口返回异常,支付宝订单号:{}", order.getOrderNo());
            }
        } else if (order.getPayWay().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            // 请求退款对象
            RefundReq refundInfo = new RefundReq();
            // 商户订单号
            refundInfo.setOutTradeNo(order.getOrderNo());
            // 退款流水号
            refundInfo.setOutRequestNo(refundNo);
            // 退款金额
            refundInfo.setRefundAmount(order.getOrderAmount());
            // 退款总金额
            refundInfo.setTotalFee(order.getOrderAmount());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes.getResultCode().equals("0")) {
                refundFlag = true;
            } else {
                LOGGER.debug("未查到需要退费的项目,或者退费接口返回异常,微信订单号:{}", order.getOrderNo());
            }
            // 一网通缴费退款操作
        } else if (order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            // 请求退款对象
            RefundReq refundInfo = new RefundReq();
            // 商户订单号
            refundInfo.setOutTradeNo(order.getOrderNo());
            // 退款流水号
            refundInfo.setOutRequestNo(refundNo);
            // 退款金额
            refundInfo.setRefundAmount(order.getOrderAmount());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbRefundNoDupRes cmbRefundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (cmbRefundRes != null && StringUtil.isNull(cmbRefundRes.getHead().getCode())) {
                refundFlag = true;
            } else {
                LOGGER.debug("未查到需要退费的项目,或者退费接口返回异常,一网通订单号:{}", order.getOrderNo());
            }
        } else {
            LOGGER.debug("未查到订单号对应的支付平台,订单号:{}", order.getOrderNo());
        }
        return refundFlag;
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
        if (refundFlag) {
            LOGGER.debug("诊间缴费HIS抛异常,对缴费进行退费,退费成功,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
            order.setOrderStatus("3");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(channelId));
            // orderService.save(order);
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_FAIL, regBack);
            detail.setRefundStatus("1");
        } else {
            LOGGER.debug("诊间缴费HIS抛异常,对缴费进行退费,退费失败,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
            order.setOrderStatus("5");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(channelId));
            // orderService.save(order);
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            detail.setRefundStatus("0");
        }
        return order;
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
        if (regBack == null || StringUtil.isEmpty(regBack.getClinicCode())) {
            LOGGER.debug("缴费项目为空,或者是缴费流水号为空,订单号:{}", order.getOrderNo());
            return false;
        }
        ResModel<PayList> paidRes = this.findPayListModel(basicInfo, regBack.getClinicCode(), "1", null, null, false);
        List<Pay> refundPayList = new ArrayList<>();
        if (paidRes == null) {
            LOGGER.debug("查询已缴费信息返回值不能进行解析,门诊流水号:{},订单号:{}", regBack.getClinicCode(), order.getOrderNo());
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }
        if (paidRes.getReturnCode() == ReturnCode.EMPTY_RETURN) {
            LOGGER.debug("HIS未查到已缴费项目,HIS返回信息:{}", paidRes.getReturnMsg());
        } else if (paidRes.getReturnCode() == ReturnCode.SUCCESS) {
            refundPayList = paidRes.getRes().getPayList();
        } else {
            LOGGER.debug("HIS查询已缴费信息出现异常,HIS返回信息:{}", paidRes.getReturnMsg());
            detail.setDescription("门诊流水号:".concat(regBack.getClinicCode()).concat("HIS查询已缴费信息出现异常,不进行自动退费,HIS返回信息:")
                    .concat(paidRes.getReturnMsg()));
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeOrderRepository.save(regBack);
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }
        List<RecipePaidDetailDocument> paidSuccessList = regBack.getRecipePaidDetailList();
        Set<String> seqSet = new HashSet<>();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidSuccessList) {
            LOGGER.debug("HIS查询记录的已缴项目的序号:{}", recipePaidDetailDocument.getHospSequence());
            seqSet.add(recipePaidDetailDocument.getHospSequence());
        }
        BigDecimal hisPaidBig = new BigDecimal("0");
        for (Pay pay : refundPayList) {
            if (!seqSet.contains(pay.getHospSequence())) {
                continue;
            }
            LOGGER.debug("HIS查询记录的已缴项目:{},缴费金额:{}", pay.getItemCode(), pay.getOwnCost());
            hisPaidBig = hisPaidBig.add(new BigDecimal(pay.getOwnCost()).abs());
        }
        BigDecimal queryBig = new BigDecimal("0");
        try {
            for (Map.Entry<String, String> paramEntry : requestOrderNoMap.entrySet()) {
                String orderNo = paramEntry.getKey();
                if (paramEntry.getValue().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                    PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                    AliPayTradeQueryRes query = payService.queryPay(orderNo);
                    if (query != null) {
                        queryBig = queryBig.add(new BigDecimal(query.getTotalAmount()).multiply(new BigDecimal("100")));
                    } else {
                        LOGGER.debug("未查到支付宝支付信息,订单号:{}", orderNo);
                    }
                    AliRefundTradeQueryRes refundQuery = payService.queryRefund(orderNo, orderNo.concat("001"));
                    if (refundQuery != null && StringUtil.isNotEmpty(refundQuery.getRefundAmount())) {
                        queryBig = queryBig.subtract(
                                new BigDecimal(refundQuery.getRefundAmount()).multiply(new BigDecimal("100")));
                    }
                } else if (paramEntry.getValue().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                    PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                    WechatPayQueryRes query = payService.queryPay(orderNo);
                    if (query != null) {
                        queryBig = queryBig.add(new BigDecimal(query.getTotalFee()));
                    } else {
                        LOGGER.debug("未查到微信支付信息,订单号:{}", orderNo);
                    }
                    WechatRefundQueryRes refundQuery = payService.queryRefund(orderNo, orderNo.concat("001"));
                    if (refundQuery != null && StringUtil.isNotEmpty(refundQuery.getRefundFee())) {
                        queryBig = queryBig.subtract(new BigDecimal(refundQuery.getRefundFee()));
                    }
                } else if (paramEntry.getValue().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                    // 查询缴费订单信息
                    PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                    CmbQuerySingleOrderRes query = payService.queryPay(orderNo);
                    // 查询支付成功
                    if (query != null && StringUtil.isEmpty(query.getHead().getCode())) {
                        queryBig = queryBig
                                .add(new BigDecimal(query.getBody().getBillAmount()).multiply(new BigDecimal("100")));
                    } else {
                        LOGGER.debug("未查到一网通支付信息,订单号:{}", orderNo);
                    }
                    // 退款信息查询
                    CmbQueryRefundRes refund = payService.queryRefund(orderNo, orderNo.concat("01"));
                    if (refund != null && refund.getBody().getBillRecord() != null
                            && StringUtil.isNotEmpty(refund.getBody().getBillRecord().get(0).getAmount())) {
                        queryBig = queryBig.subtract(new BigDecimal(refund.getBody().getBillRecord().get(0).getAmount())
                                .multiply(new BigDecimal("100")));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("计算支付平台已缴金额发生异常,门诊流水号:{},订单号:{}{}", regBack.getClinicCode(), order.getOrderNo(), e);
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }

        LOGGER.debug("支付平台结余金额:{},当前缴费金额:{},HIS已缴费金额:{}", queryBig, order.getOrderAmount(), hisPaidBig);
        detail.setDescription(
                detail.getDescription().concat(",支付平台结余金额:".concat(String.valueOf(queryBig)).concat(",当前缴费金额:")
                        .concat(order.getOrderAmount()).concat(",HIS已缴费金额:").concat(String.valueOf(hisPaidBig))));
        if (queryBig.subtract(new BigDecimal(order.getOrderAmount())).compareTo(hisPaidBig) != 0) {
            LOGGER.debug("缴费失败后,HIS已缴费金额与支付平台的金额不相等,不能进行退款,门诊流水号:{},订单号:{}", regBack.getClinicCode(),
                    order.getOrderNo());
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeOrderRepository.save(regBack);
            sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }
        return true;
    }

    /**
     * 获取已经缴费的Map信息.
     *
     * @param regBack 缴费信息.
     * @return 已经缴费的Map信息.
     */
    @Override
    public Map<String, String> getRecipeRequestOrderNoMap(RecipeOrderDocument regBack) {
        Map<String, String> requestOrderNoMap = new HashMap<>();
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

    /**
     * 将APP转化为向HIS请求对象.
     *
     * @param order 订单信息.
     * @param infoObj 支付对象.
     * @return 向HIS请求对象.
     */
    @Override
    public PayOrderReq convertAppInfo2PayOrder(Order order, Object infoObj) {
        LOGGER.debug("将订单转换成缴费参数对象传递给HIS,订单号:{}", order.getOrderNo());
        PayOrderReq payOrderReq;
        int fee;
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
            } else if (infoObj instanceof WechatEntity) {
                WechatEntity weixinEntity = (WechatEntity) infoObj;
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
            } else if (infoObj instanceof WechatPayQueryRes) {
                WechatPayQueryRes weixinPayQuery = (WechatPayQueryRes) infoObj;
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
            } else if (infoObj instanceof CmbQuerySingleOrderRes) {
                CmbQuerySingleOrderRes cmbPayQuery = (CmbQuerySingleOrderRes) infoObj;
                payOrderReq.setOrderId(order.getOrderNo());
                payOrderReq.setSerialNum(cmbPayQuery.getBody().getBankSeqNo());
                // 日期
                String date = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptDate(), "yyyyMMdd"),
                        "yyyy-MM-dd");
                // 时间
                String time = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptTime(), "HHmmss"),
                        "HH:mm:ss");
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
        } catch (Exception e) {
            LOGGER.debug("将订单转换成缴费参数对象异常,订单号:{}{}", order.getOrderNo(), e);
            payOrderReq = null;
        }
        return payOrderReq;
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
        PayListReq listReq = recipeServiceImpl.getPayListReq(basic, clinicCode, payStatus, sDate, eDate);
        if (userCache) {
            return webService4HisInterfaceCacheUtil.getCachePayListRes(listReq);
        }
        return webServicesClient.getPayDetailAll(listReq);
    }

    /**
     * (HIS线下退费)保存退款以及更新缴费信息.
     *
     * @param recipe 缴费对象.
     * @param refund 退款对象.
     * @throws Exception 异常.
     */
    @Override
    public synchronized void saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument recipe, RefundByHis refund)
            throws Exception {
        recipe = recipeServiceImpl.getRecipeOrderDocumentById(recipe.getId());
        if (recipe != null) {
            boolean canRefundFlag = true;
            Map<String, RecipeRefundDetailDocument> refundMap = recipe.getRecipeRefundDetailDocumentMap();
            RecipeRefundDetailDocument detail;
            for (Map.Entry<String, RecipeRefundDetailDocument> stringRecipeRefundDetailDocumentEntry : refundMap
                    .entrySet()) {
                if (stringRecipeRefundDetailDocumentEntry.getKey().split("_")[0].equals(refund.getId())) {
                    LOGGER.debug("线下退费已经将此记录退回,不再重复退费,退费Id:{}", refund.getId());
                    canRefundFlag = false;
                    break;
                }
                detail = stringRecipeRefundDetailDocumentEntry.getValue();
                if (detail != null) {
                    if (StringUtil.isEmpty(detail.getRecipeNo()) || StringUtil.isEmpty(detail.getSequenceNo())) {
                        continue;
                    }
                    if (detail.getRecipeNo().equals(refund.getRecipeNo())
                            && detail.getSequenceNo().equals(refund.getSequenceNo())) {
                        LOGGER.debug("线下退费已经将此处方号下的序号进行了退费,不再重复退费,退费Id:{},处方号:{},序号:{}", refund.getId(),
                                refund.getRecipeNo(), refund.getSequenceNo());
                        canRefundFlag = false;
                        break;
                    }
                }
            }
            if (canRefundFlag) {
                String refunReturnMsg = "";
                LOGGER.debug("根据退款记录准备获得已缴费的对象,退款Id:{}", refund.getId());
                RecipePaidDetailDocument recipePaidOrder = getRecipePaidOrder(recipe, refund);
                if (recipePaidOrder != null) {
                    LOGGER.debug("获得已缴费对象成功,订单号:{},支付平台:{}", recipePaidOrder.getOrderNum(),
                            recipePaidOrder.getPayChannelId());
                    String refundNo = checkRecipeAndRefundIsEqual(recipe, recipePaidOrder, refund);
                    if (StringUtil.isNotEmpty(refundNo)) {
                        // 请求退款对象
                        RefundReq refundInfo = new RefundReq();
                        // 商户订单号
                        refundInfo.setOutTradeNo(recipePaidOrder.getOrderNum());
                        // 退款流水号
                        refundInfo.setOutRequestNo(refundNo);
                        // 退款金额
                        refundInfo.setRefundAmount(refund.getCost());
                        // 退款总金额
                        refundInfo.setTotalFee(recipePaidOrder.getAmount());
                        // 退款操作
                        if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                            AliRefundRes refundRes = payService.refundPay(refundInfo);
                            // 支付宝退款结果
                            refunReturnMsg = refundRes.getMsg();
                        } else if (recipePaidOrder.getPayChannelId()
                                .equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                            WechatRefundRes refundRes = payService.refundPay(refundInfo);
                            // 微信退款结果
                            refunReturnMsg = refundRes.getResultCode().equals("SUCCESS") ? "SUCCESS"
                                    : refundRes.getReturnMsg();
                        } else if (recipePaidOrder.getPayChannelId()
                                .equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                            CmbRefundNoDupRes refundRes = payService.refundPay(refundInfo);
                            // 一网通退款结果
                            refunReturnMsg = StringUtil.isNull(refundRes.getHead().getCode()) ? "SUCESS"
                                    : refundRes.getHead().getErrMsg();
                        }
                        saveRecipeRefundResult(recipe, refund, refundMap, refundNo, refunReturnMsg);
                    }
                }
            }
        } else {
            LOGGER.debug("平台退费Id:{},门诊流水号:{},处方号:{},未找到缴费单", refund.getId(), refund.getClinicCode(),
                    refund.getRecipeNo());
        }
    }

    /**
     * 保存缴费退款信息.
     *
     * @param recipe 缴费对象.
     * @param refund 退款对象.
     * @param refundMap his返回退款对象.
     * @param refundNo 退款流水号.
     * @param refunReturnMsg 退款返回的信息.
     * @throws Exception 异常.
     */
    private void saveRecipeRefundResult(RecipeOrderDocument recipe, RefundByHis refund,
            Map<String, RecipeRefundDetailDocument> refundMap, String refundNo, String refunReturnMsg)
            throws Exception {
        RecipeRefundDetailDocument refundDetail = new RecipeRefundDetailDocument();
        BeanUtils.copyProperties(refund, refundDetail);
        refundDetail.setClinicCode(recipe.getClinicCode());
        refundDetail.setRefundNo(refundNo);
        refundDetail.setRefundReturnMsg(refunReturnMsg);
        refundMap.put(
                refund.getId().concat("_").concat(DateUtil.toString(new Date(), PEPConstants.DEFAULT_DATETIME_FORMAT)),
                refundDetail);
        recipe.setRecipeRefundDetailDocumentMap(refundMap);
        recipeServiceImpl.saveRecipeOrderDocument(recipe);
        if (refunReturnMsg.equalsIgnoreCase("Success")) {
            sendRecipeMsg(recipe, SendPushMsgEnum.RECIPE_REFUND_SUCCESS, refundDetail);
        } else {
            sendRecipeMsg(recipe, SendPushMsgEnum.RECIPE_REFUND_FAIL, refundDetail);
        }
    }

    /**
     * 取得缴费已支付信息.
     *
     * @param recipe 缴费对象.
     * @param refund his退款对象.
     * @return 支付详细信息.
     */
    private RecipePaidDetailDocument getRecipePaidOrder(RecipeOrderDocument recipe, RefundByHis refund) {
        RecipePaidDetailDocument recipePaidOrder = null;
        List<RecipeDetailAllDocument> detailList;
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
     * @param recipe 退款报文.
     * @param recipePaidOrder 已付款信息.
     * @param refund 退款历史.
     * @return 退款总数.
     * @throws Exception 异常.
     */
    private String checkRecipeAndRefundIsEqual(RecipeOrderDocument recipe, RecipePaidDetailDocument recipePaidOrder,
            RefundByHis refund) throws Exception {
        BasicInfoDocument basic = userInfoService.getFamilyMemberByUserIdAndMemberId(recipe.getCreateUserId(),
                recipe.getPatientId());
        ResModel<PayList> refundPayRes = this.findPayListModel(basic, recipe.getClinicCode(), "2", null, null, false);
        List<Pay> refundPayList = refundPayRes.getRes().getPayList();
        LOGGER.debug("HIS查询记录的退费集合数:{},退款Id:{},订单号:{}", refundPayList.size(), refund.getId(),
                recipePaidOrder.getOrderNum(), recipe.getClinicCode());
        BigDecimal hisRefundBig = new BigDecimal("0");
        for (Pay pay : refundPayList) {
            LOGGER.debug("HIS查询记录的退费项目:{},退费金额:{}", pay.getItemCode(), pay.getOwnCost());
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
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                AliRefundTradeQueryRes refundQuery = payService.queryRefund(recipePaidOrder.getOrderNum(), refundNo);
                // 退款查询结果
                if (refundQuery != null) {
                    if (refundQuery.getCode().equals("10000")) {
                        if (StringUtil.isNotEmpty(refundQuery.getRefundAmount())) {
                            finishBig = finishBig
                                    .add(new BigDecimal(refundQuery.getRefundAmount()).multiply(new BigDecimal("100")));
                        } else {
                            break;
                        }
                    } else {
                        LOGGER.debug("支付宝缴费查询失败,订单号:{},退费单号:{},失败消息:{}", recipePaidOrder.getOrderNum(), refundNo,
                                refundQuery.getMsg());
                        return null;
                    }
                } else {
                    LOGGER.debug("支付宝缴费查询失败,订单号:{},退费单号:{},查询返回值为空", recipePaidOrder.getOrderNum(), refundNo);
                    return null;
                }
            }
        } else if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                refundNo = recipePaidOrder.getOrderNum() + df.format(tempIndex);
                // 微信退款查询
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                WechatRefundQueryRes refundQuery = payService.queryRefund(recipePaidOrder.getOrderNum(), refundNo);
                if (refundQuery != null) {
                    if (refundQuery.getReturnCode().equals("SUCCESS")) {
                        if (StringUtil.isNotEmpty(refundQuery.getRefundFee())) {
                            finishBig = finishBig.add(new BigDecimal(refundQuery.getRefundFee()));
                        } else {
                            break;
                        }
                    } else {
                        LOGGER.debug("微信缴费查询失败,订单号:{},退费单号:{},未找到退费的标签", recipePaidOrder.getOrderNum(), refundNo);
                        return null;
                    }
                } else {
                    LOGGER.debug("微信缴费查询失败,订单号:{},退费单号:{},查询返回值为空", recipePaidOrder.getOrderNum(), refundNo);
                    return null;
                }
            }
        } else if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                // 一网通退款信息查询
                refundNo = recipePaidOrder.getOrderNum().substring(0, 18).concat(dfCmb.format(tempIndex));
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                CmbQueryRefundRes refundQuery = payService.queryRefund(recipePaidOrder.getOrderNum(), refundNo);
                if (refundQuery != null) {
                    // 如果查询有该订单的退款信息
                    if (StringUtil.isEmpty(refundQuery.getHead().getCode())) {
                        CmbBillRecordRes billRecord = refundQuery.getBody().getBillRecord().get(0);
                        // 如果订单已经直接退款成功
                        if (billRecord.getBillState().equals("210")) {
                            if (StringUtil.isNotEmpty(billRecord.getAmount())) {
                                finishBig = finishBig
                                        .add(new BigDecimal(billRecord.getAmount()).multiply(new BigDecimal("100")));
                            } else {
                                break;
                            }
                        } else {
                            LOGGER.debug("一网通缴费查询失败,订单号:{},退费单号:{},失败消息:{}", recipePaidOrder.getOrderNum(), refundNo,
                                    refundQuery.getHead().getErrMsg());
                            return null;
                        }
                    } else {
                        LOGGER.debug("一网通退款查询失败,无此退款订单号");
                        break;
                    }
                } else {
                    LOGGER.debug("一网通缴费查询失败,订单号:{},退费单号:{},查询返回值为空", recipePaidOrder.getOrderNum(), refundNo);
                    return null;
                }
            }
        }
        LOGGER.debug("查询支付平台结束,获得对应的退款钱数:{}", finishBig.toString());
        if (finishBig.add(new BigDecimal(refund.getCost()).abs()).compareTo(hisRefundBig) > 0) {
            LOGGER.debug("已退金额:{},待退金额:{},退款总额:{}", finishBig.intValue(),
                    new BigDecimal(refund.getCost()).abs().intValue(), hisRefundBig.intValue());
            LOGGER.debug("平台退费Id:{},门诊流水号:{},处方号:{},退款金额大于总退款金额", refund.getId(), refund.getClinicCode(),
                    refund.getRecipeNo());
            return null;
        } else {
            LOGGER.debug("退费金额校验成功后,返回的支付平台的退款单号:{}", refundNo);
            return refundNo;
        }
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
        boolean flag = false;
        User user = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(orderNum);
        if (order != null) {
            RecipeOrderDocument recipe = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
            if (recipe != null) {
                LOGGER.debug("预支付前校验,找到订单和缴费单,门诊流水号:{}", recipe.getClinicCode());
                flag = getFlagByRecipeAmount(orderAmount, user, recipe);
                if (flag) {
                    recipe.getRecipeNonPaidDetail().setPayChannelId(String.valueOf(payWay.getCode()));
                    recipeServiceImpl.saveRecipeOrderDocument(recipe);
                    order.setPayWay(String.valueOf(payWay.getCode()));
                    orderService.save(order);
                } else {
                    LOGGER.debug("预支付前校验金额是否一致,返回否,门诊流水号:{}", recipe.getClinicCode());
                }
                recipe = this.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                if (StringUtil.isEmpty(recipe.getRecipeNonPaidDetail().getPayChannelId())) {
                    flag = false;
                }
            } else {
                LOGGER.debug("预支付前校验,根据订单未找到对应的缴费单,订单号:{}", orderNum);
            }
        } else {
            LOGGER.debug("预支付前校验,未查到订单号对应的订单,订单号:{}", orderNum);
        }
        return flag;
    }

    /**
     * 校验缴费与支付是否相等
     *
     * @param orderAmount 退款金额.
     * @param user 用户.
     * @param recipe 退款报文.
     * @return 检查结果.
     * @throws Exception 异常.
     */
    private boolean getFlagByRecipeAmount(String orderAmount, User user, RecipeOrderDocument recipe) throws Exception {
        boolean flag = false;
        BasicInfoDocument basic = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(),
                recipe.getPatientId());
        ResModel<PayList> payList = this.findPayListModel(basic, recipe.getClinicCode(), "0", null, null, false);
        if (payList.getReturnCode() == ReturnCode.SUCCESS) {
            if (payList.getRes().getPayList() != null) {
                List<Pay> list = payList.getRes().getPayList();
                BigDecimal total = new BigDecimal("0");
                for (Pay pay : list) {
                    if (pay.getClinicCode().equals(recipe.getClinicCode())) {
                        total = total.add(new BigDecimal(pay.getOwnCost()));
                    }
                }
                if (total.compareTo(new BigDecimal(recipe.getRecipeNonPaidDetail().getAmount())) == 0
                        && total.compareTo(new BigDecimal(orderAmount)) == 0) {
                    flag = true;
                }
                LOGGER.debug("预支付前对金额金额进行校验,门诊流水号:{},支付金额:{},HIS端待缴金额:{}", recipe.getClinicCode(), orderAmount,
                        total.toString());
            }
        } else {
            LOGGER.debug("预支付前校验,获得HIS的待缴费接口返回值有问题,返回消息:{}", payList.getReturnMsg());
        }
        return flag;
    }
}
