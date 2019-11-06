/**
 * Nonblocking algorithms maintain thread safety by using low level
 * concurrency primitives such as compare-and-swap (CAS) instead of locking.
 * These low level primitives are exposed through the atomic variable classes,
 * which can also be used as "better volatile variables" providing atomic update
 * operations for integers and object references.
 *
 * Nonblocking algorithms are difficult to design and implement, but can offer
 * better scalability under typical conditions and greater resistance to liveness
 * failures. Many of the advances in concurrent performance from one JVM version
 * to the next come from the use of nonblocking algorithms, both within the JVM
 * and in the platform libraries.
 */
package org.rkoubsky.jcip.part4.advancedtopics.chapter15.nonblockingsynchronization;