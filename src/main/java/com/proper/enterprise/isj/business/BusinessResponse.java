package com.proper.enterprise.isj.business;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class BusinessResponse {
    @SuppressWarnings("rawtypes")
    protected <T> ResponseEntity<T> responseOfGet(T entity) {
        MultiValueMap<String, String> headers = null;
        boolean noContent = true;
        if (entity != null) {
            noContent = entity instanceof Collection && ((Collection) entity).isEmpty();
        }
        return noContent ? new ResponseEntity<T>(headers, HttpStatus.OK)
                : (new ResponseEntity<>(entity, headers, HttpStatus.OK));
    }
}
