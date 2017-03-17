/**
* <p> application name: GetLatestVersionInfoEntry.java  </p>
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
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * /app/latest.
 * <p>
 * 获得最新版本.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-21 新建
 * 
 */
@Component
public class GetLatestVersionInfoEntry
        implements ControllerEntry<AppVersionDocument> {

    public static final String KEY = "getLatestVersionInfoEntry";

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
        return getLatestVersionInfo((String) objects[0]);
    }

    protected AppVersionDocument getLatestVersionInfo(String current) {
        int latestVersion = service.getLatestVersion();
        int currentVersion = StringUtil.isNull(current) ? -1
                : Integer.parseInt(current);
        AppVersionDocument res = latestVersion > currentVersion
                ? service.getLatestVersionInfo() : null;
        return res;
    }
}
