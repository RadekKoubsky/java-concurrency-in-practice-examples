package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingperformance;

/**
 * BarrierTimer
 * <p/>
 * Barrier-based timer
 *
 * @author Brian Goetz and Tim Peierls
 */
public class BarrierTimer implements Runnable {
    private boolean started;
    private long startTime, endTime;

    @Override
    public synchronized void run() {
        final long t = System.nanoTime();
        if (!this.started) {
            this.started = true;
            this.startTime = t;
        } else {
            this.endTime = t;
        }
    }

    public synchronized void clear() {
        this.started = false;
    }

    public synchronized long getTime() {
        return this.endTime - this.startTime;
    }
}
