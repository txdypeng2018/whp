package com.proper.enterprise.isj.rule;

import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PersonRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRule.class);

    /**
     * 儿童与成人年龄分界线
     */
    public final static int ADULT_MIN_AGE = ConfCenter.getInt("isj.adultMinAge", 14);

    public boolean isChild(String idCard) {
        int age = IdcardUtils.getAgeByIdCard(idCard);
        LOGGER.debug("Checking age {} is child or not", age);
        return age < ADULT_MIN_AGE;
    }

    public boolean isAdult(String idCard) {
        return !isChild(idCard);
    }

    public boolean isMale(String idCard) {
        boolean result = "M".equals(IdcardUtils.getGenderByIdCard(idCard));
        LOGGER.debug("Checking isMale rule: {}", result);
        return result;
    }

    public boolean isFemale(String idCard) {
        boolean result = "F".equals(IdcardUtils.getGenderByIdCard(idCard));
        LOGGER.debug("Checking isFemale rule: {}", result);
        return result;
    }

}
