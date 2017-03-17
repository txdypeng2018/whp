package com.proper.enterprise.isj.business;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.proper.enterprise.platform.core.api.BusinessContext;

@Component
@Qualifier("defaultBusinessToolkit")
@Scope("prototype")
public class DefaultBusinessToolkit extends AbstractBusinessToolkit{

    
    /* (non-Javadoc)
     * @see com.proper.enterprise.isj.business.BusinessToolkit#execute(java.lang.String, com.proper.enterprise.isj.support.business.BusinessContext)
     */
    @Override
    public <T> T execute(String id, BusinessContext<T> ctx){
        try {
            return execute(Class.forName(id), ctx);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    

}
