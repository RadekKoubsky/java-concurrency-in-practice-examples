package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.threadfactories;

import java.util.concurrent.ThreadFactory;

/**
 * Custom thread factory
 *
 * It instantiates a new MyAppThread, passing a pool-specific name to
 * the constructor so that threads from each pool can be distinguished
 * in thread dumps and error logs.
 */
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(final String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        return new MyAppThread(runnable, this.poolName);
       }
}
