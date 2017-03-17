package com.proper.enterprise.isj.proxy.document.recipe;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/9/13 0013.
 */
public class RecipeDetailItemDocument extends BaseDocument {

    /**
     *
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 名称
     */
    private String name;

    /**
     * 单价
     */
    private String amount;

    /**
     * 数量
     */
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
