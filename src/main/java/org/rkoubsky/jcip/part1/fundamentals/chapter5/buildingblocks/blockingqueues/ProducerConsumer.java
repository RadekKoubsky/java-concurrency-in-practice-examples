package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.blockingqueues;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The producer-consumer pattern offers a thread-friendly means of
 * decomposing the desktop search problem into smaller components.
 *
 * Factoring file-crawling and indexing into separate activities results
 * in code that is more readable and reusable than a monolithic activity
 * that does both; each of the activities has only a single task to do, and
 * the blocking queue handles all the flow control, so the code for each is
 * simpler and clearer.
 */
public class ProducerConsumer {
    private static final int BOUND = 10;
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();

    public static void startIndexing(final File[] roots) {
        final BlockingQueue<File> queue = new LinkedBlockingQueue<File>(BOUND);
        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return true;
            }
        };


        for (final File root : roots) {
            new Thread(new FileCrawler(queue, filter, root)).start();
        }

        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }
}
