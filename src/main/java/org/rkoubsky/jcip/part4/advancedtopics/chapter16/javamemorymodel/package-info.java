/**
 * The Java Memory Model specifies when the action of one thread on memory
 * are guaranteed to be visible to another. The specifics involve ensuring
 * that operations are ordered by a partial ordering called happens-before,
 * which is specified at the level of individual memory and synchronization
 * operations.
 * In the absence of sufficient synchronization, some very strange things
 * can happen when threads access shared data. However, the higher-level
 * rules offered in Chapter2 and 3, such as @GuardedBy and safe publication,
 * can be used to ensure thread safety without resorting to the low-level
 * details of happens-before.
 *
 */
package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel;