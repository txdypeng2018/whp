package com.proper.enterprise.isj.payment.logger;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.remoting.RemoteAccessException;

import java.lang.reflect.InvocationTargetException;

public class LoggerTestAdvice {

    static boolean active;

    public static void setActive(boolean active) {
        LoggerTestAdvice.active = active;
    }
    
    public static boolean getActive(){
        return active;
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

}
