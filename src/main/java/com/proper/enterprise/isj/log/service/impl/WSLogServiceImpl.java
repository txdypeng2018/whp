package com.proper.enterprise.isj.log.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.log.document.WSLogDocument;
import com.proper.enterprise.isj.log.repository.WSLogRepository;
import com.proper.enterprise.isj.log.service.WSLogService;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.entity.DataTrunk;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class WSLogServiceImpl extends AbstractService implements ILoggable, WSLogService {

    @Autowired
    MongoTemplate mongoTemplate;

    /* (non-Javadoc)
     * @see com.proper.enterprise.isj.log.service.WSLogService#persistLog(java.lang.String, java.util.Map, java.lang.String, java.lang.Object, long)
     */
    @Override
    @Async
    public void persistLog(String methodName, Map<String, Object> param, String req, Object obj, long duration) {
        String res;
        if (obj instanceof String) {
            res = (String) obj;
        } else if (obj instanceof Throwable) {
            res = traceThrowable((Throwable) obj);
            error("Error occurs when invoke {} with {} ({}): {}", methodName, param, req, res);
        } else {
            res = obj.toString();
        }

        toolkit.executeRepositoryFunction(WSLogRepository.class, "save",
                new WSLogDocument(methodName, param, req, res, duration));
    }

    /* (non-Javadoc)
     * @see com.proper.enterprise.isj.log.service.WSLogService#getWsLogList(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public DataTrunk<WSLogDocument> getWsLogList(int pageNo, int pageSize, String search, String startDate,
            String endDate, String methodName) throws Exception {
        String startObjectId = convertTimeToObjectId(startDate);
        String endObjectId = convertTimeToObjectId(endDate);
        Query query = new Query();
        if (StringUtil.isNotEmpty(search)) {
            Pattern ptSearch = Pattern.compile("^.*" + search + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(Criteria.where("methodName").regex(ptSearch),
                    Criteria.where("req").regex(ptSearch), Criteria.where("res").regex(ptSearch)));
        }
        if (StringUtil.isNotEmpty(startDate) && StringUtil.isNotEmpty(endDate)) {
            query.addCriteria(Criteria.where("id").gte(new ObjectId(startObjectId)).lte(new ObjectId(endObjectId)));
        } else if (StringUtil.isNotEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            query.addCriteria(Criteria.where("id").gte(new ObjectId(startObjectId)));
        } else if (StringUtil.isEmpty(startDate) && StringUtil.isNotEmpty(endDate)) {
            query.addCriteria(Criteria.where("id").lte(new ObjectId(endObjectId)));
        }
        if (StringUtil.isNotEmpty(methodName)) {
            query.addCriteria(Criteria.where("methodName").is(methodName));
        } else {
            query.addCriteria(Criteria.where("methodName").ne("SMS"));
        }
        long count = mongoTemplate.count(query, WSLogDocument.class);
        query.skip((pageNo - 1) * pageSize);
        query.limit(pageSize);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        List<WSLogDocument> logs = mongoTemplate.find(query, WSLogDocument.class);
        return new DataTrunk<WSLogDocument>(logs, count);
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

    private String convertTimeToObjectId(String time) throws Exception {
        String objectId = "";
        if (StringUtil.isNotEmpty(time)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(time);
            Long seconds = date.getTime() / 1000;
            String secondHex = Long.toHexString(seconds);
            objectId = secondHex + "0000000000000000";
        }
        return objectId;
    }
}
