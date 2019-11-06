package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.safepublication;

/**
 * Class at risk of failure if not properly published
 */
public class Holder {
    /**
     * Holder can be made immune to improper publication by declaring the "n" field final,
     * which would make the "Holder" immutable.
     */
    private int n;

    public Holder(final int n) {
        this.n = n;
    }

    /**
     * If "Holder" were immutable, assertSanity() could not throw AssertionError, even
     * if the "Holder" was not properly published
     */
    public void assertSanity() {
        if (this.n != this.n) {
            /**
             * A thread may see a stale value the firs time it reads a field and then a more
             * up-to-date value the next time, which is why assertSanity() can throw AssertionError
             *
             * See the integrity note in @link {@link StuffIntoPublic} javadoc
             * and the bad scenario number 2 regarding Object's constructor writing the defaults to all fields .
             */
            throw new AssertionError("This statement is false.");
        }
    }
}
