package com.proper.enterprise.isj.context;

import java.util.Date;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DateObjContext<T> extends BusinessContext<T> {
    
    Date getDateObj();
    void setDateObj(Date date);

}
