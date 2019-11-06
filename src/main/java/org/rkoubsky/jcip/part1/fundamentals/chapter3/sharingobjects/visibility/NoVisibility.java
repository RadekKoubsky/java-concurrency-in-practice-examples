package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.visibility;

/**
 * Sharing variables without synchronization. Don't do this.
 *
 * In general, there is no guarantee that the reading thread will see a value written by another
 * thread on a timely basis, or not at all.
 *
 * <NOTE_reordering>
 *
 * There is no guarantee that operations in one thread will be performed in the order given by the
 * program, as long as the reordering is not detectable from within that thread - even if the
 * reordering is not apparent to other threads.
 *
 * Reordering allows JVMs to take full advantage of the performance of multiprocessor hardware.
 *
 * </NOTE_reordering>
 *
 * NOTE: In absence of synchronization, the compiler, processor, and runtime can do some downright weird
 * things to the order in which operations appear to execute.
 *
 * TO AVOID THESE COMPLEX ISSUES: Always use the proper synchronization whenever data is shared across threads.
 */
public class NoVisibility {
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(number);
        }
    }

    public static void main(final String[] args) {
        /**
         * Possible execution paths:
         *
         * 1. Happy case
         *
         * The reader thread will read the "ready=true" first, then reads the "number=42" and prints it.
         *
         * 2.
         *
         * NoVisibility could loop forever because the value of "ready" might never become visible to the
         * reader thread.
         *
         * 3. Reordering
         *
         * The NoVisibility could print zero because the write to "ready" field might be made visible to the
         * reader thread before the write to the "number" field, this phenomenon is known as REORDERING.
         */
        new ReaderThread().start();
        number = 42;
        ready = true;
    }
}
