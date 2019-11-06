package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.abnormaltermination;

import lombok.extern.slf4j.Slf4j;

/**
 * UncaughtExceptionHandler that logs the exception
 *
 * What the handler should do with an uncaught exception depends
 * on your quality-of-service requirements. The most common
 * response is to write an error message and stack trace to the
 * application log.
 *
 * Handlers can also take more direct action, such as trying to
 * restart the thread, shutting down the application, paging
 * and operator, or other corrective or diagnostic actions.
 *
 * <NOTE_use_of_exception_handlers>
 *
 *     In long-running applications, always use uncaught exception
 *     handlers for all threads that at least log the exception.
 *
 * </NOTE_use_of_exception_handlers>
 *
 * To set an UncaughtExceptionHandler for pool threads, provide a ThreadFactory
 * to the ThreadPoolExecutor constructor. (As with all thread manipulation,
 * only the thread's owner should change its UncaughtExceptionHandler.).
 * If you want to be notified when a task fails due to an exception so that
 * you can take some task-specific recovery action, either wrap the task
 * with a Runnable or Callable that catches the exception or override the
 * "executeAfter" hook in ThreadPoolExecutor.
 */
@Slf4j
public class UEHLogger implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        log.info("Thread terminated with exception.", e);
    }
}
