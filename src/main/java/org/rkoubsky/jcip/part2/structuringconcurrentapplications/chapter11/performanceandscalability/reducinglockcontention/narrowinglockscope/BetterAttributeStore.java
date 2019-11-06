package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter11.performanceandscalability.reducinglockcontention.narrowinglockscope;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.DelegatingVehicleTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Reducing lock duration
 *
 * BetterAttributeStore reduces significantly the lock duration.
 */
@ThreadSafe
public class BetterAttributeStore {
    /**
     * Because BetterAttributeStore has only one state variable, "attributes",
     * we can improve it further by technique of delegating thread safety (see {@link DelegatingVehicleTracker}).
     * By replacing attributes wi a thread-safe Map (a Hashtable, synchronizedMap,
     * or ConcurrentHashMap), BetterAttributeStore delegate all its thread safety
     * obligations to the underlying thread-safe collection.
     */
    @GuardedBy("this") private final Map<String, String> attributes = new HashMap<String, String>();

    public boolean userLocationMatches(final String name, final String regexp) {
        final String key = "users." + name + ".location";
        final String location;
        /**
         * Because constructing the key and processing the regular expression does not
         * access shared state, they need not to be executed with lock held.
         *
         * BetterAttributeStore factors these steps out of the synchronized block, thus
         * reducing the time the lock is held.
         *
         * By Amdahl's law, this removes an impediment to scalability because the amount
         * of serialized code is reduced.
         */
        synchronized (this) {
            location = this.attributes.get(key);
        }
        if (location == null) {
            return false;
        } else {
            return Pattern.matches(regexp, location);
        }
    }
}
