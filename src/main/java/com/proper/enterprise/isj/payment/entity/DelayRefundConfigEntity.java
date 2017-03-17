package com.proper.enterprise.isj.payment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.entity.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ISJ_PAY_DELAY_CONF")
@Component
@Scope("prototype")
public class DelayRefundConfigEntity extends BaseEntity implements Serializable {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    @Column(length = 500)
    private String text;

    @Column(length = 4000)
    private String description;

    @Column(length = 4000)
    private String howto;

    @JsonProperty("lastModifyUserId")
    @Override
    public String getLastModifyUserId() {
        return lastModifyUserId;
    }

    @JsonProperty("lastModifyTime")
    @Override
    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHowto() {
        return howto;
    }

    public void setHowto(String howto) {
        this.howto = howto;
    }


}
