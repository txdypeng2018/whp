package com.proper.enterprise.isj.user.document.info;

import org.springframework.data.mongodb.core.mapping.Document;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 家庭成员.
 * Created by think on 2016/8/12 0012.
 */
@Document
public class FamilyMemberInfoDocument extends BasicInfoDocument {

    /**
     *
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    // /**
    // * 与用户关系
    // */
    // private MemberRelationEnum memberRelation;
    //
    //
    //
    // public MemberRelationEnum getMemberRelation() {
    // return memberRelation;
    // }
    //
    // public void setMemberRelation(MemberRelationEnum memberRelation) {
    // this.memberRelation = memberRelation;
    // }
}
