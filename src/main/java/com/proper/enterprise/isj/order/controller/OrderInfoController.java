package com.proper.enterprise.isj.order.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * Created by think on 2016/9/5 0005.
 */
@RestController
@RequestMapping(path = "/orders")
public class OrderInfoController extends BaseController {

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RegistrationService registrationService;

    @RequestMapping(path = "/{orderNum}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String orderNum) {
        DecimalFormat df = new DecimalFormat("0.00");
        Order order = orderService.findByOrderNo(orderNum);
        Date cTime = DateUtil.toDate(order.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(cTime);
        cal.add(Calendar.MINUTE, CenterFunctionUtils.ORDER_COUNTDOWN);
        Date nowDate = new Date();
        long min = (cal.getTimeInMillis() - nowDate.getTime()) / (60 * 1000);
        long sec = (cal.getTimeInMillis() - nowDate.getTime()) / 1000 % 60;
        if (!order.getOrderStatus().equals(String.valueOf(1))) {
            min = 0;
            sec = 0;
        }
        if (min >= CenterFunctionUtils.ORDER_COUNTDOWN) {
            min = CenterFunctionUtils.ORDER_COUNTDOWN;
            if (sec > 0) {
                sec = 0;
            }
        }
        String description = "";
        String isAppointment = "";
        if (order.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
            RegistrationDocument reg = registrationService.getRegistrationDocumentById(order.getFormId());
            if (reg.getIsAppointment().equals("1")) {
                description = "预约挂号缴费";
            } else {
                description = "当日挂号缴费";
            }
            isAppointment = reg.getIsAppointment();
        } else {
            description = "诊间缴费";
        }
        long[] closeTime = {min, sec };
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("orderNum", orderNum);
        orderMap.put("name", description);
        orderMap.put("description", description);
        orderMap.put("amount", df.format(new BigDecimal(order.getOrderAmount()).divide(new BigDecimal("100"))));
        orderMap.put("closeTime", closeTime);
        orderMap.put("isAppointment", isAppointment);
        return responseOfGet(orderMap);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> recipeOrder(@RequestBody Map<String, Object> recipeMap)
            throws Exception {
        String memberId = (String) recipeMap.get("memberId");
        List<Map<String, Object>> outpatientList = (List<Map<String, Object>>) recipeMap.get("outpatients");
        String clinicCode = "";
        for (Map<String, Object> map : outpatientList) {
            clinicCode = (String) map.get("outpatientNum");
        }
        if(StringUtil.isEmpty(clinicCode)){
            throw new RecipeException(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        RecipeOrderDocument tempRecipeOrder = recipeService.getRecipeOrderDocumentByClinicCode(clinicCode);
        if (tempRecipeOrder != null) {
            if (tempRecipeOrder.getRecipeNonPaidDetail() != null
                    && StringUtil.isNotEmpty(tempRecipeOrder.getRecipeNonPaidDetail().getOrderNum())) {
                if (tempRecipeOrder.getRecipeOrderReqMap()
                        .containsKey(tempRecipeOrder.getRecipeNonPaidDetail().getOrderNum())) {
                    throw new RecipeException(
                            "门诊流水号:" + clinicCode + ",缴费订单未得到处理,请在意见反馈中进行反馈," + CenterFunctionUtils.ORDER_SAVE_ERR);
                }
            }
        }
        RecipeOrderDocument recipeOrder = recipeService.saveOrderAndRecipeOrderDocument(memberId, clinicCode);
        if (recipeOrder == null) {
            throw new RecipeException(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        Map<String, String> map = new HashMap<>();
        map.put("orderNum", recipeOrder.getRecipeNonPaidDetail().getOrderNum());
        return responseOfPost(map);
    }
}
