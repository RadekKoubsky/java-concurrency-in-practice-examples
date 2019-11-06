package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.nonstandardcancellation;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulating nonstandard cancellation in a task with newTaskFor
 *
 * The technique used in ReaderThread to encapsulate nonstandard cancellation
 * can be refined using the "newTaskFor" hook added to ThreadPoolExecutor in Java 6.
 *
 */
public abstract class SocketUsingTask <T> implements CancellableTask<T> {
    @GuardedBy("this") private Socket socket;

    protected synchronized void setSocket(final Socket s) {
        this.socket = s;
    }

    /**
     * {@link ReaderThread} encapsulates cancellation of socket-using threads by
     * overriding "interrupt"; the same can be done for tasks by overriding Future.cancel.
     */
    @Override
    public synchronized void cancel() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (final IOException ignored) {
        }
    }

    /**
     * SocketUsingTask defines Future.cancel to close the socket as well as call super.cancel.
     * If a SocketUsingTask is cancelled through its Future, the socket is closed and the executing
     * thread is interrupted. This increases the task's responsiveness to cancellation; not only
     * can it safely call interruptible blocking methods while remaining responsive to cancellation,
     * but it can also call blocking socket I/O methods.
     */
    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}


/**
 * CancellableTask defines an interface that extends Callable and adds
 * a "cancel" and a "newTask" factory method for constructing RunnableFuture.
 */
interface CancellableTask <T> extends Callable<T> {
    void cancel();

    RunnableFuture<T> newTask();
}

/**
 * CancellingExecutor extends ThreadPoolExecutor, and overrides
 * newTaskFor to let a CancellableTask create its own Future.
 */
@ThreadSafe
class CancellingExecutor extends ThreadPoolExecutor {
    public CancellingExecutor(
            final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CancellingExecutor(
            final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CancellingExecutor(
            final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CancellingExecutor(
            final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * The newTaskFor hook is a factory method that creates the Future allows you to
     * override Future.cancel. Custom cancellation code can perform logging or gather
     * information statistics on cancellation, and can also be used to cancel activities
     * that are not responsive to interruption.
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        if (callable instanceof CancellableTask) {
            return ((CancellableTask<T>) callable).newTask();
        } else {
            return super.newTaskFor(callable);
        }
    }
}
