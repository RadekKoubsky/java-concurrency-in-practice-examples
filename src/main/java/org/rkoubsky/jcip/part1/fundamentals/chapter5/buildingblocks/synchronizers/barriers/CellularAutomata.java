package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.barriers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Coordinating computation in a cellular automaton with CyclicBarrier
 *
 * <NOTE_barriers_vs_latches>
 *
 *     Barriers are similar to latches in that they block a group of
 *     threads until some event occurred. The key difference is that
 *     with a barrier, all the threads must come together at a barrier
 *     point at the same time in order to proceed.
 *
 *     Latches are for waiting for events.
 *
 *     Barriers are for waiting for other threads.
 *
 * </NOTE_barriers_vs_latches>
 *
 * This class demonstrates using a barrier to compute a cellular
 * automata simulation, such as Conway's Life game.
 *
 * Instead of assigning a separate thread to each element (in the case of Life, a cell) which
 * would require too many threads, it makes sense to partition the problem into a number
 * of subparts, let each thread solve a subpart, and then merge the result (after all threads
 * have reached the barrier).
 */
public class CellularAutomata {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final Worker[] workers;

    public CellularAutomata(final Board board) {
        this.mainBoard = board;
        final int count = Runtime.getRuntime().availableProcessors();

        /**
         * CyclicBarrier lets you pass a barrier action to the constructor;
         * this is a Runnable that is executed (in one of the subtask threads)
         * when the barrier is successfully passed but before the blocked
         * threads are released.
         *
         * When all worker threads have reached the barrier,
         * the barrier action commits the new values to the data model
         */
        this.barrier = new CyclicBarrier(count,
                                         new Runnable() {
                                             @Override
                                             public void run() {
                                                 CellularAutomata.this.mainBoard.commitNewValues();
                                             }});
        this.workers = new Worker[count];
        for (int i = 0; i < count; i++) {
            this.workers[i] = new Worker(this.mainBoard.getSubBoard(count, i));
        }
    }

    private class Worker implements Runnable {
        private final Board board;

        public Worker(final Board board) { this.board = board; }
        @Override
        public void run() {
            while (!this.board.hasConverged()) {
                for (int x = 0; x < this.board.getMaxX(); x++) {
                    for (int y = 0; y < this.board.getMaxY(); y++) {
                        this.board.setNewValue(x, y, this.computeValue(x, y));
                    }
                }
                try {
                    /**
                     * Wait for the rest of the worker threads to arrive at the barrier
                     */
                    CellularAutomata.this.barrier.await();
                } catch (final InterruptedException ex) {
                    return;
                } catch (final BrokenBarrierException ex) {
                    return;
                }
            }
        }

        private int computeValue(final int x, final int y) {
            // Compute the new value that goes in (x,y)
            return 0;
        }
    }

    public void start() {
        for (int i = 0; i < this.workers.length; i++) {
            new Thread(this.workers[i]).start();
        }
        this.mainBoard.waitForConvergence();
    }
}
