package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.testingsafety;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <NOTE_random_number_generator_concurrency_bottleneck>
 *
 *     Test data should be generated randomly, but many
 *     otherwise effective tests are compromised by a poor
 *     choice of random number generator (RNG). Random number
 *     generation can create couplings between classes and timing
 *     artifacts because most random number generator classes are
 *     thread-safe and therefore introduce additional synchronization.
 *
 *     Rather than using general-purpose RNG, it is better to use simple
 *     pseudorandom functions.
 *
 * </NOTE_random_number_generator_concurrency_bottleneck>
 *
 * XorShift function is among the cheapest medium-quality random number functions.
 */
public class XorShift {
    static final AtomicInteger seq = new AtomicInteger(8862213);
    int x = -1831433054;

    public XorShift(final int seed) {
        this.x = seed;
    }

    public XorShift() {
        this((int) System.nanoTime() + seq.getAndAdd(129));
    }

    public int next() {
        this.x ^= this.x << 6;
        this.x ^= this.x >>> 21;
        this.x ^= (this.x << 7);
        return this.x;
    }
}
