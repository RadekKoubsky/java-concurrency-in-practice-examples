package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety;

import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Delegating thread safety to a ConcurrentHashMap
 *
 * This class does not use any explicit synchronization; all access to state
 * is managed by ConcurrentHashMap, and all keys and values of the Map are immutable.
 *
 */
@ThreadSafe
public class DelegatingVehicleTracker {
    private final ConcurrentMap<String, Point> locations;
    private final Map<String, Point> unmodifiableMap;

    public DelegatingVehicleTracker(final Map<String, Point> points) {
        this.locations = new ConcurrentHashMap<String, Point>(points);
        this.unmodifiableMap = Collections.unmodifiableMap(this.locations);
    }

    /**
     * This method returns an unmodifiable but "live" view of the vehicle locations.
     * This means if thread A calls getLocations() and thread B later modifies
     * the location of some of the points, those changes are reflected in the map
     * returned to thread A.
     *
     * This can be benefit (more up-to-date data) or liability (potentially inconsistent view of data),
     * depending on your requirements.
     */
    public Map<String, Point> getLocations() {
        return this.unmodifiableMap;
    }

    /**
     * Point is thread-safe because it is immutable. Immutable values
     * can be freely shared and published, so we no longer need to copy
     * the locations when returning them.
     */
    public Point getLocation(final String id) {
        return this.locations.get(id);
    }

    public void setLocation(final String id, final int x, final int y) {
        if (this.locations.replace(id, new Point(x, y)) == null) {
            throw new IllegalArgumentException("invalid vehicle name: " + id);
        }
    }

    /** Alternate version of getLocations (Listing 4.8), returning a static copy
     * of the locations instead of a "live" one.
     *
     * Returns shallow copy of the "locations" map as a not-thread safe HashMap,
     * as getLocations did not promise to return a thread-safe map (the contract
     * says only to return the Map interface).
     *
     * Since the contents of the Map is are immutable,
     * only the structure of the Map, not the contents, must be copied.
     */
    public Map<String, Point> getLocationsAsStatic() {
        return Collections.unmodifiableMap(
                new HashMap<String, Point>(this.locations));
    }
}

