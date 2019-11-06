package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety;

import net.jcip.annotations.Immutable;

/**
 * Point
 * <p/>
 * Immutable Point class used by DelegatingVehicleTracker
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public class Point {
    public final int x, y;

    public Point(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}
