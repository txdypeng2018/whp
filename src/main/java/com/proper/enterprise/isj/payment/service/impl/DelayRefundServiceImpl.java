package com.proper.enterprise.isj.payment.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.logger.PayLogConstrants;
import com.proper.enterprise.isj.payment.logger.entity.DefaultPayLogRecordEntity;
import com.proper.enterprise.isj.payment.logger.repository.PayLogRecordRepository;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
import com.proper.enterprise.isj.payment.service.DelayRefundService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class DelayRefundServiceImpl implements DelayRefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayRefundServiceImpl.class);

    private static final Object UPDATE_STATUS_LOCK = new Object();

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeServiceImpl recipeServiceImpl;

    @Autowired
    PayLogRecordRepository repo;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    OrderService orderService;
    
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Override
    public void doDelayRefund(long delayTime) {
        List<DefaultPayLogRecordEntity> list = repo
                .findByStepAndStepStatus(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY, PayLogConstrants.STATUS_DEFAULT);

        for (DefaultPayLogRecordEntity record : list) {
            if (validateCanBeHandle(record, delayTime)) {
                handleRecord(record);
            }
        }
    }

    private static final int FUNC_HANDLERECORD = 0xA0020000;
    private static final int FUNC_HANDLERECORD_NULL_OBJ = FUNC_HANDLERECORD | PayLogUtils.CAUSE_TYPE_FAIL | 0x1;
    private static final int FUNC_HANDLERECORD_WRONG_TYPE = FUNC_HANDLERECORD | PayLogUtils.CAUSE_TYPE_FAIL | 0x2;

    protected void handleRecord(DefaultPayLogRecordEntity record) {
        LOGGER.trace("Delay Rufund Task start.");
        try {
            Object obj = fetchRecordObject(record);
            if (obj != null) {
                if (obj instanceof PayRegReq) {
                    handleRegristration(record, (PayRegReq) obj);
                } else if (obj instanceof Order) {
                    handleRecipe(record, (Order) obj);
                } else {
                    LOGGER.error("延时退款日志中业务对象类型异常：" + JSONUtil.toJSON(record));
                    updateStatusAsFail(record, FUNC_HANDLERECORD_WRONG_TYPE);
                }
            } else {
                LOGGER.error("延时退款日志中业务对象为空：" + JSONUtil.toJSON(record));
                updateStatusAsFail(record, FUNC_HANDLERECORD_NULL_OBJ);
            }
        } catch (Throwable t) {
            restoreStatus(record);
            LOGGER.error(t.getMessage(), t);
        }finally{
            LOGGER.trace("Delay Rufund Task is finished.");
        }
    }

    private static final int FUNC_HANDLERECIPE = 0xA0030000;
    private static final int FUNC_HANDLERECIPE_DUPLICATE_ORDER = FUNC_HANDLERECIPE | PayLogUtils.CAUSE_TYPE_FAIL | 0x1;
    private static final int FUNC_HANDLERECIPE_WRONG_AMOUNT = FUNC_HANDLERECIPE | PayLogUtils.CAUSE_TYPE_FAIL | 0x2;
    
    private void handleRecipe(DefaultPayLogRecordEntity record, Order order) throws Exception {
        String refundNo = order.getOrderNo() + "001";
        if (order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            refundNo = order.getOrderNo().substring(0, 18).concat("01");
        }
        RecipeOrderDocument regBack = recipeService.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
        RecipePaidDetailDocument detail = regBack.getRecipeNonPaidDetail();
        if (detail == null) {
            detail = new RecipePaidDetailDocument();
        }
        BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(regBack.getCreateUserId(),
                regBack.getPatientId());
        Map<String, String> requestOrderNoMap = recipeService.getRecipeRequestOrderNoMap(regBack);
        if (requestOrderNoMap.containsKey(order.getOrderNo())) {
            LOGGER.trace("延时退款，订单号重复调用缴费接口,直接返回,不对HIS接口进行调用,门诊流水号:{}", regBack.getClinicCode());
            this.updateStatusAsFail(record, FUNC_HANDLERECIPE_DUPLICATE_ORDER);
            return;
        }
        detail.setRefundNum(refundNo);
        LOGGER.trace("延时退款，退款单号:{}", refundNo);
        if (StringUtil.isEmpty(detail.getDescription())) {
            detail.setDescription("");
        }
        LOGGER.trace("延时退款，保存异常消息前,退款单号:{}", refundNo);
        detail.setDescription(detail.getDescription().concat(",").concat("延时退款"));
        LOGGER.trace("延时退款，保存异常消息后,退款单号:{}", refundNo);
        
        requestOrderNoMap.put(order.getOrderNo(), String.valueOf(order.getPayWay()));
        LOGGER.trace("延时退款，将订单号添加到计算平台缴费情况的Map中,退款单号:{},订单号:{}", refundNo, order.getOrderNo());
        if (!recipeService.checkRecipeFailCanRefund(order, regBack, requestOrderNoMap, basicInfo, detail)) {
            LOGGER.trace("延时退款，诊间缴费失败后,核对平台与HIS已缴金额不一致,不能进行退款,退款单号:{},订单号:{}", refundNo, order.getOrderNo());
            this.updateStatusAsFail(record, FUNC_HANDLERECIPE_WRONG_AMOUNT);
        }
        try {
            boolean refundFlag = recipeService.refundMoney2User(order, refundNo, false);
            order = recipeService.saveOrUpdateOrderFailInfo(order, order.getPayWay(), regBack, refundNo, detail, refundFlag);
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeServiceImpl.saveRecipeOrderDocument(regBack);
            success(record);
        } catch (Exception e2) {
            LOGGER.debug("延时退款，RecipeServiceNotxImpl.saveUpdateRecipeAndOrder[Exception]:", e2);
            LOGGER.trace("延时退款，诊间缴费HIS抛异常,调用支付平台退费失败,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
            detail.setDescription(detail.getDescription().concat(
                    ",诊间缴费HIS抛异常,调用支付平台退费失败,订单号:".concat(order.getOrderNo()).concat(",退费单号:").concat(refundNo)));
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeServiceImpl.saveRecipeOrderDocument(regBack);
            recipeService.sendRecipeMsg(regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            restoreStatus(record);
        }

    }

    private static final int FUNC_HANDLEREG = 0xA0030000;
    private static final int FUNC_HANDLEREG_NOT_FOUND_ORDER = FUNC_HANDLEREG | PayLogUtils.CAUSE_TYPE_FAIL | 0x1;
    private void handleRegristration(DefaultPayLogRecordEntity record, PayRegReq req) throws Exception, IOException {
        Order order = orderService.findByOrderNo(req.getOrderId());
        if (order != null) {
            RegistrationDocument reg = this.getRegistrationDocumentById(order.getFormId());
            registrationService.saveRefundAndUpdateRegistrationDocument(reg);
            success(record);
        } else {
            LOGGER.error("延时退款日志中业务对象为空：" + JSONUtil.toJSON(record));
            updateStatusAsFail(record, FUNC_HANDLEREG_NOT_FOUND_ORDER);
        }
    }

    private void success(DefaultPayLogRecordEntity record) {
        synchronized (UPDATE_STATUS_LOCK) {
            DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
            cur.setStep(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY);
            cur.setStepStatus(PayLogConstrants.STATUS_MASK_SUCCESS);
            repo.save(cur);
            BeanUtils.copyProperties(cur, record);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object fetchRecordObject(DefaultPayLogRecordEntity record) {
        Object res = null;
        String className = record.getJavaType();
        String content = record.getContent();
        try {
            Class clz = Class.forName(className);
            res = JSONUtil.parse(content, clz);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return res;
    }

    /**
     * 延时退款日志无法被处理(处理失败时)修改失败位置.
     *
     * @param record 被影响的记录.
     */
    protected void updateStatusAsFail(DefaultPayLogRecordEntity record, int position) {
        synchronized (UPDATE_STATUS_LOCK) {
            DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
            cur.setStep(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY);
            cur.setStepStatus(PayLogConstrants.STATUS_MASK_FAIL);
            cur.setCause(position);
            repo.save(cur);
            BeanUtils.copyProperties(cur, record);
        }
    }

    /**
     * 发生异常，恢复到可以重新执行的状态.
     *
     * @param record 被影响的记录.
     */
    protected void restoreStatus(DefaultPayLogRecordEntity record) {
        synchronized (UPDATE_STATUS_LOCK) {
            DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
            cur.setStep(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY);
            cur.setStepStatus(PayLogConstrants.STATUS_DEFAULT);
            repo.save(cur);
            BeanUtils.copyProperties(cur, record);
        }
    }

    protected boolean validateCanBeHandle(DefaultPayLogRecordEntity record, long delayTime) {
        boolean res = false;

        Date logDate = DateUtil.toDateTime(record.getLogTm());
        long logTm = logDate.getTime();
        long curTm = System.currentTimeMillis();

        res = logTm + delayTime * 1000 < curTm;
        if (res) {
            synchronized (UPDATE_STATUS_LOCK) {
                DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
                res = cur.getStep() == PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY
                        && cur.getStepStatus() == PayLogConstrants.STATUS_DEFAULT;
                if (res) {
                    cur.setStepStatus(PayLogConstrants.STATUS_MASK_START);
                    repo.save(cur);
                    record.setStepStatus(PayLogConstrants.STATUS_MASK_START);
                }
            }
        }
        return res;
    }

    public RegistrationDocument getRegistrationDocumentById(String id) {
        return registrationServiceImpl.getRegistrationDocumentById(id);
    }

}
