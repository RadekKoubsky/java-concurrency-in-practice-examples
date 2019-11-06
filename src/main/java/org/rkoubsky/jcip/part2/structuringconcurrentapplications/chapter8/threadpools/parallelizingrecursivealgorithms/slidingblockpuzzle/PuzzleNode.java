package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle;

import net.jcip.annotations.Immutable;

import java.util.LinkedList;
import java.util.List;

/**
 * PuzzleNode
 * <p/>
 * Link node for the puzzle solving framework
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public class PuzzleNode <P, M> {
    public final P pos;
    public final M move;
    public final PuzzleNode<P, M> prev;

    public PuzzleNode(final P pos, final M move, final PuzzleNode<P, M> prev) {
        this.pos = pos;
        this.move = move;
        this.prev = prev;
    }

    public List<M> asMoveList() {
        final List<M> solution = new LinkedList<M>();
        for (PuzzleNode<P, M> n = this; n.move != null; n = n.prev) {
            solution.add(0, n.move);
        }
        return solution;
    }
}
