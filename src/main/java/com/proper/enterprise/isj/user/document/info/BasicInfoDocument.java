package com.proper.enterprise.isj.user.document.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.user.model.enums.MemberRelationEnum;
import com.proper.enterprise.platform.core.converter.AESStringConverter;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/8/12 0012. 公共信息
 */

public class BasicInfoDocument extends BaseDocument {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 病历号
     */
    private String medicalNum;

    /**
     * 其他卡
     */
    private List<CardInfoDocument> cards = new ArrayList<>();

    /**
     * 是否为就诊人 1:是,0:否
     */
    private String patientVisits = String.valueOf(0);

    /**
     * 与用户关系
     */
    private MemberRelationEnum memberRelation;

    /**
     * 与用户关系
     */
    private String memberCode;

    /**
     * 与用户关系
     */
    private String member;

    /**
     * 性别编码
     */
    private String sexCode;

    /**
     * 性别
     */
    private String sex;

    /**
     * 是否删除  1:是,null:否
     */
    private String deleteStatus;

    /**
     * 年龄
     */
    @Transient
    private Integer age = 0;

    @Transient
    String memberId;

    /**
     * 病历号最新的使用记录
     */
    private Map<String, String> medicalNumMap = new HashMap<>();

    @Transient
    private transient AESStringConverter converter = new AESStringConverter();

    public String getName() {
        return converter.convertToEntityAttribute(name);
    }

    public void setName(String name) {
        this.name = converter.convertToDatabaseColumn(validateName(name));
    }

    private String validateName(String name) {
        return StringUtil.isNull(name) ? "" : name.replaceAll("[^\\u4e00-\\u9fbf\\u002E\\u00B7\\d\\w]", "");
    }

    public List<CardInfoDocument> getCards() {
        return cards;
    }

    public void setCards(List<CardInfoDocument> cards) {
        this.cards = cards;
    }

    public MemberRelationEnum getMemberRelation() {
        return memberRelation;
    }

    public void setMemberRelation(MemberRelationEnum memberRelation) {
        this.memberRelation = memberRelation;
    }

    public String getSexCode() {
        return sexCode;
    }

    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return converter.convertToEntityAttribute(phone);
    }

    public void setPhone(String phone) {
        this.phone = converter.convertToDatabaseColumn(phone);
    }

    public String getIdCard() {
        return converter.convertToEntityAttribute(idCard);
    }

    public void setIdCard(String idCard) {
        this.idCard = converter.convertToDatabaseColumn(idCard);
    }

    public String getMedicalNum() {
        return medicalNum;
    }

    public void setMedicalNum(String medicalNum) {
        this.medicalNum = medicalNum;
    }

    public String getPatientVisits() {
        return patientVisits;
    }

    public void setPatientVisits(String patientVisits) {
        this.patientVisits = patientVisits;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getMemberId() {
        return this.id;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Map<String, String> getMedicalNumMap() {
        return medicalNumMap;
    }

    public void setMedicalNumMap(Map<String, String> medicalNumMap) {
        this.medicalNumMap = medicalNumMap;
    }
}
