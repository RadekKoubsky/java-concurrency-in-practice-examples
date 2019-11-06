package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class PrimeDemo {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * The cancellation mechanism in PrimeGenerator will eventually cause the
     * prime-seeking task to exit, but it might take a while.
     *
     * This is why the program does not exit immediately after the prime
     * numbers are printed in log, the program is still running and exits
     * after a certain period of time. Try to run this main method to see.
     */
    public static void main(final String[] args) {
        try {
            final List<BigInteger> primes = aSecondOfPrimes();
            log.info("List of prime numbers: {}", primes);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        final PrimeGenerator generator = new PrimeGenerator();
        exec.execute(generator);
        try {
            SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }
}
