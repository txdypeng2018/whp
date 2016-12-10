package com.proper.enterprise.isj.proxy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeDetailAllDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.*;
import com.proper.enterprise.isj.webservices.model.req.PayListReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/9/13 0013.
 */
@Service
public class RecipeServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeServiceImpl.class);

    @Autowired
    WebServicesClient webServicesClient;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    OrderService orderService;

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Autowired
    UserService userService;

    @Autowired
    MessagesService messagesService;

    @Autowired
    AliService aliService;

    @Autowired
    WeixinService weixinService;

    public RecipeOrderDocument getRecipeOrderDocumentById(String id) {
        return recipeOrderRepository.findOne(id);
    }

    public RecipeOrderDocument saveRecipeOrderDocument(RecipeOrderDocument recipeOrder) {
        return recipeOrderRepository.save(recipeOrder);
    }

    public void sendRecipePaidFailMsg(RecipeOrderDocument recipeOrder, SendPushMsgEnum msg)
            throws Exception {
        /*---------成功挂号提示-------*/
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(msg, recipeOrder));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(recipeOrder.getCreateUserId());
        regMsg.setUserName(recipeOrder.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }

    public RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception {
        RecipeOrderDocument recipeOrder = null;
        User user = userService.getCurrentUser();
        UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(user.getId());
        BasicInfoDocument info = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        PayListReq listReq = getPayListReq(info, clinicCode, null, null, null);
        listReq.setQueryType(QueryType.TO_PAY);
        ResModel<PayList> payListRes = webServicesClient.getPayDetailAll(listReq);
        if (payListRes.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("校验缴费中的未缴费项目返回错误,HIS返回的消息:"+payListRes.getReturnMsg()+",门诊流水号:"+clinicCode);
            throw new HisReturnException(payListRes.getReturnMsg());
        }
        List<Pay> payList = payListRes.getRes().getPayList();
        if(payList == null || payList.size() == 0){
            LOGGER.debug("未找到需要:"+payListRes.getReturnMsg()+",门诊流水号:"+clinicCode);
            throw new RecipeException(CenterFunctionUtils.ORDER_NON_DATA_ERR);
        }

        List<RecipeDetailAllDocument> detailList = new ArrayList<>();
        RecipeDetailAllDocument dt = null;
        BigDecimal bigDecimal = new BigDecimal("0");
        String hospSeq = "";
        for (Pay pay : payList) {
            if (clinicCode.equals(pay.getClinicCode())) {
                dt = new RecipeDetailAllDocument();
                BeanUtils.copyProperties(pay, dt);
                hospSeq = pay.getHospSequence();
                detailList.add(dt);
                bigDecimal = bigDecimal.add(new BigDecimal(dt.getOwnCost()));
            }
        }
        String orderNum = CenterFunctionUtils.createRegOrOrderNo(2);
        RecipePaidDetailDocument paidDetal = new RecipePaidDetailDocument();
        paidDetal.setOrderNum(orderNum);
        paidDetal.setAmount(bigDecimal.toString());
        paidDetal.setHospSequence(hospSeq);
        paidDetal.setDetailList(detailList);
        recipeOrder = createRecipeOrder(userInfo, info, clinicCode, paidDetal);
        orderService.saveCreateOrder(recipeOrder);
        return recipeOrder;
    }

    public PayListReq getPayListReq(BasicInfoDocument basic, String clinicCode, String payStatus, String sDate,
            String eDate) {
        String hosId = CenterFunctionUtils.getHosId();
        PayListReq listReq = new PayListReq();
        listReq.setHosId(hosId);
        listReq.setHospPatientId(basic.getId());
        listReq.setIdcardType(IDCardType.IDCARD);
        listReq.setIdcardNo(basic.getIdCard());
        listReq.setCardType(CardType.CARD);
        listReq.setCardNo(basic.getMedicalNum());
        listReq.setPatientName(basic.getName());
        listReq.setMobile(basic.getPhone());
        if (StringUtil.isNotEmpty(clinicCode)) {
            listReq.setClinicCode(clinicCode);
        }
        if (StringUtil.isNotEmpty(basic.getSexCode())) {
            switch (Integer.parseInt(basic.getSexCode())) {
            case 0:
                listReq.setPatientSex(Sex.FEMALE);
                break;
            case 1:
                listReq.setPatientSex(Sex.MALE);
                break;
            case 2:
                listReq.setPatientSex(Sex.SECRET);
                break;
            case 3:
                listReq.setPatientSex(Sex.OTHERS);
                break;
            default:
                listReq.setPatientSex(Sex.SECRET);
                break;
            }
        } else {
            listReq.setPatientSex(Sex.SECRET);
        }
        String idCard = basic.getIdCard();
        if (StringUtil.isNotEmpty(idCard)) {
            listReq.setPatientAge(IdcardUtils.getAgeByIdCard(idCard));
        } else {
            listReq.setPatientAge(0);
        }
        if (StringUtil.isEmpty(payStatus)) {
            listReq.setQueryType(QueryType.ALL);
        } else if (payStatus.equals(String.valueOf(0))) {
            listReq.setQueryType(QueryType.TO_PAY);
        } else if (payStatus.equals(String.valueOf(1))) {
            listReq.setQueryType(QueryType.PAYED);
        } else if (payStatus.equals(String.valueOf(2))) {
            listReq.setQueryType(QueryType.REFUND);
        }

        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        calS.setTime(new Date());
        calE.setTime(new Date());
        if (StringUtil.isEmpty(sDate)) {
            calS.add(Calendar.DAY_OF_MONTH, -CenterFunctionUtils.PAYMENT_MAX_DAY);
        } else {
            calS.setTime(DateUtil.toDate(sDate));
        }
        if (StringUtil.isNotEmpty(eDate)) {
            calE.setTime(DateUtil.toDate(eDate));
        }
        listReq.setBeginDate(calS.getTime());
        listReq.setEndDate(calE.getTime());
        return listReq;
    }

    public RecipeOrderDocument createRecipeOrder(UserInfoDocument userInfo,  BasicInfoDocument info, String clinicCode,
            RecipePaidDetailDocument paidDetal) throws Exception {
        RecipeOrderDocument recipeOrder = recipeOrderRepository.getByClinicCode(clinicCode);
        if (recipeOrder == null) {
            recipeOrder = new RecipeOrderDocument();
            recipeOrder.setPatientId(info.getId());
            recipeOrder.setPatientName(info.getName());
            recipeOrder.setClinicCode(clinicCode);
        } else {
            RecipePaidDetailDocument paid = recipeOrder.getRecipeNonPaidDetail();
            if (paid != null) {
                String payWay = paid.getPayChannelId();
                boolean paidFlag = orderService.checkOrderIsPay(payWay, paid.getOrderNum());
                if (paidFlag) {
                    throw new RecipeException(CenterFunctionUtils.ORDER_ALREADY_PAID_ERR);
                    // Order order =
                    // orderService.findByOrderNo(paid.getOrderNum());
                    // orderService.deleteOrder(order);
                }
            }
        }
        recipeOrder.setOperatorPhone(userInfo.getPhone());
        recipeOrder.setOperatorName(userInfo.getName());
        recipeOrder.setOperatorId(userInfo.getId());
        recipeOrder.setRecipeNonPaidDetail(paidDetal);
        return recipeOrderRepository.save(recipeOrder);
    }




}
