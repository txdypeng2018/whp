package com.proper.enterprise.isj.business;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.proper.enterprise.isj.context.RepositoryOperationContext;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
@Scope("prototype")
public class RepositoryBusiness<T, R, M extends RepositoryOperationContext<T, R>> implements IBusiness<T, M> {

    @Autowired
    WebApplicationContext wac;

    @SuppressWarnings({ "unchecked" })
    @Override
    public void process(M ctx) throws Exception {
        Object repo = ctx.getRepository();

        if (repo == null) {
            repo = wac.getBean(ctx.getRepositoryType());
        }

        Class<? extends Object> clz = repo.getClass();
        String methodName = ctx.getMethodName();
        Object[] params = ctx.getRepositoryOperateParams();
        Method[] methods = clz.getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] pts = method.getParameterTypes();
                boolean allPass = true;
                int len = pts.length;
                for (int i = 0; i < len; i++) {
                    allPass &= pts[i].isInstance(params[i]);
                    if (!allPass) {
                        break;
                    }
                }
                if (allPass) {
                    targetMethod = method;
                    break;
                }
            }
        }

        if (targetMethod != null) {
                Object o = targetMethod.invoke(repo, params == null ? new Object[0] : params);
                ctx.setRepositoryOperationResult((R) o);
        }

    }

}
