package com.proper.enterprise.isj.proxy.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * Created by think on 2016/8/16 0016. 学科
 */
public class SubjectDocument implements Serializable {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 学科Id
     */
    private String id;

    /**
     * 学科名称
     */
    private String name;

    /**
     * 下级学科
     */
    private List<SubjectDocument> subjects = new ArrayList<>();

    public SubjectDocument() { }

    public SubjectDocument(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SubjectDocument(String id, String name, List<SubjectDocument> list) {
        this.id = id;
        this.name = name;
        this.subjects = list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubjectDocument> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDocument> subjects) {
        this.subjects = subjects;
    }
}
