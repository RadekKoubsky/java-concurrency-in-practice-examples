package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.instanceconfinement.javamonitorpattern.vehicletrackerexample;

import net.jcip.annotations.NotThreadSafe;

/**
 * MutablePoint
 * <p/>
 * Mutable Point class similar to java.awt.Point
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class MutablePoint {
    public int x, y;

    public MutablePoint() {
        this.x = 0;
        this.y = 0;
    }

    public MutablePoint(final MutablePoint p) {
        this.x = p.x;
        this.y = p.y;
    }
}
