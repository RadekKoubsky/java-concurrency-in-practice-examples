package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Transforming sequential execution into parallel execution
 */
public abstract class TransformingSequential {

    void processSequentially(final List<Element> elements) {
        for (final Element e : elements) {
            this.process(e);
        }
    }

    /**
     * <NOTE_parallelizing_loops>
     *
     *     Sequential loop iterations are suitable for parallelization when
     *     each iteration is independent of the others and the work done in
     *     each iteration of the loop body is significant enough to offset
     *     the cost of managing a new task.
     *
     * </NOTE_parallelizing_loops>
     */
    void processInParallel(final Executor exec, final List<Element> elements) {
        for (final Element e : elements) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    TransformingSequential.this.process(e);
                }
            });
        }
    }

    public abstract void process(Element e);

    /**
     * Loop parallelization can also be applied to some recursive designs;
     * there are often sequential loops within the recursive algorithm that
     * can be parallelized.
     *
     * The sequentialRecursive method does depth-first traversal of a tree,
     * performing calculation on each node and placing the result in a collection.
     */
    public <T> void sequentialRecursive(final List<Node<T>> nodes,
            final Collection<T> results) {
        for (final Node<T> n : nodes) {
            results.add(n.compute());
            this.sequentialRecursive(n.getChildren(), results);
        }
    }

    /**
     * Transformed version of sequentialRecursive, parallelRecursive, also does
     * depth-first traversal, but instead of computing the result as each node
     * is visited, it submits a task to compute the node result.
     *
     * When parallelRecursive returns, each node in the tree has been visited
     * (the traversal is still sequential: only the calls to "compute" are
     * executed in parallel) and the computation of each node has been queued
     * to the Executor.
     */
    public <T> void parallelRecursive(final Executor exec,
            final List<Node<T>> nodes,
            final Collection<T> results) {
        for (final Node<T> n : nodes) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    results.add(n.compute());
                }
            });
            this.parallelRecursive(exec, n.getChildren(), results);
        }
    }

    /**
     * Callers of parallelRecursive can wait for all the results by creating an
     * Executor specific to the traversal and using shutdown and awaitTermination.
     */
    public <T> Collection<T> getParallelResults(final List<Node<T>> nodes)
            throws InterruptedException {
        final ExecutorService exec = Executors.newCachedThreadPool();
        final Queue<T> resultQueue = new ConcurrentLinkedQueue<T>();
        this.parallelRecursive(exec, nodes, resultQueue);
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }

    interface Element {
    }

    interface Node <T> {
        T compute();

        List<Node<T>> getChildren();
    }
}

