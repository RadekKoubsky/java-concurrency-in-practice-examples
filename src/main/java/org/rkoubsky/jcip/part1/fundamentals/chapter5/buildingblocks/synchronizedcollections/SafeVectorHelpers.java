package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizedcollections;

import java.util.Vector;

/**
 * Compound actions on Vector using client-side locking
 *
 * NOTE: Avoid using client-side locking (see Clean Code horror story)
 */
public class SafeVectorHelpers {
    /**
     * By acquiring the collection lock we can make "getLast"
     * and "deleteLast" atomic, ensuring that the size of
     * the Vector does not change  between calling "size"
     * and "get".
     */
    public static Object getLast(final Vector list) {
        // acquiring the collection lock
        synchronized (list) {
            final int lastIndex = list.size() - 1;
            return list.get(lastIndex);
        }
    }

    public static void deleteLast(final Vector list) {
        synchronized (list) {
            final int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }
    }
}
