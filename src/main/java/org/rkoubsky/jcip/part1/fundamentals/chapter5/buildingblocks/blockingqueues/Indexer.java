package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.blockingqueues;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Indexer shows a consumer task that takes file names
 * from the queue and indexes them.
 */
public class Indexer implements Runnable {
    private final BlockingQueue<File> queue;

    public Indexer(final BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            /**
             * This thread never exits which prevents the program
             * from terminating.
             */
            while (true) {
                this.indexFile(this.queue.take());
            }
        } catch (final InterruptedException e) {
            /**
             * NOTE: Restore the interrupt
             *
             * Sometimes you cannot throw InterruptedException, for
             * instance when your code is part of Runnable. In these
             * situations you must catch InterruptedException and restore
             * the interrupted status by calling interrupt on the current
             * thread, so that code higher up the call stack can see that an
             * interrupt was issued.
             *
             * Example from stackoverflow: https://stackoverflow.com/questions/4906799/why-invoke-thread-currentthread-interrupt-in-a-catch-interruptexception-block
             */
            Thread.currentThread().interrupt();
        }
    }

    public void indexFile(final File file) {
        // Index the file...
    }
}
