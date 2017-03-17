package com.proper.enterprise.isj.user.utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 手机号码工具.
 * Created by think on 2016/8/23 0023.
 */
public class MobileNoUtils {
    /**
     * 验证手机号的方法.
     *
     * @param mobiles
     *            手机号码.
     * @return 是否符合规则.
     */
    public static boolean isMobileNo(String mobiles) {
        boolean flag = true;
        if (mobiles.length() != 11 || !NumberUtils.isNumber(mobiles)) {
            flag = false;
        }
        return flag;
        // Pattern p =
        // Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0,5-9]))\\d{8}$");
        // Matcher m = p.matcher(mobiles);
        // return m.matches();
    }

}
