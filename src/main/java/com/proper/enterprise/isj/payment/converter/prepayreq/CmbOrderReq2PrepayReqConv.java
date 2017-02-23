package com.proper.enterprise.isj.payment.converter.prepayreq;

import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;
import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq;
import org.springframework.beans.factory.annotation.Autowired;

public class CmbOrderReq2PrepayReqConv extends AbstractManagedConverter<Class<Object>, CmbOrderReq, PrepayReq> {

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public PrepayReq convert(CmbOrderReq source, PrepayReq target) {
        // 订单号
        target.setOutTradeNo(source.getBillNo());
        // 需要先进行查询,查询用户信息绑定的协议号,如果没有签署协议则需要生成新的协议号
        User currentUser = userService.getCurrentUser();
        BasicInfoDocument basicInfo = userInfoService.getUserInfoByUserId(currentUser.getId());
        // 用户ID
        target.setUserId(basicInfo.getId());
        // 订单金额
        target.setTotalFee(String.valueOf(source.getAmount()));
        // 支付用途
        target.setPayIntent(source.getMerchantPara());
        // 支付方式
        target.setPayWay(BusinessPayConstants.ISJ_PAY_WAY_CMB);
        return target;
    }

}
