package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.threadfactories;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom thread base class that provides interesting customizations.
 *
 * It lets you provide a thread name, sets a custom link {@link UncaughtExceptionHandler}
 * that writes a message to a Logger, maintains statistics on how many
 * threads have been created and destroyed, and optionally writes
 * a debug message to the log when a thread is created or terminates.
 */
public class MyAppThread extends Thread {
    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();

    public MyAppThread(final Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(final Runnable runnable, final String name) {
        super(runnable, name + "-" + created.incrementAndGet());
        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t,
                    final Throwable e) {
                log.log(Level.SEVERE,
                        "UNCAUGHT in thread " + t.getName(), e);
            }
        });
    }

    @Override
    public void run() {
        // Copy debug flag to ensure consistent value throughout.
        final boolean debug = debugLifecycle;
        if (debug) {
            log.log(Level.FINE, "Created " + this.getName());
        }
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) {
                log.log(Level.FINE, "Exiting " + this.getName());
            }
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebug(final boolean b) {
        debugLifecycle = b;
    }
}
