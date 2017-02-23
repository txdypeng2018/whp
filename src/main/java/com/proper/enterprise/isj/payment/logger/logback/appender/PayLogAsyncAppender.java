package com.proper.enterprise.isj.payment.logger.logback.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 支付中使用的异步日志.
 * <p>
 * 增加了退出时等待日志完成的机制,应该避免使用kill -9 &lt;pid&gt;的方式退出应用;<br/>
 * 调整了舍弃是的日志级别，见{@link #isDiscardable(ILoggingEvent)}.
 * </p>
 *
 * @author 王东石.
 * @since 0.1.0
 */
public class PayLogAsyncAppender extends AsyncAppender {

    /**
     * 记录异步Appender的实例列表，用于系统退出时的检查异步日志是否写入完成.
     *
     * @since 0.1.0
     */
    private static final Collection<PayLogAsyncAppender> INSTANCES = new LinkedList<>();

    /*
     * 添加退出钩子。在系统退出的时候查看尚未处理的日志数量，如果仍然 有未处理的日志，等待1秒后再检查，直到所有日志均被处理。
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                while (true) {
                    if (getNumberOfElementInAllQueue() > 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (Throwable t) {
                            //进入下一次循环，不用记录
                        }
                    } else {
                        break;
                    }
                }
            }
        });
    }

    /**
     * 获得未处理的异步日志数量.
     *
     * @return 未处理的异步日志数量.
     * @since 0.1.0
     */
    public static int getNumberOfElementInAllQueue() {
        int notWritedLogCount = 0;
        for (PayLogAsyncAppender cur : INSTANCES) {
            notWritedLogCount += cur.getNumberOfElementsInQueue();
        }
        return notWritedLogCount;
    }

    /*
     * 将每个异步日志Appender的实例记录到实例列表中，以便退出时检查.
     */ {
        INSTANCES.add(this);
    }

    /**
     * 判断是否舍弃当前消息.
     * <p>
     * 原有的判断规则时在队列满80%时舍弃TRACE、DEBUG和INFO。但是支付相关日志用于
     * 记录重要日志，不适合舍弃，切记录内容大部分为正常业务，而非异常情况，所以记录
     * 到WARN或以上级别也不合适，而应使用INFO作为日志级别。这样就需要将基类中的舍弃 级别调整到INFO以下，保留INFO级别的日志输出。
     * </p>
     *
     * @param event 日志消息.
     * @return true if the event is of level TRACE, DEBUG; false otherwise.
     * @see AsyncAppender#isDiscardable(ILoggingEvent)
     * @since 0.1.0
     */
    @Override
    protected boolean isDiscardable(ILoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() < Level.INFO_INT;
    }

}
