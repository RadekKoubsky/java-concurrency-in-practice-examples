package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation;

import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Using a volatile field to hold cancellation state
 *
 * There is no safe way to preemptively stop a thread in Java, and therefore
 * no safe way to stop a task. There are only cooperative mechanisms, by which
 * the task and the code requesting cancellation follow an agreed-upon
 * protocol.
 *
 * One such cooperative mechanism is setting a "cancellation-request" flag
 * that the task checks periodically; if it finds the flag set, the task
 * terminates early.
 *
 * <NOTE_cancellation_policy>
 *
 *     A task that wants to be cancellable must have a cancellation policy
 *     that specifies the "how", "when", and "what" of cancellation - how
 *     other code can request cancellation, when the task checks whether
 *     cancellation has been requested, and what actions the task takes in
 *     response to a cancellation request.
 *
 *     PrimeGenerator uses simple cancellation policy: client code requests
 *     cancellation by calling "cancel", PrimeGenerator checks for cancellation
 *     once per prime found and exit when it detects cancellation has been requested.
 * </NOTE_cancellation_policy>
 *
 *
 */
@ThreadSafe
@Slf4j
public class PrimeGenerator implements Runnable {
    @GuardedBy("this") private final List<BigInteger> primes = new ArrayList<>();

    /**
     * The cancel method sets the cancelled flag and the main
     * loop polls this flag before searching for the next prime number.
     *
     * For this to work reliably, "cancelled" must be volatile
     */
    private volatile boolean cancelled;

    @Override
    public void run() {
        log.info("Generating prime numbers stared.");
        BigInteger p = BigInteger.ONE;
        while (!this.cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                this.primes.add(p);
            }
        }
    }

    public void cancel() {
        this.cancelled = true;
        log.info("Cancellation requested!!!");
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(this.primes);
    }
}
