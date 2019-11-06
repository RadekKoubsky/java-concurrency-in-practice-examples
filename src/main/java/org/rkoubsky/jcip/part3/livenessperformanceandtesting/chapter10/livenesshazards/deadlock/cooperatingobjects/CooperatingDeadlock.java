package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.cooperatingobjects;

import net.jcip.annotations.GuardedBy;
import org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * Lock-ordering deadlock between cooperating objects
 *
 * <NOTE_alien_method>
 *
 *     Invoking an alien method with a lock held is asking for liveness trouble.
 *     The alien method might acquire other locks (risking deadlock) or block
 *     for an unexpectedly long time, stalling other threads that need the lock
 *     you hold.
 *
 * </NOTE_alien_method>
 */
public class CooperatingDeadlock {
    // Warning: deadlock-prone!
    class Taxi {
        @GuardedBy("this") private Point location;
        @GuardedBy("this") private Point destination;
        private final Dispatcher dispatcher;

        public Taxi(final Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return this.location;
        }

        /**
         * Since setLocation and notifyAvailable are synchronized, the thread calling
         * setLocation acquires the Taxi lock and then the Dispatcher lock.
         *
         * WARNING SIGN of deadlock:
         * an alien method (notifyAvailable) is being called is being called
         * while holding a lock
         */
        public synchronized void setLocation(final Point location) {
            this.location = location;
            if (location.equals(this.destination)) {
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
         * Similarly, a thread calling getImage acquires the Dispatcher lock and then each Taxi
         * lock (one at a time).
         */
        public synchronized Image getImage() {
            final Image image = new Image();
            for (final Taxi t : this.taxis) {
                /**
                 * Deadlock:
                 * Thread A calls t1.setLocation and acquires Taxi lock L and tries to acquire dispatcher lock M
                 * Thread B calls dispatcher.getImage and acquires lock M and tries to acquire t1 lock L which is already
                 * held by t1.
                 *
                 * A(t1.L) -> B(dispatcher.M) -> A(t1.L)
                 *
                 */
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
