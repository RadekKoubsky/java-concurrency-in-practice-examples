package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.instanceconfinement.javamonitorpattern.vehicletrackerexample;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Monitor-based vehicle tracker implementation
 *
 * This class encapsulates the identity and locations of the known vehicles,
 * making them well-suited as a data model in a model-view-controller GUI
 * application where it might be shared by a view thread and multiple updater threads.
 *
 * Since the view thread and the updater threads will access the data model concurrently,
 * it must be thread-safe.
 *
 * <NOTE_mutable_point>
 *
 *     Even though MutablePoint is not thread-safe, the tracker class is.
 *     Neither the map nor any of the mutable points it contains is ever published.
 *     When we need to return vehicle locations to callers, the appropriate values
 *     are copied using either the MutablePoint copy constructor or deepCopy,
 *     which creates a new Map whose values are copies of the keys and values from
 *     the old Map.
 *
 * </NOTE_mutable_point>
 *
 * <NOTE_performance>
 *
 *     This implementation maintains thread safety in part by copying mutable data
 *     before returning it to the client. This is usually not a performance issue,
 *     but could become one if the set of vehicles is very large (holding the lock
 *     for the long-running copy operation could degrade the responsiveness of UI).
 *
 * </NOTE_performance>
 *
 */
@ThreadSafe
public class MonitorVehicleTracker {
    @GuardedBy("this") private final Map<String, MutablePoint> locations;

    public MonitorVehicleTracker(final Map<String, MutablePoint> locations) {
        /**
         * We cannot just populate the HashMap via a copy constructor new HashMap<String, MutablePoint>(locations),
         * because only the references to the points would be copied, not the point objects themselves.
         *
         * Use deepCopy to safely copy the map.
         */
        this.locations = deepCopy(locations);
    }

    public synchronized Map<String, MutablePoint> getLocations() {
        /**
         * We cannot just wrap the Map with "unmodifiableMap", because
         * that protects only the collection from modification; it does not
         * prevent callers from modifying the mutable objects stored in it.
         *
         * Use deepCopy to safely copy the map.
         */
        return deepCopy(this.locations);
    }

    public synchronized MutablePoint getLocation(final String id) {
        final MutablePoint loc = this.locations.get(id);
        /**
         * The original point object never gets published as we are using
         * a copy constructor to return a copy of the original point object.
         */
        return loc == null ? null : new MutablePoint(loc);
    }

    public synchronized void setLocation(final String id, final int x, final int y) {
        final MutablePoint loc = this.locations.get(id);
        if (loc == null) {
            throw new IllegalArgumentException("No such ID: " + id);
        }
        loc.x = x;
        loc.y = y;
    }

    private static Map<String, MutablePoint> deepCopy(final Map<String, MutablePoint> m) {
        final Map<String, MutablePoint> result = new HashMap<String, MutablePoint>();

        for (final String id : m.keySet()) {
            result.put(id, new MutablePoint(m.get(id)));
        }

        return Collections.unmodifiableMap(result);
    }
}
