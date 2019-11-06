/**
 * Summary
 *
 * Liveness failures are a serious problem because there is no way to recover
 * from them short of aborting the application.
 *
 * The most common form of liveness failure is lock-ordering deadlock.
 *
 * Avoiding lock ordering deadlock starts at design time:
 * ensure that when threads acquire multiple locks, they do so
 * in a consistent order.
 *The best way to do this is by using open calls throughout your program.
 * This greatly reduces the number of places where multiple locks are
 * held at once, and makes it more obvious where those places are.
 */
package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock;