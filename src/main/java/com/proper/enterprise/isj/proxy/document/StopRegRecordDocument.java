package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by think on 2016/10/7 0007.
 */
@Document(collection = "stopreg_record")
public class StopRegRecordDocument extends BaseDocument {

    /**
     * 停诊日期
     */
    private String stopDate;

    /**
     * 停诊拼接字符串
     */
    private String stopReg;

    /**
     * 停诊需通知人数
     */
    private int noticeMsgNum;

    public String getStopDate() {
        return stopDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public String getStopReg() {
        return stopReg;
    }

    public void setStopReg(String stopReg) {
        this.stopReg = stopReg;
    }

    public int getNoticeMsgNum() {
        return noticeMsgNum;
    }

    public void setNoticeMsgNum(int noticeMsgNum) {
        this.noticeMsgNum = noticeMsgNum;
    }
}
