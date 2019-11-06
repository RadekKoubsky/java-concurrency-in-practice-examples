package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Using FutureTask to preload data that is needed later
 *
 * A computation represented by a FutureTask is implemented with a Callable,
 * the result-bearing equivalent of Runnable, and can be in one of three states:
 * waiting to run, running, completed.
 *
 * Completion subsumes all the ways a computation can complete,
 * including normal completion, cancellation, and exception.
 *
 * Once a FutureTask enters the complete state, it stays in that state forever.
 */

public class Preloader {
    private final FutureTask<ProductInfo> future =
            new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
                @Override
                public ProductInfo call() throws DataLoadException {
                    return Preloader.this.loadProductInfo();
                }
            });

    private final Thread thread = new Thread(this.future);

    private ProductInfo loadProductInfo() throws DataLoadException {
        return null;
    }

    /**
     * We start the thread from using the start method, since it is inadvisable
     * to start a thread from constructor or static initializer (e.g. possible escape of
     * reference of the enclosing instance aka Qualified This)
     */
    public void start() {
        this.thread.start(); }

    /**
     * After calling start method, it returns the loaded data if it is ready,
     * or waits for the load to complete if not.
     */
    public ProductInfo get() throws DataLoadException, InterruptedException {
        try {
            /**
             * The behavior of Future.get depends on the state of the task.
             * If it is completed, get returns the result immediately, and
             * otherwise blocks until the task transitions to the completed
             * state and then returns the result or throws an exception.
             *
             * The publication of a result from the computing thread to the thread(s)
             * retrieving the result is guaranteed to be safe publication.
             */
            return this.future.get();
        } catch (final ExecutionException e) {
            /**
             * Whe Future.get throws an exception, the cause will fall into three categories:
             *  - a checked exception thrown by the Callable
             *  - a RuntimeException
             *  - an Error
             *
             *  We must handle each of these cases separately
             */
            final Throwable cause = e.getCause();
            if (cause instanceof DataLoadException) {
                throw (DataLoadException) cause;
            } else {
                throw LaunderThrowable.launderThrowable(cause);
            }
        }
    }
}

class DataLoadException extends Exception { }

interface ProductInfo { }