/**
 * Explicit Locks offer an extended feature set compared to intrinsic locking,
 * including greater flexibility in dealing with lock unavailability and greater
 * control over queueing behavior. But ReentrantLock is not a blanked substitute
 * for "synchronized"; use it only when you need features that "synchronized" lacks.
 *
 * Read-write locks allow multiple readers to access a guarded object concurrently,
 * offering the potential for improved scalability when accessing read-mostly data
 * structures.
 */
package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks;