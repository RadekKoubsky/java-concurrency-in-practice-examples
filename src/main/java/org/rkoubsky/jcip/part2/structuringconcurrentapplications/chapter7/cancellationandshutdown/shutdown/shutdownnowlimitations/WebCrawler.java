package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.shutdownnowlimitations;

import net.jcip.annotations.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Using TrackingExecutorService to save unfinished tasks for later execution
 *
 * WebCrawler shows an application of link {@link TrackingExecutor}. The
 * work of a web crawler is often unbounded, so if a crawler must be shut down
 * we might want to save its state so it can be restarted later.
 *
 * CrawlTask provides a getPage method that identifies what page it is working on.
 * When the crawler is shut down, both tasks that did not start and those that
 * were cancelled are scanned and their URLs recorded, so tha page-crawling tasks
 * for those URLs can be added to the queue when the crawler restarts.
 *
 * <NOTE_unavoidable_race_condition>
 *
 *     TrackingExecutor has an unavoidable race condition that could make it
 *     yield false positives: tasks that are identified as cancelled but actually
 *     completed.
 *
 *     This arises because the thread pool could be shut down between when the last
 *     instruction of the task executes and when the pool records the task as complete.
 *
 *     This is not a problem if tasks are idempotent (if performing them twice has
 *     the same effect as performing once), as they typically are in a web crawler.
 *     Otherwise the application retrieving the cancelled tasks must be aware of the
 *     risk and be prepared to deal with false positives.
 *
 * </NOTE_unavoidable_race_condition>
 */
public abstract class WebCrawler {
    private volatile TrackingExecutor exec;
    @GuardedBy("this") private final Set<URL> urlsToCrawl = new HashSet<>();

    private final ConcurrentMap<URL, Boolean> seen = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 500;
    private static final TimeUnit UNIT = MILLISECONDS;

    public WebCrawler(final URL startUrl) {
        this.urlsToCrawl.add(startUrl);
    }

    public synchronized void start() {
        this.exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (final URL url : this.urlsToCrawl) {
            this.submitCrawlTask(url);
        }
        this.urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            this.saveUncrawled(this.exec.shutdownNow());
            if (this.exec.awaitTermination(TIMEOUT, UNIT)) {
                this.saveUncrawled(this.exec.getCancelledTasks());
            }
        } finally {
            this.exec = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    private void saveUncrawled(final List<Runnable> uncrawled) {
        for (final Runnable task : uncrawled) {
            this.urlsToCrawl.add(((CrawlTask) task).getPage());
        }
    }

    private void submitCrawlTask(final URL u) {
        this.exec.execute(new CrawlTask(u));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        CrawlTask(final URL url) {
            this.url = url;
        }

        private int count = 1;

        boolean alreadyCrawled() {
            return WebCrawler.this.seen.putIfAbsent(this.url, true) != null;
        }

        void markUncrawled() {
            WebCrawler.this.seen.remove(this.url);
            System.out.printf("marking %s uncrawled%n", this.url);
        }

        @Override
        public void run() {
            for (final URL link : WebCrawler.this.processPage(this.url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                WebCrawler.this.submitCrawlTask(link);
            }
        }

        public URL getPage() {
            return this.url;
        }
    }
}
