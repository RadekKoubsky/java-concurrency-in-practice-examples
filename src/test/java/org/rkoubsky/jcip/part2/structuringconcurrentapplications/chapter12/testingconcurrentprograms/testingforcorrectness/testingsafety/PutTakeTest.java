package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.testingsafety;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.SemaphoreBoundedBuffer;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Producer-consumer test program for BoundedBuffer
 *
 * PutTakeTest takes the following approach to a multi-producer, multi-consumer situation:
 *
 *  - using a checksum function that is insensitive to the order in which the elements
 *    are combined, so that multiple checksums can be combined after the test, otherwise,
 *    synchronizing access to a shared checksum field could become a concurrency bottleneck
 *    or distort the timing of the test.
 *
 *  - using pseudorandom number generator {@link XorShift} instead of consecutive integers
 *
 *  - using {@link CyclicBarrier} to ensure all threads are up and running before any start working,
 *    PutTakeTest test uses this technique to coordinate starting and stopping the worker threads,
 *    creating more potential concurrent interleavings.
 *
 *
 *  Tests like PutTakeTest tend to be good at finding safety violations. For example
 *  a common error in implementing semaphore-controlled buffers is to forget that the code
 *  actually doing insertion and extraction requires mutual exclusion (using "synchronized" or
 *  {@link ReentrantLock})
 *
 *  A sample run of PutTakeTest with a version of SemaphoreBoundedBuffer that omits making doInsert
 *  and doExtract synchronized fails quickly (try it out, it really does).
 *
 */
public class PutTakeTest {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    public static final int CAPACITY = 10;
    public static final int NPAIRS = 10;
    public static final int NTRIALS = 100000;
    private CyclicBarrier barrier = new CyclicBarrier(NPAIRS * 2 + 1);;
    private final SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(CAPACITY);
    private final AtomicInteger putSum = new AtomicInteger(0);
    private final AtomicInteger takeSum = new AtomicInteger(0);

    @Test
    public void test() {
        try {
            for (int i = 0; i < NPAIRS; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            this.barrier.await(); // cycle 1: wait for all threads to be ready
            this.barrier.await(); // cycle 2: wait for all threads to finish
            Assertions.assertThat(this.putSum.get()).isEqualTo(this.takeSum.get());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                PutTakeTest.this.barrier.await();
                for (int i = NTRIALS; i > 0; --i) {
                    PutTakeTest.this.bb.put(seed);
                    sum += seed;
                    seed = new XorShift(seed).next();
                }
                PutTakeTest.this.putSum.getAndAdd(sum);
                PutTakeTest.this.barrier.await();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                PutTakeTest.this.barrier.await();
                int sum = 0;
                for (int i = NTRIALS; i > 0; --i) {
                    sum += PutTakeTest.this.bb.take();
                }
                PutTakeTest.this.takeSum.getAndAdd(sum);
                PutTakeTest.this.barrier.await();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @After
    public void shutdown(){
        this.pool.shutdown();
    }
}
