package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.interruption;

import org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.blockingqueues.Indexer;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Using interruption for cancellation
 *
 * <NOTE_interruption_use>
 *
 *     There is nothing in the API or language specification that ties
 *     interruption to any specific cancellation semantics, but in practice,
 *     using interruption for anything but cancellation is fragile and
 *     difficult to sustain in large applications.
 *
 * </NOTE_interruption_use>
 *
 * <NOTE_calling_interrupt>
 *
 *     Calling interrupt does not necessarily stop the target thread from
 *     doing what it is doing; it merely delivers message that interruption
 *     has been requested.
 *
 * </NOTE_calling_interrupt>
 *
 * <NOTE_cancellation_points>
 *
 *     A good way to think about interruption is that it does not actually
 *     interrupt a running thread; it just "requests" that the thread interrupt
 *     itself at the next convenient opportunity - a cancellation point.
 *
 *     Some methods, such as wait, sleep, and join, take such requests seriously,
 *     throwing an exception when they receive an interrupt request or encounter
 *     an already set interrupt status upon entry.
 *
 * </NOTE_cancellation_points>
 *
 * <NOTE_clearing_interrupted_status>
 *
 *     If you call Thread.interrupted and it returns true,  unless you are planing
 *     to swallow the interruption, you should do something with it - either
 *     throw InterruptedException or restore the interrupted status by calling
 *     Thread.interrupt again, as in link {@link Indexer} line 40 and others.
 *
 * </NOTE_clearing_interrupted_status>
 *
 *
 * BrokenPrimeProducer illustrates how custom cancellation mechanisms do not always
 * interact well with blocking library methods. If you code your task to be
 * responsive to interruption, you can use interruption as your cancellation
 * mechanism and take advantage of the interruption support provided by many
 * library classes.
 *
 * <NOTE_interruption_as_cancellation>
 *
 *     Interruption is usually the most sensible way to implement cancellation.
 *
 * </NOTE_interruption_as_cancellation>
 *
 * BrokenPrimeProducer can be easily fixed by using interruption instead of a
 * boolean flag to request cancellation.
 *
 *
 * <NOTE_interrupting_a_thread>
 *
 *     Because each thread has its own interruption policy, you should
 *     not interrupt a thread unless you know what interruption means
 *     to that thread.
 *
 * </NOTE_interrupting_a_thread>
 *
 * <NOTE_swallowing_interrupted_request>
 *
 *     Only code that implements a thread's interruption policy may swallow
 *     an interruption request.
 *
 *     General-purpose task and library code should never swallow interruption requests.
 *
 * </NOTE_swallowing_interrupted_request>
 */
public class PrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;

    PrimeProducer(final BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            /**
             * The explicit test is not necessary in the loop header because
             * of the blocking "put" call, but it makes PrimeProducer more
             * responsive to interruption because it checks for the interruption
             * before starting the lengthy task.
             *
             * When calls to interruptible blocking methods are not frequent enough to
             * deliver the desired responsiveness, explicitly testing the interrupted
             * status can help.
             */
            while (!Thread.currentThread().isInterrupted()) {
                this.queue.put(p = p.nextProbablePrime());
            }
        }
        /**
         * You shouldn't swallow the InterruptedException by catching it and doing nothing
         * in the catch block, unless your code is actually implementing the interruption
         * policy for a thread.
         *
         * PrimeProducer swallows the interrupt, but does so with the knowledge that the
         * thread is about to terminate and that therefore there is no code higher up
         * on the call stack that needs to know about the interruption.
         */
        catch (final InterruptedException consumed) {
            /* Allow thread to exit */
        }
    }

    public void cancel() {
        this.interrupt();
    }
}
