package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.repository.ServiceUserOpinionRepository;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ServiceServiceImpl implements ServiceService {

    private static final Logger LOGGER  = org.slf4j.LoggerFactory.getLogger(ServiceServiceImpl.class);

    @Autowired
    BaseInfoRepository baseRepo;

    @Autowired
    ServiceUserOpinionRepository opinionRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public String getPhoneNum() {
        String phoneType = ConfCenter.get("isj.info.phone");
        List<BaseInfoEntity> retDoc = baseRepo.findByInfoType(phoneType);
        return retDoc.get(0).getInfo();
    }

    @Override
    public void saveBaseInfo(BaseInfoEntity baseInfo) {
        baseRepo.save(baseInfo);
    }
    @Override
    public void saveOpinion(ServiceUserOpinionDocument opinionDocument) {
        opinionRepo.save(opinionDocument);
    }
    @Override
    public List<ServiceUserOpinionDocument> getAll() {
        return opinionRepo.findAll();
    }
    @Override
    public List<ServiceUserOpinionDocument> findByFeedbackStatus(String feedbackStatus) {
        return opinionRepo.findByStatusCodeOrderByOpinionTimeDescCreateTimeDesc(feedbackStatus);
    }
    @Override
    public List<ServiceUserOpinionDocument> getByUserId(String userId) {
        return opinionRepo.findByUserIdOrderByOpinionTimeDescCreateTimeDesc(userId);
    }

    @Override
    public List<ServiceUserOpinionDocument> getByUserIdAndFeedbackStatus(String userId, String feedbackStatus) {
        return opinionRepo.findByUserIdAndStatusCodeOrderByOpinionTimeDescCreateTimeDesc(userId, feedbackStatus);
    }
    @Override
    public ServiceUserOpinionDocument getById(String id){
        return opinionRepo.findById(id);
    }

    @Override
    public ServiceFeedbackDocument getFeedBackInfo(String userName, String userTel, String statusCode,
            String opinion, String feedback, String pageNo,  String pageSize) throws Exception {
        // 查询条件
        Query query = new Query();
        // 用户姓名
        if(StringUtil.isNotEmpty(userName)) {
            Pattern ptUserName = Pattern.compile("^.*"+userName+".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("userName").regex(ptUserName));
        }
        // 用户手机号
        if(StringUtil.isNotEmpty(userTel)) {
            Pattern ptUserTel = Pattern.compile("^.*"+userTel+".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("userTel").regex(ptUserTel));
        }
        // 反馈状态
        if(StringUtil.isNotEmpty(statusCode)) {
            query.addCriteria(Criteria.where("statusCode").is(statusCode));
        }
        // 用户意见
        if(StringUtil.isNotEmpty(opinion)) {
            Pattern ptOpinion = Pattern.compile("^.*"+opinion+".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("opinion").regex(ptOpinion));
        }
        // 反馈意见
        if(StringUtil.isNotEmpty(feedback)) {
            Pattern ptFeedback = Pattern.compile("^.*"+feedback+".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("feedback").regex(ptFeedback));
        }
        // 设置按照时间倒序
        query.with(new Sort(Sort.Direction.DESC, "opinionTime").and(new Sort(Sort.Direction.DESC, "createTime")));
        // 设置分页
        // 当前页码
        int iPageNo = Integer.parseInt(pageNo);
        // 每页数量
        int iPageSize = Integer.parseInt(pageSize);
        // 获取总条数
        long opinionCount = this.mongoTemplate.count(query, ServiceUserOpinionDocument.class);
        // 分页页码
        int skip = (iPageNo - 1) * iPageSize;
        // skip相当于从那条记录开始
        query.skip(skip);
        // 从skip开始,取多少条记录
        query.limit(iPageSize);

        ServiceFeedbackDocument feedbackInfo = new ServiceFeedbackDocument();
        // 总条数
        feedbackInfo.setCount((int) opinionCount);
        LOGGER.debug("count:" + feedbackInfo.getCount());
        // 列表数据
        List<ServiceUserOpinionDocument> opinionList = mongoTemplate.find(query, ServiceUserOpinionDocument.class);
        feedbackInfo.setData(opinionList);

        return feedbackInfo;
    }
}
