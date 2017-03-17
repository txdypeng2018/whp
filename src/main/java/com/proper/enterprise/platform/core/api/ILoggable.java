package com.proper.enterprise.platform.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可记录日志的接口.
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
public interface ILoggable {
    /**
     * 默认日志对象.
     * <p>
     * 获得名为"root"的日志对象.
     * </p>
     */
    static final Logger LOGGER = LoggerFactory.getLogger("root");
    
    /**
     * 以当前对象的class作为参数获得相应的日志对象.
     * @return 日志对象.
     */
    default Logger findLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
    
    /**
     * 记录跟踪信息.
     * @param msg 信息.
     */
    default void trace(String msg){
        trace(findLogger(), msg);
    }
    
    /**
     * 记录跟踪信息.
     * @param msg 信息.
     * @param t 异常或错误.
     */
    default void trace(String msg, Throwable t){
        trace(findLogger(), msg, t);
    }
    
    default void trace(String format, Object ... args){
        trace(findLogger(), format, args);
    }
    
    default void debug(String msg){
        debug(findLogger(), msg);
    }
    
    default void debug(String msg, Throwable t){
        debug(findLogger(), msg, t);
    }
    
    default void debug(String format, Object ... args){
        debug(findLogger(), format, args);
    }
    
    default void info(String msg){
        info(findLogger(), msg);
    }
    
    default void info(String msg, Throwable t){
        info(findLogger(), msg, t);
    }
    
    default void info(String format, Object ... args){
        info(findLogger(), format, args);
    }
    
    default void warn(String msg){
        warn(findLogger(), msg);
    }
    
    default void warn(String msg, Throwable t){
        warn(findLogger(), msg, t);
    }
    
    default void warn(String format, Object ... args){
        warn(findLogger(), format, args);
    }
    
    default void error(String msg){
        error(findLogger(), msg);
    }
    
    default void error(String msg, Throwable t){
        error(findLogger(), msg, t);
    }
    
    default void error(String format, Object ... args){
        error(findLogger(), format, args);
    }
    
    default void fatal(String msg){
        fatal(findLogger(), msg);
    }
    
    default void fatal(String msg, Throwable t){
        fatal(findLogger(), msg, t);
    }
    
    default void fatal(String format, Object ... args){
        fatal(findLogger(), format, args);
    }
    
    static void trace(Logger logger, String msg, Throwable t){
        logger.trace(msg, t);
    }
    static void debug(Logger logger, String msg, Throwable t){
        logger.debug(msg, t);
    }
    static void info(Logger logger, String msg, Throwable t){
        logger.info(msg, t);
    }
    static void warn(Logger logger, String msg, Throwable t){
        logger.warn(msg, t);
    }
    static void error(Logger logger, String msg, Throwable t){
        logger.error(msg, t);
    }
    static void fatal(Logger logger, String msg, Throwable t){
        logger.error(msg, t);
    }
    
    static void trace(Logger logger, String format, Object ... params){
        logger.trace(format, params);
    }
    static void debug(Logger logger, String format, Object ... params){
        logger.debug(format, params);
    }
    static void info(Logger logger, String format, Object ... params){
        logger.info(format, params);
    }
    static void warn(Logger logger, String format, Object ... params){
        logger.warn(format, params);
    }
    static void error(Logger logger, String format, Object ... params){
        logger.error(format, params);
    }
    static void fatal(Logger logger, String format, Object ... params){
        logger.error(format, params);
    }
    
}
