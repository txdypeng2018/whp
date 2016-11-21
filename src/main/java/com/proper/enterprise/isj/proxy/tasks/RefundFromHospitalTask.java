package com.proper.enterprise.isj.proxy.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.proper.enterprise.isj.order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            e.printStackTrace();
        }
        if (res != null) {
            List<RefundByHis> list = res.getRes().getRefundlist();
            String orderNo = null;
            for (RefundByHis refundByHis : list) {
                if (refundByHis.getType().equals(String.valueOf(1))) {
                    orderNo = refundByHis.getOrderId();
                    if (!StringUtil.isNumeric(orderNo)) {
                        orderNo = orderNo.substring(1);
                    }
                    Order order = orderService.findByOrderNo(orderNo);
                    RegistrationDocument reg = registrationService.getRegistrationDocumentById(order.getFormId());
                    try {
                        if (reg != null) {
                            registrationService.saveRefundAndUpdateRegistrationDocument(reg);
                        }
                    } catch (Exception e) {
                        LOGGER.debug("挂号缴费退款异常,挂号单号:" + reg.getNum() + ",订单号:" + orderNo + ",异常信息:" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    RecipeOrderDocument recipe = recipeService
                            .getRecipeOrderDocumentByClinicCode(refundByHis.getClinicCode());
                    if (recipe != null) {
                        try {
                            recipeService.saveRefundAndUpdateRecipeOrderDocument(recipe, refundByHis);
                        } catch (Exception e) {
                            LOGGER.debug("诊间缴费退款异常,门诊流水号:" + refundByHis.getClinicCode() + ",异常信息:" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
