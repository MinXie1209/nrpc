package org.example.nrpc.proxy;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 江南小俊
 * @since 2021/7/6
 **/
public class ProxyExecutor {
    private final static ProxyExecutor instance = new ProxyExecutor();
    private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
            ProxyExecutor.defaultThreadFactory());

    private ProxyExecutor() {
    }

    public static ProxyExecutor newInstance() {
        return instance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static ThreadFactory defaultThreadFactory() {
        return new ProxyExecutor.DefaultThreadFactory();
    }
}
