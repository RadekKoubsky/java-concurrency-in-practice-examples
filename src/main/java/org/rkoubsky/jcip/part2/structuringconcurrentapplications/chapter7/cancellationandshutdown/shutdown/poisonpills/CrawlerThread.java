package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.poisonpills;

import java.io.File;
import java.io.FileFilter;

public class CrawlerThread extends Thread {
    private IndexingService indexingService;
    private File root;
    private FileFilter fileFilter;

    public CrawlerThread(final IndexingService indexingService, final File root, final FileFilter fileFilter) {
        this.indexingService = indexingService;
        this.root = root;
        this.fileFilter = fileFilter;
    }

    @Override
    public void run() {
        try {
            this.crawl(this.root);
        } catch (final InterruptedException e) { /* fall through */
        } finally {
            while (true) {
                try {
                    this.indexingService.getQueue().put(IndexingService.POISON);
                    break;
                } catch (final InterruptedException e1) { /* retry */
                }
            }
        }
    }

    private void crawl(final File root) throws InterruptedException {
        final File[] entries = root.listFiles(this.fileFilter);
        if (entries != null) {
            for (final File entry : entries) {
                if (entry.isDirectory()) {
                    this.crawl(entry);
                } else if (!this.indexingService.alreadyIndexed(entry)) {
                    this.indexingService.getQueue().put(entry);
                }
            }
        }
    }
}
