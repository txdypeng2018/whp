/**
* <p> application name: UpdateLatestVersionInfoEntry.java  </p>
* <p> description: </p>
* <p> Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心 </p>
* <p> date: 2017年2月21日 </p>
* @author:  王东石<wangdongshi@propersoft.cn> 
* @version Ver 1.0 2017年2月21日 新建
*/

package com.proper.enterprise.isj.app.controller.entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.app.service.AppVersionService;
import com.proper.enterprise.isj.support.dispatch.ControllerEntry;

/**
 * .
 * <p>
 * 描述该类功能介绍.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-21 新建
 */
@Component
public class UpdateLatestVersionInfoEntry
        implements ControllerEntry<AppVersionDocument> {

    @Autowired
    AppVersionService service;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.proper.enterprise.isj.support.dispatch.ControllerEntry#handle(java.
     * lang.Object[])
     */
    @Override
    public AppVersionDocument handle(Object... objects) throws Exception {
        return updateLatestVersionInfo((AppVersionDocument) objects[0]);
    }

    protected AppVersionDocument updateLatestVersionInfo(
            AppVersionDocument document) {
        return service.save(document);
    }

}
