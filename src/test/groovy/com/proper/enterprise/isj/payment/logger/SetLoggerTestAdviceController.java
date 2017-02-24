package com.proper.enterprise.isj.payment.logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;

@RestController
@RequestMapping(value = "/delayRefundTest")
public class SetLoggerTestAdviceController extends BaseController{
    
    
    @AuthcIgnore
    @GetMapping("/setLogger")
    public ResponseEntity<String> getLatestVersionInfo(@RequestParam(required = false) String value) {
        LoggerTestAdvice.setActive(Boolean.getBoolean(value));
        return responseOfGet(Boolean.toString(LoggerTestAdvice.active));
    }

}
