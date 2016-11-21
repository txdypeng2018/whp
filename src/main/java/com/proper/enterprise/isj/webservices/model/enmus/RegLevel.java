package com.proper.enterprise.isj.webservices.model.enmus;

public enum RegLevel {

    /**
     * 普通
     */
    NORMAL("1"),

    /**
     * 副教授
     */
    FUJIAOSHOU("2"),

    /**
     * 急诊
     */
    JIAOSHOU("3"),
    /**
     * 名专家15
     */
    MINGZHUANJIA15("4"),
    /**
     * 名专家30
     */
    MINGZHUANJIA30("5"),
    /**
     * 名专家50
     */
    MINGZHUANJIA50("6"),
    /**
     * 急诊
     */
    JIZHEN("7"),
    /**
     * 开药
     */
    KAIYAO("8"),
    /**
     * 老年证普通
     */
    LAONIANZHENGPUTONG("9"),
    /**
     * 副教授咨询
     */
    FUJIAOSHOUZIXUN("A"),
    /**
     * 教授咨询
     */
    JIAOSHOUZIXUN("B"),
    /**
     * 老年证副教
     */
    LAONIANZHENGFUJIAO("C"),
    /**
     * 干诊(老年证)
     */
    GANZHEN_LAONIANZHENG("D"),
    /**
     * 疗程内挂号
     */
    LIAOCHENGNEIGUAHAO("E"),
    /**
     * 透析医保
     */
    TOUXIYIBAO("F"),
    /**
     * 老年证正教
     */
    LAONIANZHENGZHENGJIAO("G"),
    /**
     * 普通(特困减)
     */
    PUTONG_TEKUNJIAN("H"),
    /**
     * 副教(特困减)
     */
    FUJIAO_TEKUNJIAN("I"),
    /**
     * 教授(特困减)
     */
    JIAOSHOU_TEKUNJIAN("J"),
    /**
     * 急诊(特困减)
     */
    JIZHEN_TEKUNJIAN("K"),
    /**
     * 婴儿结石
     */
    YINGERJIESHI("L"),
    /**
     * vip特需服务
     */
    VIP_TEXUFUWU("M"),
    /**
     * VIP特需复诊
     */
    VIP_TEXUFUZHEN("N"),
    /**
     * 省级干诊
     */
    SHENGJIGANZHEN("O"),
    /**
     * 婴儿结石正
     */
    YINGERJIESHIZHENG("P"),
    /**
     * 婴儿结石副
     */
    YINGERJIESHIFU("Q"),
    /**
     * 奶粉(咨询)
     */
    NAIFEN_ZIXUN("R"),
    /**
     * 奶粉副(咨询)
     */
    NAIFENFU_ZIXUN("S"),
    /**
     * 奶粉正(咨询)
     */
    NAIFENZHENG_ZIXUN("T"),
    /**
     * 老年证名专家
     */
    LAONIANZHENGMINGZHUANJIA("U"),
    /**
     * 老年证VIP
     */
    LAONIANZHENGVIP("V"),
    /**
     * 院工离休
     */
    YUANGONGLIXIU("W"),
    /**
     * 老年正教咨询
     */
    LAONIANZHENGJIAOZIXUN("X"),
    /**
     * 老年副教咨询
     */
    LAONIANFUJIAOZIXUN("Y"),
    /**
     * 特需服务
     */
    TEXUFUWU("Z");

    private String code;

    RegLevel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RegLevel codeOf(String code) {
        for (RegLevel regLevel : values()) {
            if (regLevel.getCode().equals(code)) {
                return regLevel;
            }
        }
        return null;
    }
}
