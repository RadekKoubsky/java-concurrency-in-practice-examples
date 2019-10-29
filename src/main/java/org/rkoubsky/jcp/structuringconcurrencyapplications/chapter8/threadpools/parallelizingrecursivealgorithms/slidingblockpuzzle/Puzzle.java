package org.rkoubsky.jcp.structuringconcurrencyapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle;

import java.util.Set;

/**
 * Abstraction for puzzles like the 'sliding blocks puzzle'
 */
public interface Puzzle <P, M> {
    P initialPosition();

    boolean isGoal(P position);

    Set<M> legalMoves(P position);

    P move(P position, M move);
}
