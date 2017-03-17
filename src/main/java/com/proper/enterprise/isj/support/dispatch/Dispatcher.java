/**
* <p> application name: Dispatcher.java  </p>
* <p> description: </p>
* <p> Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心 </p>
* <p> date: 2017年2月21日 </p>
* @author:  王东石<wangdongshi@propersoft.cn> 
* @version Ver 1.0 2017年2月21日 新建
*/

package com.proper.enterprise.isj.support.dispatch;


/**
 * 分发器.
 * <p>该接口用于做业务分发.</p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-21 新建</p>
 */

public interface Dispatcher {
    
    /**
     * 分发方法.
     * 
     * @param <T> 结果类型.
     * @param key 分发key值.
     * @param params 执行参数.
     * @return 返回结果.
     * @throws Exception 可能抛出异常.
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    <T> T dispatch(String key, Object ... params) throws Exception;

}
