package org.rkoubsky.jcp.structuringconcurrencyapplications.chapter12.testingconcurrentprograms.testingforcorrectness;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SemaphoreBoundedBufferTest {
    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    @Test
    public void testIsEmptyWhenConstructed() throws Exception {
        final SemaphoreBoundedBuffer<Integer> buffer = new SemaphoreBoundedBuffer<>(10);
        assertThat(buffer.isEmpty()).isTrue();
        assertThat(buffer.isFull()).isFalse();
    }

    @Test
    public void testIsFullAfterPuts() throws Exception {
        final int capacity = 10;
        final SemaphoreBoundedBuffer<Integer> buffer = new SemaphoreBoundedBuffer<>(capacity);
        for (int i = 0; i < capacity; i++) {
            buffer.put(i);
        }

        assertThat(buffer.isEmpty()).isFalse();
        assertThat(buffer.isFull()).isTrue();
    }

    /**
     * This methods shows an approach to testing blocking operations. It creates a "taker"
     * thread that attempts to "take" an element from an empty buffer. If "take" succeeds,
     * it registers failure. The test runner thread starts the taker thread, waits a long
     * time, and then interrupts it. If the taker thread has correctly blocked in the "take"
     * operation, it will throw InterruptedException, and the catch block for this exception
     * treats this as success and lets the thread exit. The main test runner thread then attempts
     * to "join" with the taker thread and verifies that the join returned successfully
     * by calling Thread.isAlive; if the taker thread responded to the interrupt, the "join"
     * should return quickly.
     *
     * This method tests several properties of "take" - not only that it blocks but that,
     * when interrupted, it throws InterruptedException.
     *
     * This is one of the few cases in which it is appropriate to subclass Thread explicitly
     * instead of using a Runnable in a pool: in order to test proper termination with "join"
     */
    @Test
    public void testTakeBlocksWhenEmpty() {
        final SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<Integer>(10);
        final Thread taker = new Thread() {
            @Override
            public void run() {
                try {
                    final int unused = bb.take();
                    fail(); // if we get here, it's an error
                } catch (final InterruptedException success) {
                }
            }
        };
        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            /**
             * The timed "join" ensures that the test completes even if "take" get stuck
             * in some unexpected way.
             */
            taker.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());
        } catch (final Exception unexpected) {
            fail();
        }
    }
}