package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.addingfunctionality.composition;

import net.jcip.annotations.ThreadSafe;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementing put-if-absent using composition
 *
 * There is a less fragile alternative for adding an atomic operation to
 * an existing class: composition
 *
 * Like Collections.synchronizedList and other collections wrappers,
 * ImprovedList assumes that once a list is passed to its constructor,
 * the client will not use the underlying list directly again, accessing it
 * only through ImprovedList.
 *
 * ImprovedList adds an additional level ov locking using its own intrinsic lock.
 *
 * While the extra layer of synchronization may add some small performance penalty,
 * the implementation in ImprovedList is less fragile than attempting to mimic
 * the locking strategy of another object.
 *
 * In effect, we've used the Java monitor pattern to encapsulate an existing List,
 * and this is guaranteed to provide thread safety so long as our class holds
 * the only outstanding reference to the underlying List.
 */
@ThreadSafe
public class ImprovedList<T> implements List<T> {

    private final List<T> list;

    /**
     * PRE: list argument is thread-safe.
     */
    public ImprovedList(final List<T> list) { this.list = list; }

    public synchronized boolean putIfAbsent(final T x) {
        final boolean contains = this.list.contains(x);
        if (!contains) {
            this.list.add(x);
        }
        return !contains;
    }

    // Plain vanilla delegation for List methods.
    // Mutative methods must be synchronized to ensure atomicity of putIfAbsent.

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return this.list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return this.list.toArray(a);
    }

    @Override
    public synchronized boolean add(final T e) {
        return this.list.add(e);
    }

    @Override
    public synchronized boolean remove(final Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public synchronized boolean addAll(final Collection<? extends T> c) {
        return this.list.addAll(c);
    }

    @Override
    public synchronized boolean addAll(final int index, final Collection<? extends T> c) {
        return this.list.addAll(index, c);
    }

    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(final Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return this.list.equals(o);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public T get(final int index) {
        return this.list.get(index);
    }

    @Override
    public T set(final int index, final T element) {
        return this.list.set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        this.list.add(index, element);
    }

    @Override
    public T remove(final int index) {
        return this.list.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return this.list.listIterator(index);
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    @Override
    public synchronized void clear() {
        this.list.clear(); }
}
