package com.proper.enterprise.isj.support.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.proper.enterprise.isj.business.BusinessToolkit;

public abstract class AbstractService {
    
    @Autowired
    protected BusinessToolkit toolkit;

}
