package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.lifetimeinmethodcalll;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Using a private \Executor whose lifetime is bounded by a method call
 *
 * If a method needs to process a batch of tasks and does not return untill
 * all the tasks are finished, it can simplify service lifecycle management
 * by using a private Executor whose lifetime is bounded by that method.
 */
public class CheckForMail {
    public boolean checkMail(final Set<String> hosts, final long timeout, final TimeUnit unit) throws InterruptedException {
        final ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (final String host : hosts) {
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (CheckForMail.this.checkMail(host)) {
                            hasNewMail.set(true);
                        }
                    }
                });
            }
        } finally {
            exec.shutdown();
            exec.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }

    private boolean checkMail(final String host) {
        // Check for mail
        return false;
    }
}
