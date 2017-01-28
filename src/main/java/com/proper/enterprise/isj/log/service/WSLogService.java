package com.proper.enterprise.isj.log.service;

import com.proper.enterprise.isj.log.document.WSLogDocument;
import com.proper.enterprise.isj.log.repository.WSLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WSLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WSLogService.class);

    @Autowired
    private WSLogRepository repository;

    @Async
    public void persistLog(String methodName, Map<String, Object> param,
                            String req, Object obj, long duration) {
        String res;
        if (obj instanceof String) {
            res = (String) obj;
        } else if (obj instanceof Throwable) {
            res = traceThrowable((Throwable) obj);
            LOGGER.error("Error occurs when invoke {} with {} ({}): {}", methodName, param, req, res);
        } else {
            res = obj.toString();
        }
        repository.save(new WSLogDocument(methodName, param, req, res, duration));
    }

    private String traceThrowable(Throwable t) {
        if (t == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(t.toString());
        for (StackTraceElement ste : t.getStackTrace()) {
            sb.append("\r\n\tat ").append(ste);
        }
        sb.append("\r\n").append(traceThrowable(t.getCause()));
        return sb.toString();
    }

}
