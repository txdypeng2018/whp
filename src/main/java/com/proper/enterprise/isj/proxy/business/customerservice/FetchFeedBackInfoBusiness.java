package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FeedbackContext;
import com.proper.enterprise.isj.context.OpinionContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.StatusCodeContext;
import com.proper.enterprise.isj.context.UserNameContext;
import com.proper.enterprise.isj.context.UserTelContext;
import com.proper.enterprise.isj.proxy.document.ServiceFeedbackDocument;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchFeedBackInfoBusiness<M extends UserNameContext<ServiceFeedbackDocument> & UserTelContext<ServiceFeedbackDocument> & StatusCodeContext<ServiceFeedbackDocument> & OpinionContext<ServiceFeedbackDocument> & FeedbackContext<ServiceFeedbackDocument> & PageNoContext<ServiceFeedbackDocument> & PageSizeContext<ServiceFeedbackDocument> & ModifiedResultBusinessContext<ServiceFeedbackDocument>>
        implements IBusiness<ServiceFeedbackDocument, M>, ILoggable {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(getFeedBackInfo(ctx.getUserName(), ctx.getUserTel(), ctx.getStatusCode(), ctx.getOpinion(),
                ctx.getFeedback(), ctx.getPageNo(), ctx.getPageSize()));
    }

    public ServiceFeedbackDocument getFeedBackInfo(String userName, String userTel, String statusCode, String opinion,
            String feedback, int pageNo, int pageSize) throws Exception {
        // 查询条件
        Query query = new Query();
        // 用户姓名
        if (StringUtil.isNotEmpty(userName)) {
            Pattern ptUserName = Pattern.compile("^.*" + userName + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("userName").regex(ptUserName));
        }
        // 用户手机号
        if (StringUtil.isNotEmpty(userTel)) {
            Pattern ptUserTel = Pattern.compile("^.*" + userTel + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("userTel").regex(ptUserTel));
        }
        // 反馈状态
        if (StringUtil.isNotEmpty(statusCode)) {
            query.addCriteria(Criteria.where("statusCode").is(statusCode));
        }
        // 用户意见
        if (StringUtil.isNotEmpty(opinion)) {
            Pattern ptOpinion = Pattern.compile("^.*" + opinion + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("opinion").regex(ptOpinion));
        }
        // 反馈意见
        if (StringUtil.isNotEmpty(feedback)) {
            Pattern ptFeedback = Pattern.compile("^.*" + feedback + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("feedback").regex(ptFeedback));
        }
        // 设置按照时间倒序
        query.with(new Sort(Sort.Direction.DESC, "opinionTime").and(new Sort(Sort.Direction.DESC, "createTime")));
        // 设置分页
        // 当前页码
        int iPageNo = pageNo;
        // 每页数量
        int iPageSize = pageSize;
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
        debug("count:" + feedbackInfo.getCount());
        // 列表数据
        List<ServiceUserOpinionDocument> opinionList = mongoTemplate.find(query, ServiceUserOpinionDocument.class);
        feedbackInfo.setData(opinionList);

        return feedbackInfo;
    }

}
