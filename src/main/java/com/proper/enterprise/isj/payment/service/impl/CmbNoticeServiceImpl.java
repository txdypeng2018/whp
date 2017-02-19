package com.proper.enterprise.isj.payment.service.impl;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.api.pay.service.NoticeService;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.platform.pay.cmb.service.CmbPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 一网通异步通知业务处理类.
 */
@Service("pay_notice_cmb")
public class CmbNoticeServiceImpl implements NoticeService<CmbPayEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmbNoticeServiceImpl.class);

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    CmbPayService cmbPayService;

    @Autowired
    OrderRepository orderRepo;

    /**
     * 异步通知业务处理代码
     *
     * @param cmbInfo 一网通异步通知处理参数
     */
    @Override
    public void saveNoticeProcess(CmbPayEntity  cmbInfo) throws Exception {
        LOGGER.debug("------------- 一网通异步通知相关业务处理--------处理开始---------");
        try {
            if (cmbInfo == null || StringUtil.isEmpty(cmbInfo.getBillNo()) || StringUtil.isEmpty(cmbInfo.getDate())) {
                LOGGER.debug("一网通异步通知参数解析异常:{}", JSONUtil.toJSON(cmbInfo));
            } else {
                // 查询订单号是否已经处理过了
                StringBuilder partOrderNo = new StringBuilder();
                partOrderNo.append("%").append(cmbInfo.getDate()).append(cmbInfo.getBillNo()).append("%");
                LOGGER.debug("orderNoPart:{}", partOrderNo.toString());
                // 通过日期和订单号拼接的订单号以及支付类型查询订单信息
                List<OrderEntity> orderList = orderRepo.findByOrderNoLike(partOrderNo.toString());
                // 获取查询条件并且只有一条
                if (orderList != null && orderList.size() == 1) {
                    LOGGER.debug("订单数据有效");
                    // 订单信息
                    Order orderInfo = orderList.get(0);
                    // 订单号
                    String orderNo = orderInfo.getOrderNo();
                    LOGGER.debug("订单号:" + orderNo);
                    // 没有处理过订单
                    if (orderInfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {
                        LOGGER.debug("没有处理过的订单");
                        // 保存支付宝异步通知信息
                        synchronized (orderInfo.getOrderNo()) {
                            try {
                                // 挂号单
                                if (orderInfo.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
                                    PayRegReq payReg = registrationService.convertAppInfo2PayReg(cmbInfo,
                                            orderInfo.getFormId());
                                    if (payReg != null) {
                                        registrationService.saveUpdateRegistrationAndOrder(payReg);
                                    } else {
                                        LOGGER.debug("未查到已支付的挂号单信息,订单号:{}", orderNo);
                                    }
                                    // 缴费
                                } else {
                                    orderInfo = recipeService.saveUpdateRecipeAndOrder(orderInfo.getOrderNo(),
                                            String.valueOf(PayChannel.WEB_UNION.getCode()), cmbInfo);
                                    if (orderInfo == null) {
                                        LOGGER.debug("缴费异常,订单号:{}", orderNo);
                                    } else {
                                        orderService.save(orderInfo);
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.debug("支付成功后,调用HIS接口中发生了错误,订单号:{}{}", orderNo, e);
                            }
                        }
                        Order orderinfoRet = orderService.findByOrderNo(orderNo);
                        LOGGER.debug("更新订单状态为:{}", orderinfoRet.getPaymentStatus());
                    }
                }
            }
            LOGGER.debug("------------- 一网通异步通知相关业务处理-----正常结束------------");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.debug("一网通异步通知业务处理异常:{}", e);
        }
    }
}
