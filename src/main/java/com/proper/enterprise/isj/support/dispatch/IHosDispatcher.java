/**
* <p> application name: IHosDispatcher.java  </p>
* <p> description: </p>
* <p> Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心 </p>
* <p> date: 2017年2月21日 </p>
* @author:  王东石<wangdongshi@propersoft.cn> 
* @version Ver 1.0 2017年2月21日 新建
*/

package com.proper.enterprise.isj.support.dispatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * .
 * <p>描述该类功能介绍.</p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-21 新建</p>
 */
@Service
@Lazy(true)
public class IHosDispatcher implements Dispatcher {

    @Autowired
    WebApplicationContext wac;
    
    /* (non-Javadoc)
     * @see com.proper.enterprise.isj.utils.dispatch.Dispatcher#dispatch(java.lang.String, java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T dispatch(String key, Object... params) throws Exception {
        try{
        return ((ControllerEntry<T>)wac.getBean(key)).handle(params);
        }catch(Exception e){
            throw e;
        }
    }

    
}
