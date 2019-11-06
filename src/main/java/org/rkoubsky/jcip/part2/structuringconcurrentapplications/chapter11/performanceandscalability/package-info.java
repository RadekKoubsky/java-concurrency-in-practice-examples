/**
 * Summary
 *
 * Because one of the most common reasons to use threads is to exploit multiple processors,
 * in discussing the performance of concurrent applications, we are usually more concerned
 * with throughput or scalability than we are with raw service time. Amdahl's law tells us
 * that scalability of an application is driven by the proportion of code that must be
 * executed serially. Since the primary source of serialization in Java programs is the
 * exclusive resource lock, scalability can often be improved by spending less time holding
 * locks, either by reducing lock granularity, reducing the duration for which locks are held,
 * or replacing exclusive locks with nonexclusive or nonblocking alternatives.
 */
package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter11.performanceandscalability;