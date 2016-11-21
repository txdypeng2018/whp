package com.proper.enterprise.isj.user.service.impl.custom;

import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.req.PatReq;
import com.proper.enterprise.isj.webservices.model.res.PatRes;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/11/3 0003.
 */
public class UserInfoWebServiceClientCustom extends WebServicesClient{
    @Override
    public ResModel<PatRes> createPat(PatReq req) throws Exception {
        if(StringUtil.isEmpty(req.getCardNo())){
            //注册的用户
            if(req.getMarkNo().equals(req.getName())){
                ResModel<PatRes> patRes = new ResModel<>();
                PatRes patRes1 = new PatRes();
                patRes1.setCardNo(req.getMarkNo());
                patRes.setReturnCode(0);
                patRes.setRes(patRes1);
                return patRes;
            }else{
                ResModel<PatRes> patRes = new ResModel<>();
                patRes.setReturnCode(-1);
                patRes.setReturnMsg("app姓名与院端不符");
                return patRes;
            }

        }else{
            if(req.getCardNo().equals(req.getMarkNo())||req.getCardNo().equals(req.getMobile())){
                ResModel<PatRes> patRes = new ResModel<>();
                PatRes patRes1 = new PatRes();
                patRes1.setCardNo(req.getCardNo());
                patRes.setReturnCode(2);
                patRes.setRes(patRes1);
                return patRes;
            }else{
                ResModel<PatRes> patRes = new ResModel<>();
                patRes.setReturnCode(-1);
                patRes.setReturnMsg("app预留手机号与医院不符");
                return patRes;
            }

        }
    }
}
