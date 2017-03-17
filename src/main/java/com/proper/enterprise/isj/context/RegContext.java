package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;

public interface RegContext<T> extends BusinessContext<T> {
    Reg getRecipeOrderDocument();
    void setReg(Reg doc);
}
