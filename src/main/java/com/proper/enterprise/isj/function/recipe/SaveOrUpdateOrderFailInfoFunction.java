package com.proper.enterprise.isj.function.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.function.message.SaveSingleDetailMessageFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * old:com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.saveOrUpdateOrderFailInfo(Order, String, RecipeOrderDocument, String, RecipePaidDetailDocument, boolean)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class SaveOrUpdateOrderFailInfoFunction implements IFunction<Order>, ILoggable {

    @Autowired
    FunctionToolkit toolkit;
    @Override
    public Order execute(Object... params) throws Exception {
        return saveOrUpdateOrderFailInfo((Order) params[0], (String) params[1], (RecipeOrderDocument) params[2], (String) params[3],
            (RecipePaidDetailDocument) params[4], (Boolean) params[5]);
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
    public Order saveOrUpdateOrderFailInfo(Order order, String channelId, RecipeOrderDocument regBack, String refundNo,
            RecipePaidDetailDocument detail, boolean refundFlag) throws Exception {
        if (refundFlag) {
            debug("诊间缴费HIS抛异常,对缴费进行退费,退费成功,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
            order.setOrderStatus("3");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(channelId));
            // orderService.save(order);
            toolkit.executeFunction(SaveSingleDetailMessageFunction.class, regBack, SendPushMsgEnum.RECIPE_PAID_FAIL, regBack);
            detail.setRefundStatus("1");
        } else {
            debug("诊间缴费HIS抛异常,对缴费进行退费,退费失败,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
            order.setOrderStatus("5");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(channelId));
            // orderService.save(order);
            toolkit.executeFunction(SaveSingleDetailMessageFunction.class, regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            detail.setRefundStatus("0");
        }
        return order;
    }
}
