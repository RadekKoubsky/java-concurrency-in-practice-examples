package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.sequential;

import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.PuzzleNode;
import org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.Puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sequential puzzle solver
 *
 * It performs a depth-first search of the puzzle space.
 * It terminates when it finds a solution (which is not
 * necessarily the shortest solution)
 *
 */

public class SequentialPuzzleSolver <P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> seen = new HashSet<P>();

    public SequentialPuzzleSolver(final Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        final P pos = this.puzzle.initialPosition();
        return this.search(new PuzzleNode<P, M>(pos, null, null));
    }

    private List<M> search(final PuzzleNode<P, M> node) {
        if (!this.seen.contains(node.pos)) {
            this.seen.add(node.pos);
            if (this.puzzle.isGoal(node.pos)) {
                return node.asMoveList();
            }
            for (final M move : this.puzzle.legalMoves(node.pos)) {
                final P pos = this.puzzle.move(node.pos, move);
                final PuzzleNode<P, M> child = new PuzzleNode<P, M>(pos, move, node);
                final List<M> result = this.search(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
