/**
 * <p>
 * application name: BusinessContextFactory.java
 * </p>
 * <p>
 * description:
 * </p>
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * 
 * @author: 王东石<wangdongshi@propersoft.cn>
 * @version Ver 1.0 2017年2月22日 新建
 */

package com.proper.enterprise.isj.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IFactory;
import com.proper.enterprise.platform.core.business.factory.AutoProxyBizCtxFactory;

/**
 * .
 * <p>
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */
@Service
public class BusinessContextFactory implements IFactory {
    
    @Autowired
    AutoProxyBizCtxFactory innerFactory;

    /*
     * (non-Javadoc)
     * @see com.proper.enterprise.platform.utils.factory.Factory#create(java.lang.Object[])
     */
    @Override
    public <T> T create(Object... params) {
        if(params!=null&&params.length==1&&params[0] instanceof Class){
        return innerFactory.create(params);
        }else{
            throw new IllegalArgumentException("创建BusinessContext需要一个Class类型的参数. ");
        }
    }

}
