package com.proper.enterprise.isj.function.customservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;

/**
 * 旧的com.proper.enterprise.isj.proxy.service.impl.HospitalNavigationServiceImpl.getDisList()
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchDisListFunction implements IFunction<List<SubjectDocument>>, ILoggable {

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public List<SubjectDocument> execute(Object... params) throws Exception {
        return getDisList();
    }

    /**
     * 通过HIS接口获取院区列表信息.
     *
     * @return 返回应答.
     * @throws Exception 异常.
     */
    private List<SubjectDocument> getDisList() throws Exception {
        List<SubjectDocument> disList;
        try {
            disList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                    .get(String.valueOf(DeptLevel.CHILD.getCode())).get("0");
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            throw new HisLinkException(e.getMessage());
        } catch (Exception e) {
            debug("系统错误", e);
            throw new Exception(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return disList;
    }

}