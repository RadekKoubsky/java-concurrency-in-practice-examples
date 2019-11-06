package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.loggingservice;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Producer-consumer logging service with no shutdown support
 *
 * LogWriter shows a simple logging service in which the logging
 * activity is moved to a separate logger thread.
 *
 * For a service like LogWriter to be useful in production, we
 * need a way to terminate the logger thread so it does not prevent
 * the JVM from shutting down normally.
 *
 * However, simply making the logger thread exit is not very satisfying
 * shutdown mechanism. Such abrupt shutdown discards log messages that might
 * be waiting to be written to the log, but more importantly, threads blocked
 * in "log" because of full queue will never become unblocked.
 *
 * Cancelling producer-consumer activity requires cancelling both the producers
 * and the consumers. Interrupting the logger thread deals with the consumer,
 * but because the producers in this case are not dedicated threads, cancelling
 * them is harder.
 *
 * Unreliable way to add shutdown support to the logging service:
 *
 * public void log(final String msg) throws InterruptedException {
 *         if(!shutdownRequested){
 *             this.queue.put(msg);
 *         } else {
 *             throw new IllegalStateException("logger is shut down")
 *         }
 *     }
 *
 * The approach above has race conditions. The implementation of "log" is
 * check-then-act sequence: producers could observe that the service has not
 * yet been shutdown but still queue messages after shutdown, again with the
 * risk that producer might get blocked in "log" and never get unblocked.
 *
 * To fix the race condition means making the submission of a new log message
 * atomic, see the
 */
public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private static final int CAPACITY = 1000;

    public LogWriter(final Writer writer) {
        this.queue = new LinkedBlockingQueue<String>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        this.logger.start();
    }

    /**
     * Instead of having the thread that produces the message
     * write it directly to the output stream, LogWriter hands
     * it off to the logger thread via a BlockingQueue and the
     * logger thread writes it out.
     *
     * This is a multi-producer, single consumer design: any activity
     * calling "log" is acting as a producer, and the background
     * logging thread is the consumer.
     *
     * BlockingQueue eventually blocks the producers until the logger
     * thread catches up.
     */
    public void log(final String msg) throws InterruptedException {
        this.queue.put(msg);
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer;

        public LoggerThread(final Writer writer) {
            this.writer = new PrintWriter(writer, true); // autoflush
        }

        @Override
        public void run() {
            try {
                while (true) {
                    this.writer.println(LogWriter.this.queue.take());
                }
            } catch (final InterruptedException ignored) {
            } finally {
                this.writer.close();
            }
        }
    }
}
