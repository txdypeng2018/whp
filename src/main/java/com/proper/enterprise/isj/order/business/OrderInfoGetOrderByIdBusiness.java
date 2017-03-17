package com.proper.enterprise.isj.order.business;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.OrderNumContext;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class OrderInfoGetOrderByIdBusiness<M extends OrderNumContext<Map<String, Object>> & ModifiedResultBusinessContext<Map<String, Object>>>
        implements IBusiness<Map<String, Object>, M> {
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

    @Override
    public void process(M ctx) {
        String orderNum = ctx.getOrderNum();
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
        String description;
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
        ctx.setResult(orderMap);
    }
}
