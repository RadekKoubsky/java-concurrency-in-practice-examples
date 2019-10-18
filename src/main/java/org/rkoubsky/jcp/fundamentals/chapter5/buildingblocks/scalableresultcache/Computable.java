package org.rkoubsky.jcp.fundamentals.chapter5.buildingblocks.scalableresultcache;

/**
 * A function with input of type A and result of type V
 *
 * @param <A> input for the function
 * @param <V> result of the function
 */
public interface Computable <A, V> {
    V compute(A arg) throws InterruptedException;
}
