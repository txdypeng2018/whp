package com.proper.enterprise.isj.webservices.document;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "WS_LOG")
public class WSLogDocument extends BaseDocument {

    /**
     * 接口 function code
     */
    @Field("C")
    private String funCode;

    /**
     * 方法名称
     */
    @Field("M")
    private String methodName;

    /**
     * 传入参数
     */
    @Field("P")
    private Map<String, Object> param;

    /**
     * 请求字符串
     */
    @Field("Q")
    private String req;

    /**
     * 响应字符串
     */
    @Field("S")
    private String res;

    /**
     * 调用接口持续时间，单位毫秒
     */
    @Field("D")
    private long duration;

    public WSLogDocument(String funCode, String methodName, Map<String, Object> param,
                         String req, String res, long duration) {
        this.funCode = funCode;
        this.methodName = methodName;
        this.param = param;
        this.req = req;
        this.res = res;
        this.duration = duration;
    }

    public String getFunCode() {
        return funCode;
    }

    public void setFunCode(String funCode) {
        this.funCode = funCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
