package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizedcollections;

import org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.locking.reentrancy.Widget;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class IteratorExamples {

    public static void unsafeIteration(final Vector vector) {
        /**
         * Iteration that may throw ArrayIndexOutOfBoundsException.
         *
         * The size of the Vector might change between the call "vector.size" and
         * "vector.get(i)"
         */
        for (int i = 0; i < vector.size(); i++) {
            vector.get(i);
        }
    }

    public static void safeIterationButSlow(final Vector vector) {
        /**
         * Iteration with client-side locking
         *
         * By holding the Vector lock for the duration of iteration,
         * we prevent other threads from modifying the Vector while
         * we are iterating it.
         *
         */
        synchronized (vector) {
            for (int i = 0; i < vector.size(); i++) {
                vector.get(i);
            }
        }
    }

    /**
     * Iterating a List with an Iterator
     */
    public static void unsafeListIterationWithIterator(final List list) {
        final List<Widget> widgetList = Collections.synchronizedList(list);

        /**
         * May throw ConcurrentModificationException
         */
        for (final Widget widget : widgetList) {
            /** Internally, the javac generates code that uses Iterator, repeatedly
             * calling "hasNext()" and "next" to iterate the list which is not atomic
             * operation.
             */

            // doSomething(w)
        }
    }

    /**
     * Iterating a List with an Iterator using the collection lock
     *
     * As with the Vector, hold the collection lock for
     * the duration of the operation. Beware, other threads that need
     * to access the collection will block until the iteration is complete;
     * the could wait for a long time
     */
    public static void safeListIterationWithIteratorButSlow(final List list) {
        final List<Widget> widgetList = Collections.synchronizedList(list);

        synchronized (widgetList) {
            for (final Widget widget : widgetList) {
                // doSomething(w)
            }
        }
    }

    /**
     * An alternative to locking the collection during iteration is to clone
     * the collection and iterate the copy instead. Since the clone is thread-confined,
     * no other thread can modify it during iteration.
     *
     * The collection still must be locked during the clone iteration itself.
     *
     * Cloning the collection has an obvious performance cost, depending on your
     * requirements.
     */
    public static void safeListIterationWithIteratorAndClone(final List list) {
        final List<Widget> widgetList = Collections.synchronizedList(list);

        final List<Widget> widgetListClone = null;
        synchronized (widgetList){
            // widgetListClone = clone the list
        }

        for (final Widget widget : widgetListClone) {
            // doSomething(w)
        }
    }
}
