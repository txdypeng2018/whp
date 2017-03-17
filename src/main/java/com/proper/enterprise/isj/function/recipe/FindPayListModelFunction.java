package com.proper.enterprise.isj.function.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.function.payment.FetchPayListReqFunction;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.req.PayListReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * old:com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.findPayListModel(BasicInfoDocument, String, String, String, String, boolean)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class FindPayListModelFunction implements IFunction<ResModel<PayList>> {

    @Autowired
    FunctionToolkit toolkitx;
    
    @Autowired
    @Lazy
    WebServicesClient webServicesClient;
    
    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;
    
    @Override
    public ResModel<PayList> execute(Object... params) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * 获取支付详情信息.
     *
     * @param basic 用户基本信息.
     * @param clinicCode 门诊流水号.
     * @param payStatus 支付状态.
     * @param sDate 开始日期.
     * @param eDate 结算日期.
     * @param userCache 缓存.
     * @return 获取支付详情信息.
     * @throws Exception 异常.
     */
    public ResModel<PayList> findPayListModel(BasicInfoDocument basic, String clinicCode, String payStatus,
            String sDate, String eDate, boolean userCache) throws Exception {
        PayListReq listReq = toolkitx.executeFunction(FetchPayListReqFunction.class, basic, clinicCode, payStatus, sDate, eDate);
        if (userCache) {
            return webService4HisInterfaceCacheUtil.getCachePayListRes(listReq);
        }
        return webServicesClient.getPayDetailAll(listReq);
    }

}
