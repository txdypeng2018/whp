package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import com.proper.enterprise.platform.core.utils.DateUtil;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户意见
 */
@Document(collection = "service_user_opinion")
public class ServiceUserOpinionDocument extends BaseDocument {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户意见
     */
    private String opinionTime;
    /**
     * 用户意见
     */
    private String opinion;
    /**
     * 反馈时间
     */
    private String feedbackTime;
    /**
     * 反馈内容
     */
    private String feedback;
    /**
     * 反馈状态编码
     */
    private String statusCode;
    /**
     * 反馈状态名称
     */
    private String status;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 用户手机号
     */
    private String userTel;

    private String createTime = DateUtil.getTimestamp(true);

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpinionTime() {
        return opinionTime;
    }

    public void setOpinionTime(String opinionTime) {
        this.opinionTime = opinionTime;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(String feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    @Override
    public String getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
