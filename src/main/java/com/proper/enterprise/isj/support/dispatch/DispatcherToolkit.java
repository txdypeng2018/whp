/**
 * <p>
 * application name: DispatcherToolkit.java
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

package com.proper.enterprise.isj.support.dispatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * .
 * <p>
 * 描述该类功能介绍.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */
@Service
public class DispatcherToolkit {

    @Autowired
    Dispatcher dispather;

    @SuppressWarnings("unchecked")
    public <T> T dispatch(String key, Object... params) {
        try {
            return (T) dispather.dispatch(key, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
