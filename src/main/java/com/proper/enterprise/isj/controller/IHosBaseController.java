package com.proper.enterprise.isj.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.controller.BaseController;

public abstract class IHosBaseController extends BaseController implements ILoggable {

    public static class RetValAndHeaderResult implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
        private String retValue;
        private HttpHeaders headers;
        
        public RetValAndHeaderResult(){
            
        }
        
        

        public RetValAndHeaderResult(String retValue, HttpHeaders headers) {
            super();
            this.retValue = retValue;
            this.headers = headers;
        }



        public String getRetValue() {
            return retValue;
        }

        public void setRetValue(String retValue) {
            this.retValue = retValue;
        }

        public HttpHeaders getHeaders() {
            return headers;
        }

        public void setHeaders(HttpHeaders headers) {
            this.headers = headers;
        }

    }

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    protected BusinessToolkit toolkit;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, WebRequest request) {
        error(ex.getLocalizedMessage(), ex);
        return super.handleException(ex, request);
    }

}
