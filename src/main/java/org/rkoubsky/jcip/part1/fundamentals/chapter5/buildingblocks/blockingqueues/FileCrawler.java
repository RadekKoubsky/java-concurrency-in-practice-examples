package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.blockingqueues;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * FileCrawler shows a producer task that searches a file hierarchy
 * for files meeting an indexing criterion and put their names
 * on the work queue.
 */
public class FileCrawler  implements Runnable {
        private final BlockingQueue<File> fileQueue;
        private final FileFilter fileFilter;
        private final File root;

        public FileCrawler(final BlockingQueue<File> fileQueue,
                final FileFilter fileFilter,
                final File root) {
            this.fileQueue = fileQueue;
            this.root = root;
            this.fileFilter = new FileFilter() {
                @Override
                public boolean accept(final File f) {
                    return f.isDirectory() || fileFilter.accept(f);
                }
            };
        }

        private boolean alreadyIndexed(final File f) {
            return false;
        }

        @Override
        public void run() {
            try {
                this.crawl(this.root);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void crawl(final File root) throws InterruptedException {
            final File[] entries = root.listFiles(this.fileFilter);
            if (entries != null) {
                for (final File entry : entries) {
                    if (entry.isDirectory()) {
                        this.crawl(entry);
                    } else if (!this.alreadyIndexed(entry)) {
                        this.fileQueue.put(entry);
                    }
                }
            }
        }
}
