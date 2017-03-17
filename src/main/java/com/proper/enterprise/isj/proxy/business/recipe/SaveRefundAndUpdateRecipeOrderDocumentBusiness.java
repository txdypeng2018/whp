package com.proper.enterprise.isj.proxy.business.recipe;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.context.RecipeOrderDocumentContext;
import com.proper.enterprise.isj.context.RefundByHisContext;
import com.proper.enterprise.isj.function.recipe.CheckRecipeAndRefundIsEqualFunction;
import com.proper.enterprise.isj.function.recipe.FetchRecipePaidOrderFunction;
import com.proper.enterprise.isj.function.recipe.SaveRecipeRefundResultFunction;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeRefundDetailDocument;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.model.RefundReq;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;

/**
 * synchronized
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument,
 * RefundByHis)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @param <M>
 */
@Service
public class SaveRefundAndUpdateRecipeOrderDocumentBusiness<M extends RecipeOrderDocumentContext<Object> & RefundByHisContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    PayFactory payFactory;

    @Override
    public void process(M ctx) throws Exception {
        saveRefundAndUpdateRecipeOrderDocument(ctx.getRecipeOrderDocument(), ctx.getRefundByHis());
    }

    /**
     * (HIS线下退费)保存退款以及更新缴费信息.
     *
     * @param recipe 缴费对象.
     * @param refund 退款对象.
     * @throws Exception 异常.
     */
    public void saveRefundAndUpdateRecipeOrderDocument(RecipeOrderDocument recipe, RefundByHis refund)
            throws Exception {

        recipe = toolkitx.executeRepositoryFunction(RecipeOrderRepository.class, "findOne", recipe.getId());

        if (recipe != null) {
            boolean canRefundFlag = true;
            Map<String, RecipeRefundDetailDocument> refundMap = recipe.getRecipeRefundDetailDocumentMap();
            RecipeRefundDetailDocument detail;
            for (Map.Entry<String, RecipeRefundDetailDocument> stringRecipeRefundDetailDocumentEntry : refundMap
                    .entrySet()) {
                if (stringRecipeRefundDetailDocumentEntry.getKey().split("_")[0].equals(refund.getId())) {
                    debug("线下退费已经将此记录退回,不再重复退费,退费Id:{}", refund.getId());
                    canRefundFlag = false;
                    break;
                }
                detail = stringRecipeRefundDetailDocumentEntry.getValue();
                if (detail != null) {
                    if (StringUtil.isEmpty(detail.getRecipeNo()) || StringUtil.isEmpty(detail.getSequenceNo())) {
                        continue;
                    }
                    if (detail.getRecipeNo().equals(refund.getRecipeNo())
                            && detail.getSequenceNo().equals(refund.getSequenceNo())) {
                        debug("线下退费已经将此处方号下的序号进行了退费,不再重复退费,退费Id:{},处方号:{},序号:{}", refund.getId(), refund.getRecipeNo(),
                                refund.getSequenceNo());
                        canRefundFlag = false;
                        break;
                    }
                }
            }
            if (canRefundFlag) {
                String refunReturnMsg = "";
                debug("根据退款记录准备获得已缴费的对象,退款Id:{}", refund.getId());
                RecipePaidDetailDocument recipePaidOrder = toolkitx.executeFunction(FetchRecipePaidOrderFunction.class,
                        recipe, refund);
                if (recipePaidOrder != null) {
                    debug("获得已缴费对象成功,订单号:{},支付平台:{}", recipePaidOrder.getOrderNum(), recipePaidOrder.getPayChannelId());
                    String refundNo = toolkitx.executeFunction(CheckRecipeAndRefundIsEqualFunction.class, recipe,
                            recipePaidOrder, refund);
                    if (StringUtil.isNotEmpty(refundNo)) {
                        // 请求退款对象
                        RefundReq refundInfo = new RefundReq();
                        // 商户订单号
                        refundInfo.setOutTradeNo(recipePaidOrder.getOrderNum());
                        // 退款流水号
                        refundInfo.setOutRequestNo(refundNo);
                        // 退款金额
                        refundInfo.setRefundAmount(refund.getCost());
                        // 退款总金额
                        refundInfo.setTotalFee(recipePaidOrder.getAmount());
                        // 退款操作
                        if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                            AliRefundRes refundRes = payService.refundPay(refundInfo);
                            // 支付宝退款结果
                            refunReturnMsg = refundRes.getMsg();
                        } else if (recipePaidOrder.getPayChannelId()
                                .equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                            WechatRefundRes refundRes = payService.refundPay(refundInfo);
                            // 微信退款结果
                            refunReturnMsg = refundRes.getResultCode().equals("SUCCESS") ? "SUCCESS"
                                    : refundRes.getReturnMsg();
                        } else if (recipePaidOrder.getPayChannelId()
                                .equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                            CmbRefundNoDupRes refundRes = payService.refundPay(refundInfo);
                            // 一网通退款结果
                            refunReturnMsg = StringUtil.isNull(refundRes.getHead().getCode()) ? "SUCESS"
                                    : refundRes.getHead().getErrMsg();
                        }

                        toolkitx.executeFunction(SaveRecipeRefundResultFunction.class, recipe, refund, refundMap,
                                refundNo, refunReturnMsg);
                    }
                }
            }
        } else {
            debug("平台退费Id:{},门诊流水号:{},处方号:{},未找到缴费单", refund.getId(), refund.getClinicCode(), refund.getRecipeNo());
        }
    }

}
