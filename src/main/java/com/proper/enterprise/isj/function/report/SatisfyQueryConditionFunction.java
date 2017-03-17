package com.proper.enterprise.isj.function.report;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SatisfyQueryConditionFunction implements IFunction<Boolean>, ILoggable {

    @Override
    public Boolean execute(Object... params) throws Exception {
        return satisfyQueryCondition((String) params[0], (String) params[1], (String) params[2], (String) params[3],
                (Integer) params[4]);
    }

    /**
     * 是否满足查询条件
     *
     * @param searchTime
     *            查询时间段参数
     * @param originSearchTime
     *            数据中时间段参数
     * @param searchStatus
     *            查询状态参数
     * @param originSearchState
     *            数据中状态参数
     * @param typeFlg
     *            报告类型
     * @return ret
     */
    public boolean satisfyQueryCondition(String searchTime, String originSearchTime, String searchStatus,
            String originSearchState, int typeFlg) {
        boolean ret = false;
        // 判断条件
        boolean timeFlg = false;
        boolean statusFlg = false;
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();
        Calendar calO = Calendar.getInstance();
        calS.setTime(new Date());
        calE.setTime(new Date());

        // searchTime查询参数不为空并且报告日期也不为空
        if (StringUtil.isNotEmpty(searchTime) && StringUtil.isNotEmpty(originSearchTime)) {
            // 普通报告
            if (1 == typeFlg) {
                calO.setTime(DateUtil.toDate(originSearchTime, "yyyyMMddHHmmss"));
                // 检查报告(pacs)
            } else if (2 == typeFlg) {
                calO.setTime(DateUtil.toDate(originSearchTime, "yyyy-MM-dd HH:mm:ss.S"));
            }
            // 一周内
            if ("week".equals(searchTime)) {
                debug("一周内");
                calS.add(Calendar.WEEK_OF_MONTH, -1);
                // 一月内
            } else if ("month".equals(searchTime)) {
                debug("一月内");
                calS.add(Calendar.MONTH, -1);
                // 半年内
            } else if ("halfYear".equals(searchTime)) {
                debug("半年内");
                calS.add(Calendar.MONTH, -6);
                // 一年内
            } else if ("year".equals(searchTime)) {
                debug("一年内");
                calS.add(Calendar.YEAR, -1);
            }
            if (calS.before(calO) && calE.after(calO)) {
                debug("在时间范围内");
                timeFlg = true;
            }
        } else if (StringUtil.isEmpty(searchTime)) {
            debug("时间不限");
            timeFlg = true;
        }

        // 状态查询条件为空
        if (StringUtil.isEmpty(searchStatus)) {
            statusFlg = true;
            if ("0".equals(originSearchState)) {
                timeFlg = true;
            }
            // 状态查询条件为已出
        } else if ("1".equals(searchStatus)) {
            if ("1".equals(originSearchState)) {
                statusFlg = true;
            }
            // 状态查询条件为未出
        } else if ("0".equals(searchStatus)) {
            if ("0".equals(originSearchState)) {
                statusFlg = true;
                timeFlg = true;
            }
        }
        // 添加结果
        if (timeFlg && statusFlg) {
            ret = true;
        }
        return ret;
    }

}
