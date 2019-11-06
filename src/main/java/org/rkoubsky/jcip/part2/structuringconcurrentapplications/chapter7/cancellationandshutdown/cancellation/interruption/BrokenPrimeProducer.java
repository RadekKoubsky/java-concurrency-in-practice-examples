package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.interruption;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Unreliable cancellation that can leave producers stuck in a blocking operation. Don't do this.
 *
 * If a task that uses the same approach as PrimeGenerator (task exits after a while) calls
 * a bocking method such as BlockingQueue.put, we could have a more serious problem- the task
 * might never checks the cancellation flag and therefore might never terminate.
 *
 * BrokenPrimeProducer illustrates this problem. The producer thread generates primes
 * and places them on a blocking queue. If the producer gets ahead of consumer,
 * the queue will fill up and the "put" method will block.
 */
class BrokenPrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    BrokenPrimeProducer(final BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            /**
             * Unreliable cancellation caused by calling a blocking method (e.g. BlockingQueue.put), don't do this.
             *
             * What happens if the consumer task tries to cancel the producer task while it is blocked
             * in "put"? It can call "cancel" which will set the "cancelled" flag to true - but the
             * producer will never check the flag because it will never emerge from the blocking "put"
             * (because the consumer has stopped retrieving primes from the queue)
             */
            while (!this.cancelled) {
                this.queue.put(p = p.nextProbablePrime());
            }
        } catch (final InterruptedException consumed) {
        }
    }

    public void cancel() {
        this.cancelled = true;
    }
}
