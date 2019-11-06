package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter11.performanceandscalability.amdahlslaw;

import java.util.concurrent.BlockingQueue;

/**
 * Serialized access to a task queue
 *
 * <NOTE_premature_optimization>
 *
 *     Avoid premature optimization. First make it right, then make it fast -
 *     if it is not already fast enough.
 *
 * </NOTE_premature_optimization>
 *
 * <NOTE_amdahls_law>
 *
 *     Amdahl's law describes how much a program can theoretically be
 *     sped up by additional computing resources, based on portion of
 *     parallelizable and serial components.
 *
 *     If F is the fraction of the calculation that must be executed
 *     serially, then Amdahl's law says that on machine with N processors,
 *     we can achieve a speedup of at most:
 *
 *                  Speedup <=       1
 *                              -----------
 *                                  1 - F
 *                              F + -----
 *                                    N
 *
 * </NOTE_amdahls_law>
 *
 * At first glance, it may appear that the application is completely parallelizable:
 * tasks do not wait for each other, and the more processors available, the more tasks
 * can be processed concurrently.
 *
 * However, there is a serial component - fetching the task from the work queue.
 *
 * <NOTE_result_handling>
 *
 * This example also ignores another common source of serialization: result handling.
 * All useful computations produce some sort of result or side effect - if not, dead can
 * be eliminated as dead code. Since Runnable provides for no explicit result handling,
 * these task must have some sort of side effect, say writing result to a log file or
 * putting them in a data structure. Log files and result containers are usually shared by
 * multiple worker threads and therefore are also a source of serialization. If each thread
 * maintains its own data structure for results that are merged after all tasks are performed,
 * then the final merge is a source of serialization.
 *
 * </NOTE_result_handling>
 */
public class WorkerThread extends Thread {
    /**
     * The work queue is shared by all the worker threads, and it will require some
     * amount of synchronization to maintain its integrity in the face of concurrent
     * access.
     *
     * If locking is used to guard the state of the queue, then while one thread is
     * dequeing a task, other threads that need to dequeue their next task must wait -
     * and this is where task processing is serialized.
     */
    private final BlockingQueue<Runnable> queue;

    public WorkerThread(final BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final Runnable task = this.queue.take();
                task.run();
            } catch (final InterruptedException e) {
                break; /* Allow thread to exit */
            }
        }
    }
}
