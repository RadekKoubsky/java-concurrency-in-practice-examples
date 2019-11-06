package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.publishingstatevariables;

import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Vehicle tracker that safely publishes underlying state
 *
 * PublishingVehicleTracker derives its thread safety from delegation to
 * an underlying ConcurrentHashMap, but this time the contents of the Map
 * are thread-safe mutable points rather than immutable ones.
 *
 * PublishingVehicleTracker is thread-safe, but would not be so if it imposed
 * additional constraints on the valid values of the vehicle locations. If it needed
 * to be able to "veto" changes to vehicle locations or to take action when
 * a location changes, the approach taken by PublishingVehicleTracker would not be
 * appropriate.
 */
@ThreadSafe
public class PublishingVehicleTracker {
    private final Map<String, SafePoint> locations;
    private final Map<String, SafePoint> unmodifiableMap;

    public PublishingVehicleTracker(final Map<String, SafePoint> locations) {
        this.locations = new ConcurrentHashMap<String, SafePoint>(locations);
        this.unmodifiableMap = Collections.unmodifiableMap(this.locations);
    }

    /**
     * The getLocations() method returns an unmodifiable copy of the
     * underlying Map. Callers cannot add or remove vehicles, but could
     * change the location of one of the vehicles by mutating the SafePoint
     * values in the returned Map.
     *
     * Again, the "live" nature of the map can be a benefit or a drawback,
     * depending on your requirements.
     */
    public Map<String, SafePoint> getLocations() {
        return this.unmodifiableMap;
    }

    public SafePoint getLocation(final String id) {
        return this.locations.get(id);
    }

    public void setLocation(final String id, final int x, final int y) {
        if (!this.locations.containsKey(id)) {
            throw new IllegalArgumentException("invalid vehicle name: " + id);
        }
        this.locations.get(id).set(x, y);
    }
}
