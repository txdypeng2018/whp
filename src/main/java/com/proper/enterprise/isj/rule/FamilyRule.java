package com.proper.enterprise.isj.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class FamilyRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyRule.class);

    public int addLimit(int familyMemberSize, String lastCreateTime, int maxFamilyMemberSize,
            int addIntervalDays) throws Exception {
        int leftIntervalDays = 0;
        if (familyMemberSize >= maxFamilyMemberSize && StringUtil.isNotEmpty(lastCreateTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            long todayMillis = calendar.getTimeInMillis();
            calendar.setTime(sdf.parse(lastCreateTime));
            long lastMillis = calendar.getTimeInMillis();
            int intervalDays = Long.valueOf((todayMillis - lastMillis) / (1000 * 60 * 60 * 24)).intValue();
            leftIntervalDays = addIntervalDays - intervalDays;
        }
        LOGGER.debug("Checking addLimit rule: {} {} {} {} return {}", familyMemberSize, lastCreateTime,
                maxFamilyMemberSize, addIntervalDays, leftIntervalDays);
        return leftIntervalDays;
    }
}
