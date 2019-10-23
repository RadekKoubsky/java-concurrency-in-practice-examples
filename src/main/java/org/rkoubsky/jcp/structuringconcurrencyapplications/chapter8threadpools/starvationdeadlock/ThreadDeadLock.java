package org.rkoubsky.jcp.structuringconcurrencyapplications.chapter8threadpools.starvationdeadlock;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Task that deadlocks in a single-threaded Executor. Don't do this.
 *
 * ThreadDeadLock illustrates thread starvation deadlock.
 * RenderPageTask submits two additional tasks to the Executor to
 * fetch the page header and footer, renders the page body, waits
 * for the results of header and footer tasks, and then combines
 * the header, body, and footer into the finished page.
 *
 * With a SingleThreadedExecutor, ThreadDeadLock will always deadlock.
 *
 * <NOTE_starvation_deadlock>
 *
 *     Thread starvation deadlock can occur whenever a pool task initiates
 *     an unbounded blocking wait for some resource or condition that can
 *     succeed only through the action of another pool task, such as waiting
 *     for the return value or side effect of another task, unless you can
 *     guarantee that the pool is large enough.
 *
 *     In a single-threaded executor, a task that submits another task to
 *     the same executor and waits for its result will always deadlock.
 *
 * </NOTE_starvation_deadlock>
 */
public class ThreadDeadLock {
    ExecutorService exec = Executors.newSingleThreadExecutor();

    public class LoadFileTask implements Callable<String> {
        private final String fileName;

        public LoadFileTask(final String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String call() throws Exception {
            // Here's where we would actually read the file
            return "";
        }
    }

    public class RenderPageTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            final Future<String> header;
            final Future<String> footer;
            header = ThreadDeadLock.this.exec.submit(new LoadFileTask("header.html"));
            footer = ThreadDeadLock.this.exec.submit(new LoadFileTask("footer.html"));
            final String page = this.renderBody();
            // Will deadlock -- task waiting for result of subtask both running in a single threaded executor
            return header.get() + page + footer.get();
        }

        private String renderBody() {
            // Here's where we would actually render the page
            return "";
        }
    }
}
