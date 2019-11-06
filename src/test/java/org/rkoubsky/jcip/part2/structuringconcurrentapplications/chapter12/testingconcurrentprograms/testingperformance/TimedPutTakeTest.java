package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingperformance;

import junit.framework.TestCase;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.SemaphoreBoundedBuffer;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.testingsafety.XorShift;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing with a barrier-based timer
 *
 * Rather than attempting  to measure the time for a single operation,
 * we get a more accurate measure by timing the entire run and dividing
 * by the number of operations to get a per-operation time.
 */
public class TimedPutTakeTest extends TestCase {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final int nTrials;
    private final int nPairs;
    private BarrierTimer timer = new BarrierTimer();
    private CyclicBarrier barrier;
    private final AtomicInteger putSum = new AtomicInteger(0);
    private final AtomicInteger takeSum = new AtomicInteger(0);
    private final SemaphoreBoundedBuffer<Integer> bb;

    public TimedPutTakeTest(final int capacity, final int npairs, final int ntrials) {
        this.bb = new SemaphoreBoundedBuffer<>(capacity);
        this.nTrials = ntrials;
        this.nPairs = npairs;
        this.barrier = new CyclicBarrier(npairs * 2 + 1, this.timer);
    }

    public void test() {
        try {
            this.timer.clear();
            for (int i = 0; i < this.nPairs; i++) {
                pool.execute(new TimedPutTakeTest.Producer());
                pool.execute(new TimedPutTakeTest.Consumer());
            }
            this.barrier.await(); // cycle 1: wait for all threads to be ready
            this.barrier.await(); // cycle 2: wait for all threads to finish
            final long nsPerItem = this.timer.getTime() / (this.nPairs * (long) this.nTrials);
            System.out.print("Throughput: " + nsPerItem + " ns/item");
            assertThat(this.putSum.get()).isEqualTo(this.takeSum.get());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws Exception {
        final int tpt = 100000; // trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10) {
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs <= 128; pairs *= 2) {
                final TimedPutTakeTest t = new TimedPutTakeTest(cap, pairs, tpt);
                System.out.print("Pairs: " + pairs + "\t");
                t.test();
                System.out.print("\t");
                Thread.sleep(1000);
                t.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        TimedPutTakeTest.pool.shutdown();
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                TimedPutTakeTest.this.barrier.await();
                for (int i = TimedPutTakeTest.this.nTrials; i > 0; --i) {
                    TimedPutTakeTest.this.bb.put(seed);
                    sum += seed;
                    seed = new XorShift(seed).next();
                }
                TimedPutTakeTest.this.putSum.getAndAdd(sum);
                TimedPutTakeTest.this.barrier.await();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                TimedPutTakeTest.this.barrier.await();
                int sum = 0;
                for (int i = TimedPutTakeTest.this.nTrials; i > 0; --i) {
                    sum += TimedPutTakeTest.this.bb.take();
                }
                TimedPutTakeTest.this.takeSum.getAndAdd(sum);
                TimedPutTakeTest.this.barrier.await();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
