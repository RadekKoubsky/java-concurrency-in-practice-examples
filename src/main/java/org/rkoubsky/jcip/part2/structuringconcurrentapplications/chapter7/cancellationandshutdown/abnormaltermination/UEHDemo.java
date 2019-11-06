package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.abnormaltermination;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UEHDemo {
    public static void main(final String[] args) throws InterruptedException {
        final UEHLogger exceptionHandler = new UEHLogger();
        final ExecutorService exec = Executors.newCachedThreadPool(task -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(task);
            thread.setUncaughtExceptionHandler(exceptionHandler);
            return thread;
        });

        exec.execute(() -> {
            log.debug("Running arbitrary task and throwing runtime exception");
            throw new RuntimeException("This should be caught in uncaught exception handler");
        });
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);
    }
}
