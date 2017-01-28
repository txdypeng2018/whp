package com.proper.enterprise.isj.user.model.enums;

/**
 * Created by think on 2016/8/12 0012. 家庭成员关系
 */
public enum MemberRelationEnum {

    /**
     * 我
     */
    ME("00"),

    /**
     * 父亲
     */
    FATHER("01"),
    /**
     * 母亲
     */
    MOTHER("02"),
    /**
     * 配偶
     */
    MATE("03"),

    /**
     * 儿子
     */
    SON("04"),

    /**
     * 女儿
     */
    DAUGHTER("05"),
    /**
     * 兄弟
     */
    BROTHER("06"),
    /**
     * 姐妹
     */
    SISTER("07"),

    /**
     * 亲属
     */
    KIN("08"),
    /**
     * 朋友
     */
    FRIEND("09"),
    /**
     * 其他
     */
    OTHER("10");

    private String value;

    MemberRelationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
