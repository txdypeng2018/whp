package com.proper.enterprise.isj.context;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;

public interface TimeRegsContext<T> extends BusinessContext<T> {
    Collection<TimeReg> getTimeRegs();

    void setTimeRegs(Collection<TimeReg> timeRegs);
}
