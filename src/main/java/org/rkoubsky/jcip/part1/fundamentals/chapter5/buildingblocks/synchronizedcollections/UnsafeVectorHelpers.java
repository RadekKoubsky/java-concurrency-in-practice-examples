package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizedcollections;

import java.util.Vector;

/**
 * Compound actions on a Vector that may produce confusing results
 *
 * These methods seem harmless, and in sense they are - they can't corrupt
 * the Vector because Vector class is thread-safe.
 *
 * But the caller of these might have a different opinion.
 *
 * If thread A calls "getLast" on a Vector with ten elements, thread B calls
 * "deleteLast" on the same Vector, and the operations are interleaved, "getLast"
 * throws ArrayIndexOutOfBoundsException. Between the call to "size" and subsequent
 * call to "getLast", the Vector shrank and the index computed in the first step
 * no longer valid.
 */
public class UnsafeVectorHelpers {
    public static Object getLast(final Vector list) {
        final int lastIndex = list.size() - 1;
        /** another thread can call deleteLast here which makes the lastIndex value invalid
         * and list.get(lastIndex) throws out of bound exception
         */
        return list.get(lastIndex);
    }

    public static void deleteLast(final Vector list) {
        final int lastIndex = list.size() - 1;
        list.remove(lastIndex);
    }
}
