package org.rkoubsky.jcp.structuringconcurrencyapplications.chapter11.performanceandscalability.reducinglockontention.reducinglockgranularity.lockstriping;

import net.jcip.annotations.ThreadSafe;

/**
 * Hash-based map using lock striping
 * <p>
 * There are N_LOCKS,each guarding a subset of the buckets.
 * <p>
 * Most methods, like "get", need acquire only a single bucket lock.
 * <p>
 * Some methods may need to acquire all the locks but, as in the
 * implementation of "clear", may not need to acquire them simultaneously.
 * (clearing the map this way is not atomic, so there is not necessarily
 * a time when the StripedMap is actually empty if other threads are concurrently
 * adding elements, but clients using concurrent collection should expect
 * the size or clear methods to behave this way)
 */
@ThreadSafe
public class StripedMap {
    // Synchronization policy: buckets[n] guarded by locks[n%N_LOCKS]
    private static final int N_LOCKS = 16;
    private final Node[] buckets;
    private final Object[] locks;

    private static class Node {
        Node next;
        Object key;
        Object value;
    }

    public StripedMap(final int numBuckets) {
        this.buckets = new Node[numBuckets];
        this.locks = new Object[N_LOCKS];
        for (int i = 0; i < N_LOCKS; i++) {
            this.locks[i] = new Object();
        }
    }

    private final int hash(final Object key) {
        return Math.abs(key.hashCode() % this.buckets.length);
    }

    public Object get(final Object key) {
        final int hash = this.hash(key);
        synchronized (this.locks[hash % N_LOCKS]) {
            for (Node m = this.buckets[hash]; m != null; m = m.next) {
                if (m.key.equals(key)) {
                    return m.value;
                }
            }
        }
        return null;
    }

    public void clear() {
        for (int i = 0; i < this.buckets.length; i++) {
            synchronized (this.locks[i % N_LOCKS]) {
                this.buckets[i] = null;
            }
        }
    }
}
