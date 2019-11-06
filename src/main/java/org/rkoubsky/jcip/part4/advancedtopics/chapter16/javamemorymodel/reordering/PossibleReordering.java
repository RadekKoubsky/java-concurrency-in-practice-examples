package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel.reordering;

/**
 * Insufficiently synchronized program that can have surprising results
 *
 * PossibleReordering illustrates how difficult is to reason about the behavior
 * of even the simplest concurrent programs unless they are correctly synchronized.
 *
 *
 */
public class PossibleReordering {
    static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void main(final String[] args) throws InterruptedException {
        final Thread one = new Thread(new Runnable() {
            @Override
            public void run() {
                a = 1;
                x = b;
            }
        });
        final Thread other = new Thread(new Runnable() {
            @Override
            public void run() {
                b = 1;
                y = a;
            }
        });
        /**
         * It is fairly easy to imagine how PossibleReordering could print
         * (1,0), (0,1), (1,1):
         * threadTwo could run to completion before threadOne, or
         * threadOne could run to completion before threadTwo, or
         * their action could be interleaved
         *
         * But strangely, PossibleReordering could print (0, 0). The actions
         * in each thread have no dataflow dependence on each other, and accordingly
         * can be executed out of order.
         *
         * Reordering example:
         * Thread One         x=b (0) ----------------------> a=1
         * Thread Two                 b=1 ------> y=a (0)
         *
         * It is prohibitively difficult to reason about ordering in the absence of
         * synchronization; it is much easier to ensure that your program uses
         * synchronization appropriately.
         */
        one.start();
        other.start();
        one.join();
        other.join();
        System.out.println("( " + x + "," + y + ")");
    }
}
