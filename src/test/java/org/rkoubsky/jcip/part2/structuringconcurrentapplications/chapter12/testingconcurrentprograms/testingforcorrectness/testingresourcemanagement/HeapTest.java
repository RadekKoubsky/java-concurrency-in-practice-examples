package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.testingresourcemanagement;

import org.junit.Test;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.SemaphoreBoundedBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class HeapTest {
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    /**
     * The "testLeak" method contains placeholders for a heap-inspection tool to snapshot
     * the heap, which forces a garbage collection and then records information about the
     * heap size and memory usage.
     *
     * The "testLeak" method inserts several large objects into a bounded buffer and then
     * removes them; memory usage at heap snapshot #2 should be approximately the same as
     * at heap snapshot #1. On the other hand, if the "doExtract" method forgot to null
     * out the references to the returned element (items[i] = null), the reported memory
     * usage at the two snapshots would definitely not be the same. (This is one of the few
     * times where explicit nulling is necessary; most of the time, it is either not helpful
     * or actually harmful [EJ Item 5])
     */
    @Test
    public void testLeak() throws InterruptedException {
        final SemaphoreBoundedBuffer<Big> bb = new SemaphoreBoundedBuffer<>(CAPACITY);
        final int heapSize1 = this.snapshotHeap();
        for (int i = 0; i < CAPACITY; i++) {
            bb.put(new Big());
        }
        for (int i = 0; i < CAPACITY; i++) {
            bb.take();
        }
        final int heapSize2 = this.snapshotHeap();
        assertThat(Math.abs(heapSize1 - heapSize2)).isLessThan(THRESHOLD);
    }

    class Big {
        double[] data = new double[10000];
    }

    private int snapshotHeap() {
        /* Snapshot of the heap by heap-inspection tool, this is only a placeholder */
        return 0;
    }
}
