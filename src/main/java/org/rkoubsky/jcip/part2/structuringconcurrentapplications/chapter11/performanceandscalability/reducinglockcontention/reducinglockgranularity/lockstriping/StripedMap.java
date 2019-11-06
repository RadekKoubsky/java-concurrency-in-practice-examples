package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter11.performanceandscalability.reducinglockcontention.reducinglockgranularity.lockstriping;

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

    /**
     * <NOTE_avoiding_hot_fields>
     *
     *     Lock granularity cannot be reduced when there are variables that are required
     *     for every operation. This is yet another area where raw performance and scalability
     *     are at odds with each other; common optimization such as caching frequently computed
     *     values can introduce "hot fields" that limit scalability.
     *
     *     HashMap.size() example:
     *
     *     The simplest approach is to count the number of entries every time the "size" is called.
     *     A common optimization is to update a separate counter as entries are added or removed;
     *     this slightly increases the cost of a "put" or "remove" operation the keep the counter
     *     up-to-date, but it reduces the cost of the "size" operation from O(n) to O(1).
     *
     *     Keeping a separate counter to speed up operations like "size" and "isEmpty" works
     *     fine for a single-threaded or fully synchronized implementations, but makes it much
     *     harder to improve the scalability of the implementation because every operation
     *     that modifies the Map must now update the shared counter. Even if you use lock
     *     striping for the hash chains, synchronizing access to the counter reintroduces
     *     tha scalability problems because of locking. What looked like a performance
     *     optimization - caching the results of the "size" operation - has turned into
     *     a scalability liability. In this case, the counter is called a hot field because
     *     every mutative operation need to access it.
     *
     *     ConcurrentHashMap avoids this problem by having "size" enumerate the stripes and
     *     add up the number of elements in each stripe, instead of maintaining a global count.
     *     To avoid enumarating every element, ConcurrentHashMap maintains a separate count
     *     field for each stripe, also guarded by the stripe lock.
     *
     * </NOTE_avoiding_hot_fields>
     */
    public int size(){
        return 1;
    }

    public void clear() {
        for (int i = 0; i < this.buckets.length; i++) {
            synchronized (this.locks[i % N_LOCKS]) {
                this.buckets[i] = null;
            }
        }
    }
}
