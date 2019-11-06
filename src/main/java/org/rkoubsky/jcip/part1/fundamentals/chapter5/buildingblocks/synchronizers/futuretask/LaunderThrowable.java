package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask;

/**
 * StaticUtilities
 *
 * @author Brian Goetz and Tim Peierls
 */
public class LaunderThrowable {

    /**
     * Coerce an unchecked Throwable to a RuntimeException
     * <p/>
     * If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException
     */
    public static RuntimeException launderThrowable(final Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }
}
