/**
 * <p>
 * application name: UserContext.java
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

import com.proper.enterprise.platform.core.api.BusinessContext;

/**
 * 操作用户信息的上下文.
 * <p>
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */

public interface UserIdContext<T> extends BusinessContext<T> {

    String getUserId();

    void setUserId(String userId);
}
