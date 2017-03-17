/**
* <p> application name: ControllerEntry.java  </p>
* <p> description: </p>
* <p> Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心 </p>
* <p> date: 2017年2月21日 </p>
* @author:  王东石<wangdongshi@propersoft.cn> 
* @version Ver 1.0 2017年2月21日 新建
*/

package com.proper.enterprise.isj.support.dispatch;

import com.proper.enterprise.platform.core.api.IHandler;

/**
 * .
 * <p>
 * 描述该类功能介绍.
 * </p>
 * 
 * @param <T> 返回值类型.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-21 新建
 *           </p>
 */

public interface ControllerEntry<T> extends IHandler {

    /**
     * .
     * 
     * @param objects
     * @return
     * @throws Exception
     * @author 王东石<wangdongshi@propersoft.cn>
     */
    T handle(Object... objects) throws Exception;

}
