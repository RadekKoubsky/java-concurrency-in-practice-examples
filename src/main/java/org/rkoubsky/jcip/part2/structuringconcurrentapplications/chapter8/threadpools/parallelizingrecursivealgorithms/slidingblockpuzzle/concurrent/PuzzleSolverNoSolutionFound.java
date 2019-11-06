package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.concurrent;

import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.Puzzle;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.PuzzleNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Solver that recognizes when no solution exists
 *
 * ConcurrentPuzzleSolver does not deal well with the case where there is
 * no solution: if all possible moves and positions have been evaluated and
 * no solution has been found, "solve" waits forever in the call to "getSolution".
 *
 * The sequential version terminated when it had exhausted the search space, but
 * getting concurrent programs to terminate can sometimes be more difficult.
 *
 * One possible solution is to keep a count of active solver tasks and set
 * the solution to null when the count drops to zero.
 */
public class PuzzleSolverNoSolutionFound<P,M> extends ConcurrentPuzzleSolver<P, M> {
    PuzzleSolverNoSolutionFound(final Puzzle<P, M> puzzle) {
        super(puzzle);
    }

    private final AtomicInteger taskCount = new AtomicInteger(0);

    @Override
    protected Runnable newTask(final P p, final M m, final PuzzleNode<P, M> n) {
        return new CountingSolverTask(p, m, n);
    }

    class CountingSolverTask extends SolverTask {
        CountingSolverTask(final P pos, final M move, final PuzzleNode<P, M> prev) {
            super(pos, move, prev);
            PuzzleSolverNoSolutionFound.this.taskCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                if (PuzzleSolverNoSolutionFound.this.taskCount.decrementAndGet() == 0) {
                    PuzzleSolverNoSolutionFound.this.solution.setValue(null);
                }
            }
        }
    }
}
