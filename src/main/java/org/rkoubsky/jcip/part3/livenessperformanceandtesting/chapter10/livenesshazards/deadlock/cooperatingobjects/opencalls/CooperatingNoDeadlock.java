package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.cooperatingobjects.opencalls;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.Point;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.cooperatingobjects.CooperatingDeadlock;

import java.util.HashSet;
import java.util.Set;

/**
 * Using open calls to avoiding deadlock between cooperating objects
 *
 * <NOTE_open_call>
 *
 *     Calling a method with no lock held is called an open-call.
 *     Classes that rely on open calls are more wel-behaved and
 *     composable than classes that make calls with locks held.
 *
 * </NOTE_open_call>
 *
 * <NOTE_shrinking_synchronized_blocks>>
 *
 *     Taxi and Dispatcher from {@link CooperatingDeadlock} can be easily
 *     refactored to use open calls and thus eliminate the deadlock risk.
 *
 *     This involves shrinking the synchronized blocks to guard only
 *     operations that involve share state. Very often, the cause of
 *     problems like those is the use of "synchronized" methods instead
 *     of smaller synchronized blocks for reasons of compat syntax
 *     or simplicity rather than because the entire method must be guarded
 *     by a lock.
 *
 * </NOTE_shrinking_synchronized_blocks>>
 */
class CooperatingNoDeadlock {
    @ThreadSafe
    class Taxi {
        @GuardedBy("this") private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(final Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return this.location;
        }

        /**
         * <NOTE_breaking_atomic_block>
         *
         *     Restructuring a synchronized block to allow open calls can sometimes
         *     have undesirable consequences, since it takes an operation that was atomic
         *     and makes it not atomic.
         *
         *     In many cases, the lost of the atomicity is perfectly acceptable; there is
         *     no reason that updating a taxi's location and notifying the dispatcher that
         *     it is ready for a new destination need be an atomic operation.
         *
         * </NOTE_breaking_atomic_block>
         */
        public void setLocation(final Point location) {
            final boolean reachedDestination;
            synchronized (this) {
                this.location = location;
                reachedDestination = location.equals(this.destination);
            }
            if (reachedDestination) {
                this.dispatcher.notifyAvailable(this);
            }
        }

        public synchronized Point getDestination() {
            return this.destination;
        }

        public synchronized void setDestination(final Point destination) {
            this.destination = destination;
        }
    }

    @ThreadSafe
    class Dispatcher {
        @GuardedBy("this") private final Set<Taxi> taxis;
        @GuardedBy("this") private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            this.taxis = new HashSet<Taxi>();
            this.availableTaxis = new HashSet<Taxi>();
        }

        public synchronized void notifyAvailable(final Taxi taxi) {
            this.availableTaxis.add(taxi);
        }

        /**
         * <NOTE_breaking_atomic_block>
         *
         *     In other cases, the loss of atomicity is noticeable but the semantic
         *     changes are still acceptable. In the deadlock-prone version, getImage
         *     produces a complete snapshot of the fleet locations at that instant;
         *     in refactored version, it fetches the location of each taxi at slightly
         *     different times.
         *
         * </NOTE_breaking_atomic_block>
         */
        public Image getImage() {
            final Set<Taxi> copy;
            synchronized (this) {
                copy = new HashSet<Taxi>(this.taxis);
            }
            final Image image = new Image();
            for (final Taxi t : copy) {
                image.drawMarker(t.getLocation());
            }
            return image;
        }
    }

    class Image {
        public void drawMarker(final Point p) {
        }
    }

}
