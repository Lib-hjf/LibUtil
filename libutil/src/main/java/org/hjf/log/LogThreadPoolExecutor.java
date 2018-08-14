package org.hjf.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 按顺序一个一个执行任务的线程池，用于执行依赖顺序正确执行的【调试】任务。
 */
final class LogThreadPoolExecutor extends ThreadPoolExecutor {

    private static LogThreadPoolExecutor fifoPoolExecutor;

    private LogThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    static LogThreadPoolExecutor getInstance() {
        if (LogThreadPoolExecutor.fifoPoolExecutor == null) {
            synchronized (LogThreadPoolExecutor.class) {
                if (LogThreadPoolExecutor.fifoPoolExecutor == null) {
                    //核心线程 == 最大线程 == 1，无需回收多余线程操作，减少回收检测将保持活性时间设置为：Integer.MAX_VALUE
                    LogThreadPoolExecutor.fifoPoolExecutor = new LogThreadPoolExecutor(
                            // 核心线程数，即使空闲也存活
                            1,
                            // 最大线程数
                            1,
                            // 保持活性时间，不会产生多余的线程，0 即可
                            0,
                            // 保持活性时间单位
                            TimeUnit.SECONDS,
                            // 任务队列模式，这里是链表顺序模式，数量设置为 Integer.MAX_VALUE
                            new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE),
                            // 线程工厂类
                            Executors.defaultThreadFactory(),
                            // 拒绝策略，使用默认的抛出异常
                            new AbortPolicy()
                    );
                }
            }
        }
        return LogThreadPoolExecutor.fifoPoolExecutor;
    }

    static void destroy() {
        if (LogThreadPoolExecutor.fifoPoolExecutor != null) {
            synchronized (LogThreadPoolExecutor.class) {
                if (LogThreadPoolExecutor.fifoPoolExecutor != null) {
                    LogThreadPoolExecutor.fifoPoolExecutor.shutdownNow();
                    LogThreadPoolExecutor.fifoPoolExecutor = null;
                }
            }
        }
    }
}
