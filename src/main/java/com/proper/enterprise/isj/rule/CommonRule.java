package com.proper.enterprise.isj.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CommonRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRule.class);

    public boolean in(String str, String... strs) {
        boolean result = Arrays.asList(strs).contains(str);
        LOGGER.debug("Checking in rule: {} {} in {}", str, result ? "IS" : "NOT", Arrays.toString(strs));
        return result;
    }

}
