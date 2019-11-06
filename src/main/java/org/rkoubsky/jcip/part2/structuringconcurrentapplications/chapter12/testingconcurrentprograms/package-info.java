/**
 * Testing concurrent programs for correctness can be extremely challenging because
 * many of the possible failure modes of concurrent programs are low-probability
 * events that are sensitive to timing, load, and other hard-to-reproduce conditions.
 *
 * Further, the testing infrastructure can introduce additional synchronization or
 * timing constraints that can mask concurrency problems in the code being tested.
 *
 * Testing concurrent programs for performance can be equally challenging; Java programs
 * are more difficult to test than programs written in statically compiled languages like C,
 * because timing measurements can be affected by dynamic compilation, garbage collection, and
 * adaptive optimization.
 *
 * To have the best chance of finding latent bugs before they occur in production, combine
 * traditional testing techniques (being careful to avoid pitfalls discussed here) with
 * code reviews and automated analysis tools. Each of these techniques finds problems that
 * the other are likely to miss.
 */
package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms;