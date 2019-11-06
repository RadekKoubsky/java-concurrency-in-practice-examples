package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.readwritelocks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Wrapping a Map with a read-write lock
 *
 * Read-write locks can improve concurrency when locks are typically
 * held for a moderately long time and most operations do not modify
 * the guarded resources.
 *
 * ReadWriteMap uses {@link ReentrantReadWriteLock} to wrap a Map so
 * that it can be shared safely by multiple readers and still prevent
 * reader-writer or writer-writer conflicts (ReadWriteMap does not
 * implement Map inteface because implementing the view methods such as
 * "entrySet" and "values" would be difficult and the "easy" methods
 * are usually sufficient).
 *
 * In reality, {@link ConcurrentHashMap}'s performance is so good
 * that you would probably use it rather than this approach if all
 * you needed was a concurrent hash-based map, but this technique
 * would be useful if you want to provide more concurrent access
 * to an alternate Map implementation such as {@link LinkedHashMap}
 */
public class ReadWriteMap <K,V> {
    private final Map<K, V> map;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = this.lock.readLock();
    private final Lock w = this.lock.writeLock();

    public ReadWriteMap(final Map<K, V> map) {
        this.map = map;
    }

    public V put(final K key, final V value) {
        this.w.lock();
        try {
            return this.map.put(key, value);
        } finally {
            this.w.unlock();
        }
    }

    public V remove(final Object key) {
        this.w.lock();
        try {
            return this.map.remove(key);
        } finally {
            this.w.unlock();
        }
    }

    public void putAll(final Map<? extends K, ? extends V> m) {
        this.w.lock();
        try {
            this.map.putAll(m);
        } finally {
            this.w.unlock();
        }
    }

    public void clear() {
        this.w.lock();
        try {
            this.map.clear();
        } finally {
            this.w.unlock();
        }
    }

    public V get(final Object key) {
        this.r.lock();
        try {
            return this.map.get(key);
        } finally {
            this.r.unlock();
        }
    }

    public int size() {
        this.r.lock();
        try {
            return this.map.size();
        } finally {
            this.r.unlock();
        }
    }

    public boolean isEmpty() {
        this.r.lock();
        try {
            return this.map.isEmpty();
        } finally {
            this.r.unlock();
        }
    }

    public boolean containsKey(final Object key) {
        this.r.lock();
        try {
            return this.map.containsKey(key);
        } finally {
            this.r.unlock();
        }
    }

    public boolean containsValue(final Object value) {
        this.r.lock();
        try {
            return this.map.containsValue(value);
        } finally {
            this.r.unlock();
        }
    }
}
