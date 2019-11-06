package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.poisonpills;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class IndexerThread extends Thread {
    private BlockingQueue<File> queue;

    public IndexerThread(final BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                final File file = this.queue.take();
                if (file == IndexingService.POISON) {
                    break;
                } else {
                    this.indexFile(file);
                }
            }
        } catch (final InterruptedException consumed) {
        }
    }

    public void indexFile(final File file) {
        /*...*/
    };
}
