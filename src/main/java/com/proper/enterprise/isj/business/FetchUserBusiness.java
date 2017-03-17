/**
 * <p>
 * application name: FetchUserBusiness.java
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

package com.proper.enterprise.isj.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.UserContext;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;

/**
 * .
 * <p>
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */
@Service
public class FetchUserBusiness<T, C extends UserContext<T>> implements IBusiness<T, C> {

    @Autowired
    UserService userService;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.support.business.Business#process(com.proper.
     * enterprise.isj.support.business.BusinessContext)
     */
    @Override
    public void process(C ctx) throws RegisterException {
        if (ctx != null) {
            User user = userService.getCurrentUser();
            if (user == null) {
                throw new RegisterException(CenterFunctionUtils.USERINFO_LOGIN_ERR);
            }
            ctx.setUser(user);
        }
    }

}
