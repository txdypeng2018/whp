package com.proper.enterprise.isj.proxy.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.res.RefundByHisInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/10/6 0006.
 */
@Component
public class RefundFromHospitalTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundFromHospitalTask.class);

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    OrderService orderService;

    @Override
    public void run() {
        String hosId = CenterFunctionUtils.getHosId();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date sDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date eDate = cal.getTime();
        ResModel<RefundByHisInfo> res = null;
        try {
            res = webServicesClient.refundByHisToAPP(hosId, sDate, eDate);
        } catch (Exception e) {
            LOGGER.debug("获得线下退费结果异常", e);
            return;
        }
        if (res != null) {
            List<RefundByHis> list = res.getRes().getRefundlist();
            LOGGER.debug("查询线下退费记录数:" + list.size());
            String orderNo = null;
            RegistrationDocument reg = null;
            RecipeOrderDocument recipe = null;
            for (RefundByHis refundByHis : list) {
                if ("1".equals(refundByHis.getType())) {
                    try {
                        orderNo = refundByHis.getOrderId();
                        if (!StringUtil.isNumeric(orderNo)) {
                            orderNo = orderNo.substring(1);
                        }
                        Order order = orderService.findByOrderNo(orderNo);
                        reg = registrationService.getRegistrationDocumentById(order.getFormId());
                        if (reg != null) {
                            if (!reg.getStatusCode().equals(RegistrationStatusEnum.REFUND.getValue())) {
                                registrationService.saveRefundAndUpdateRegistrationDocument(reg);
                            }
                        } else {
                            LOGGER.debug("挂号缴费退款异常,未找到挂号单,订单号:" + orderNo);
                        }
                    } catch (Exception e) {
                        if (reg != null) {
                            LOGGER.debug("挂号缴费退款异常,挂号单号:" + reg.getNum() + ",订单号:" + orderNo, e);
                        } else {
                            LOGGER.debug("挂号缴费退款异常,订单号:" + orderNo, e);
                        }
                    }
                } else if ("2".equals(refundByHis.getType())) {
                    LOGGER.debug("门诊流水号:" + refundByHis.getClinicCode() + ",退费表id:" + refundByHis.getId());
                    try {
                        recipe = recipeService.getRecipeOrderDocumentByClinicCode(refundByHis.getClinicCode());
                        if (recipe != null) {
                            recipeService.saveRefundAndUpdateRecipeOrderDocument(recipe, refundByHis);
                        } else {
                            LOGGER.debug(
                                    "没查到缴费记录,门诊流水号:" + refundByHis.getClinicCode() + ",退费表id:" + refundByHis.getId());
                        }
                    } catch (Exception e) {
                        LOGGER.debug("诊间缴费退款异常,门诊流水号:" + refundByHis.getClinicCode(), e);
                    }
                } else {
                    LOGGER.debug("不是线下退费类型,HIS退费ID号:" + refundByHis.getId());
                }
            }
        }
    }
}
