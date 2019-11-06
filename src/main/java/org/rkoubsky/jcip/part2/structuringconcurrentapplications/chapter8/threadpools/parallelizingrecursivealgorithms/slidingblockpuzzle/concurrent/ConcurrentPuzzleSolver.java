package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.concurrent;

import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.Puzzle;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.PuzzleNode;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Concurrent version of puzzle solver
 *
 * ConcurrentPuzzleSolver uses an inner SolverTask class
 * that extends Node and implements Runnable. Most of the
 * work is done in "run" method: evaluating the set of
 * possible next positions, pruning positions already searched,
 * evaluating whether success has yet been achieved (by this task
 * or other task), and submitting unsearched positions to an Executor.
 *
 */
public class ConcurrentPuzzleSolver <P, M> {
    private final Puzzle<P, M> puzzle;
    /**
     * ConcurrentPuzzleSolver uses the internal work queue of the thread pool
     * instead of the call stack to hold the state of the search -> free of stack
     * size restrictions but can still run out of memory if the set of positions
     * exceeds the available memory.
     */
    private final ExecutorService exec;
    /**
     * To avoid infinite loops, the sequential version maintained a Set of
     * previously searched positions; ConcurrentPuzzleSolver uses a ConcurrentHashMap
     * for this purpose.
     *
     * This provides thread-safety and avoids the race condition using atomic putIfAbsent method.
     */
    private final ConcurrentMap<P, Boolean> seen;
    protected final ValueLatch<PuzzleNode<P, M>> solution = new ValueLatch<PuzzleNode<P, M>>();

    public ConcurrentPuzzleSolver(final Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
        this.exec = this.initThreadPool();
        this.seen = new ConcurrentHashMap<P, Boolean>();
        if (this.exec instanceof ThreadPoolExecutor) {
            final ThreadPoolExecutor tpe = (ThreadPoolExecutor) this.exec;
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    private ExecutorService initThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public List<M> solve() throws InterruptedException {
        try {
            final P p = this.puzzle.initialPosition();
            this.exec.execute(this.newTask(p, null, null));
            /**
             * The main thread needs to wait until a solution is found;
             * getValue in ValueLatch blocks until some thread has set
             * the value.
             */
            final PuzzleNode<P, M> solnPuzzleNode = this.solution.getValue();
            return (solnPuzzleNode == null) ? null : solnPuzzleNode.asMoveList();
        } finally {
            this.exec.shutdown();
        }
    }

    protected Runnable newTask(final P p, final M m, final PuzzleNode<P, M> n) {
        return new SolverTask(p, m, n);
    }

    protected class SolverTask extends PuzzleNode<P, M> implements Runnable {
        SolverTask(final P pos, final M move, final PuzzleNode<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            /**
             * Each task first consults the solution latch and stops if a solution
             * has already been found.
             */
            if (ConcurrentPuzzleSolver.this.solution.isSet()
                    || ConcurrentPuzzleSolver.this.seen.putIfAbsent(this.pos, true) != null) {
                return; // already solved or seen this position
            }
            if (ConcurrentPuzzleSolver.this.puzzle.isGoal(this.pos)) {
                ConcurrentPuzzleSolver.this.solution.setValue(this);
            } else {
                for (final M m : ConcurrentPuzzleSolver.this.puzzle.legalMoves(this.pos)) {
                    ConcurrentPuzzleSolver.this.exec.execute(
                            ConcurrentPuzzleSolver.this.newTask(ConcurrentPuzzleSolver.this.puzzle.move(this.pos, m), m,
                                                                this));
                }
            }
        }
    }
}
