package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.findingparalelism.timedtasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Fetching an advertisement with a time budget
 *
 * RenderWithTimeBudget shows a typical application of a timed Future.get.
 * It generates a composite web page that contains the requested content plus
 * an advertisement fetched from and ad server. It submits the ad-fetching task
 * to an executor, computes the rest of the page content, and then waits for the
 * ad until its time budget runs out (all the timed methods in java.concurrent package
 * treat negative timeouts as zero).
 *
 * If the "get" times out, it cancels the ad-fetching task and uses a default
 * advertisement instead.
 */
public class RenderWithTimeBudget {
    private static final Ad DEFAULT_AD = new Ad();
    private static final long TIME_BUDGET = 1000;
    private static final ExecutorService exec = Executors.newCachedThreadPool();

    Page renderPageWithAd() throws InterruptedException {
        final long endNanos = System.nanoTime() + TIME_BUDGET;
        final Future<Ad> f = exec.submit(new FetchAdTask());
        // Render the page while waiting for the ad
        final Page page = this.renderPageBody();
        Ad ad;
        try {
            // Only wait for the remaining time budget
            final long timeLeft = endNanos - System.nanoTime();

            /**
             * The timed version of "Future.get" supports the following
             * requirement: it returns as soon as it is ready, but throws
             * TimeoutException if the result is not ready within the timeout
             * period.
             */
            ad = f.get(timeLeft, NANOSECONDS);
        } catch (final ExecutionException e) {
            ad = DEFAULT_AD;
        }
        /**
         * A secondary problem when using timed tasks is to stop them
         * when they run out of time, so they do not waste computing
         * resources by continuing to compute a result that will not
         * be used. If a timed "get" method completes with a TimeoutException,
         * you can cancel the task through the Future.
         */
        catch (final TimeoutException e) {
            ad = DEFAULT_AD;
            // cancel the task as it has not been completed in time ->
            // The timed Future.get threw TimeoutException
            f.cancel(true);
        }
        page.setAd(ad);
        return page;
    }

    Page renderPageBody() { return new Page(); }


    static class Ad {
    }

    static class Page {
        public void setAd(final Ad ad) { }
    }

    static class FetchAdTask implements Callable<Ad> {
        @Override
        public Ad call() {
            return new Ad();
        }
    }

}
