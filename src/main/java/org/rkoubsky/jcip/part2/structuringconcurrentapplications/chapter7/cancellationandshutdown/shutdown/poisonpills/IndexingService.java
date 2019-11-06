package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.poisonpills;

import org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.blockingqueues.ProducerConsumer;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Shutdown with poison pill
 *
 * Another way to convince producer-consumer service to shut down is
 * with a poison pill: a recognizable object placed on the queue that
 * means "when you get this, stop".
 *
 * IndexingService shows a single-producer, single-consumer version of
 * the desktop search application in link {@link ProducerConsumer}
 *
 * Poison pills work only when the number of producers and consumers is
 * known.
 *
 * The approach in IndexingService can be extended to multiple producers
 * by having each producer place a pill on the queue and having the consumer
 * stop when it receives N_producers pills.
 *
 * It can be extended to multiple consumers by having each producer place
 * N_consumers pills on the queue, tough this can get unwieldy with large
 * number of producers and consumers.
 *
 * Poison pills work reliable only with unbounded queues.
 */
public class IndexingService {
    private static final int CAPACITY = 1000;
    public static final File POISON = new File("");
    private final IndexerThread consumer;
    private final CrawlerThread producer;
    private final BlockingQueue<File> queue = new LinkedBlockingQueue<File>(CAPACITY);

    public IndexingService(final File root, final FileFilter fileFilter) {
        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || fileFilter.accept(f);
            }
        };
        this.producer = new CrawlerThread(this, root, fileFilter);
        this.consumer = new IndexerThread(this.queue);
    }

    public BlockingQueue<File> getQueue() {
        return this.queue;
    }

    public boolean alreadyIndexed(final File f) {
        return false;
    }

    public void start() {
        this.producer.start();
        this.consumer.start();
    }

    public void stop() {
        this.producer.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        this.consumer.join();
    }
}
