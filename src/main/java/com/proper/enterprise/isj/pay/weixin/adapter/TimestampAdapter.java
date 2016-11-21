package com.proper.enterprise.isj.pay.weixin.adapter;

import com.proper.enterprise.isj.pay.weixin.constants.WeixinConstants;
import com.proper.enterprise.platform.core.utils.DateUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class TimestampAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String v) throws Exception {
        return DateUtil.toDate(v, WeixinConstants.TIME_FORMAT);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return DateUtil.toString(v, WeixinConstants.TIME_FORMAT);
    }

}
