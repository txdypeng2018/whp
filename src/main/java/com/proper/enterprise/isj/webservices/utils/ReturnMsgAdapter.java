package com.proper.enterprise.isj.webservices.utils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class ReturnMsgAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return TRANSLATOR.get(v);
    }

    @Override
    public String marshal(String v) throws Exception {
        return null;
    }

    private static final Map<String, String> TRANSLATOR = new HashMap<>();

    static {
        TRANSLATOR.put("200101", "科室不存在，未查询到科室记录");

        TRANSLATOR.put("200201", "科室不存在");
        TRANSLATOR.put("200202", "医生不存在，未查询到医生记录");

        TRANSLATOR.put("200301", "科室不存在");
        TRANSLATOR.put("200302", "医生不存在");
        TRANSLATOR.put("200303", "排班不存在，未查询到排班信息");

        TRANSLATOR.put("200701", "医院挂号规则限制");
        TRANSLATOR.put("200702", "不符合科室挂号规则");
        TRANSLATOR.put("200703", "用户建档失败");
        TRANSLATOR.put("200704", "院内多个用户档案，请先联系医院处理");
        TRANSLATOR.put("200705", "无效的排班");
        TRANSLATOR.put("200706", "科室不存在");
        TRANSLATOR.put("200707", "医生不存在");
        TRANSLATOR.put("200708", "平台订单号已存在");
        TRANSLATOR.put("200709", "该排班挂号已满（无剩余号源）");
        TRANSLATOR.put("200710", "该排班下的当前号源已被占用");
        TRANSLATOR.put("200711", "就诊人身份信息和医院档案不匹配");
        TRANSLATOR.put("200712", "用户卡信息和医院档案不匹配");

        TRANSLATOR.put("300101", "缴费记录不存在，未查询到缴费订单记录");
        TRANSLATOR.put("300102", "用户不存在");

        TRANSLATOR.put("300201", "无效的缴费记录");

        TRANSLATOR.put("300301", "缴费订单不存在");
        TRANSLATOR.put("300302", "缴费订单已支付");
        TRANSLATOR.put("300303", "缴费订单已关闭");
        TRANSLATOR.put("300304", "缴费金额不正确");
    }

}
