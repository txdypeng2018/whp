package com.proper.enterprise.isj.app.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "APP_VERSIONS")
public class AppVersionDocument extends BaseDocument {


    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 版本号
     */
    private int ver;

    /**
     * app 下载地址
     */
    private String url;

    /**
     * 版本说明
     */
    private String note;

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
