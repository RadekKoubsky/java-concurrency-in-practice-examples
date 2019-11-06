package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.findingparalelism.timedtasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Requesting travel quotes under a time budget
 *
 * Depending on the company, fetching a bid might involve invoking a web service,
 * consulting a database, performing an EDI transaction. Rather than have the
 * response time for the page driven by the slowest response, it may be
 * preferable to present only the information available within a given time
 * budget.
 */
public class TravelReservationsPortal {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    public List<TravelQuote> getRankedTravelQuotes(final TravelInfo travelInfo, final Set<TravelCompany> companies,
            final Comparator<TravelQuote> ranking, final long time, final TimeUnit unit)
            throws InterruptedException {
        final List<QuoteTask> tasks = new ArrayList<>();

        /**
         * Fetching a bid from one company is independent of fetching bids from
         * another, so fetching a single bid is a sensible task boundary that
         * allows bid retrieval to proceed concurrently.
         */
        for (final TravelCompany company : companies) {
            tasks.add(new QuoteTask(company, travelInfo));
        }

        /**
         * It would be easy enough to create N tasks, submit them to a thread pool,
         * retain the Futures, and use a timed get to fetch each result sequentially
         * via its Future, but there is an even easier way - invokeAll().
         */
        final List<Future<TravelQuote>> futures = exec.invokeAll(tasks, time, unit);

        /**
         * The invokeAll method takes a collection of tasks and returns a collection
         * of Futures. The two collections have identical structures; invokeAll adds
         * the Futures to the returned collection in order imposed by the task
         * collection's iterator, thus allowing the caller to associate a Future
         * with the Callable it represents.
         */
        final List<TravelQuote> quotes = new ArrayList<>(tasks.size());
        final Iterator<QuoteTask> taskIter = tasks.iterator();

        for (final Future<TravelQuote> f : futures) {
            final QuoteTask task = taskIter.next();
            try {
                quotes.add(f.get());
            } catch (final ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
            } catch (final CancellationException e) {
                quotes.add(task.getTimeoutQuote(e));
            }
        }

        Collections.sort(quotes, ranking);
        return quotes;
    }

}

class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(final TravelCompany company, final TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

    TravelQuote getFailureQuote(final Throwable t) {
        return null;
    }

    TravelQuote getTimeoutQuote(final CancellationException e) {
        return null;
    }

    @Override
    public TravelQuote call() throws Exception {
        return this.company.solicitQuote(this.travelInfo);
    }
}

interface TravelCompany {
    TravelQuote solicitQuote(TravelInfo travelInfo) throws Exception;
}

interface TravelQuote {
}

interface TravelInfo {
}
