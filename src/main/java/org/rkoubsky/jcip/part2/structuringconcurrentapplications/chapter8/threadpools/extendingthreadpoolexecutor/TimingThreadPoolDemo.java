package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.extendingthreadpoolexecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class TimingThreadPoolDemo {
    public static void main(final String[] args) throws InterruptedException {
        final ExecutorService exec = new TimingThreadPool();

        log.info("Starting {} instance.", exec.getClass().getSimpleName());
        IntStream.rangeClosed(0, 1).forEach((i) -> {
            exec.execute(() -> log.debug("Running in thread:{}", Thread.currentThread().getId()));
        });
        log.info("Shutting down executor service.");
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);
    }
}
