package com.proper.enterprise.isj.function.payment;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.pay.enums.PayResType;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class FetchPayTimeResFunction implements IFunction<PayResultRes> {


    @Autowired
    OrderService orderService;
    
    @Override
    public PayResultRes execute(Object... params) throws Exception {
        return getPayTimeRes((String)params[0], (PrepayReq)params[1]);
    }
    
    public PayResultRes getPayTimeRes(String outTradeNo, PrepayReq prepayReq) {
        PayResultRes resObj = new PayResultRes();
        // 查询订单
        Order order = orderService.findByOrderNo(outTradeNo);
        // 查到订单信息
        if(order != null){
            // 获取订单生成日期
            Date cTime = DateUtil.toDate(order.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT);
            Calendar cal = Calendar.getInstance();
            cal.setTime(cTime);
            // 设定订单开始时间
            prepayReq.setPayTime(DateUtil.toString(cTime, "yyyyMMddHHmmss"));
            cal.add(Calendar.MINUTE, CenterFunctionUtils.ORDER_COUNTDOWN);
            Date nowDate = new Date();
            // 计算订单失效时间
            long min = (cal.getTimeInMillis() - nowDate.getTime()) / (60 * 1000);
            // 订单已经超时
            if (min < 0) {
                resObj.setResultCode(PayResType.SYSERROR);
                resObj.setResultMsg(CenterFunctionUtils.ORDER_OVERTIME_INVALID);
                return resObj;
            }
            // 设定超时时间
            prepayReq.setOverMinuteTime(String.valueOf(min + 1));
        } else {
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.ORDER_NON_DATA_ERR);
            return resObj;
        }
        return resObj;
    }

}
