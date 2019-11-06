package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.loggingservice;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Logging service that uses an ExecutorService
 *
 * LogServiceWithExecutorService delegates to an ExecutorService
 * instead of managing its own thread.
 *
 * Encapsulating an ExecutorService extends the ownership chain from
 * application to service to thread by adding another link; each member
 * of the chain manages the lifecycle of the services or threads it owns.
 */
public class LogServiceWithExecutorService {
    private final ExecutorService exec = newSingleThreadExecutor();
    private PrintWriter writer;

    public LogServiceWithExecutorService(final Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public void start() {

    }

    public void stop() throws InterruptedException {
        try {
            this.exec.shutdown();
            this.exec.awaitTermination(10, TimeUnit.SECONDS);
        } finally {
            this.writer.close();
        }
    }

    public void log(final String msg) {
        this.exec.execute(new WriteTask(msg));
    }

    private class WriteTask implements Runnable {
        private String msg;

        public WriteTask(final String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            LogServiceWithExecutorService.this.writer.println(this.msg);
        }
    }
}
