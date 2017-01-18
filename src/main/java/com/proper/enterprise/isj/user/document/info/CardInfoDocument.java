package com.proper.enterprise.isj.user.document.info;

import com.proper.enterprise.isj.user.model.enums.CardTypeEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * 卡信息.
 * Created by think on 2016/8/12 0012.
 */

public class CardInfoDocument extends BaseDocument {

    private CardTypeEnum cardType;

    private String cardCode;

    private String cardNo;

    public CardTypeEnum getCardType() {
        return cardType;
    }

    public void setCardType(CardTypeEnum cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }
}
