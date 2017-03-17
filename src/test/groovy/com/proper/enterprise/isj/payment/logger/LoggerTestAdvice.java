package com.proper.enterprise.isj.payment.logger;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.user.service.impl.custom.UserInfoWebServiceClientCustom;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.remoting.RemoteAccessException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoggerTestAdvice {

    static boolean active;

    private static boolean useAsUserInfoWebServiceClientCustom;

    public static void setAsUserInfoWebServiceClientCustom(boolean val) {
        useAsUserInfoWebServiceClientCustom = val;
    }

    public static void setActive(boolean active) {
        LoggerTestAdvice.active = active;
    }

    public Object forTest(ProceedingJoinPoint pjp) throws Throwable {
        if (active) {
            throw new InvocationTargetException(new RemoteAccessException(""));
        } else {
            return pjp.proceed();
        }
    }

    public Object forFindByOrderNo(ProceedingJoinPoint pjp) throws Throwable {
        if (active) {
            OrderEntity res = new OrderEntity();
            return res;
        } else {
            return pjp.proceed();
        }
    }

    public Object useWebClientAsUserInfoWebServiceClientCustom(ProceedingJoinPoint pjp) throws Throwable {
        if (useAsUserInfoWebServiceClientCustom) {
            UserInfoWebServiceClientCustom target = new UserInfoWebServiceClientCustom();
            Signature sig = pjp.getSignature();
            MethodSignature msig = null;
            if (!(sig instanceof MethodSignature)) {
                throw new IllegalArgumentException("该注解只能用于方法");
            }
            msig = (MethodSignature) sig;
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            return currentMethod.invoke(target, pjp.getArgs());
        } else {
            return pjp.proceed();
        }
    }

}
