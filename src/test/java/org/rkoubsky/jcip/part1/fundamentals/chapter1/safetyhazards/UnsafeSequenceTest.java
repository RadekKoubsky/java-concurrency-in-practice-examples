package org.rkoubsky.jcip.part1.fundamentals.chapter1.safetyhazards;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class UnsafeSequenceTest {

    public static final int NEXT_LOOP = 100000;
    public static final int LAST_NEXT = 2 * NEXT_LOOP;

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[100][0]);
    }

    @Test
    public void whenRunningInMultipleThreadsThenReturnCorruptedNextValue() throws Exception {
        final UnsafeSequence unsafeSequence = new UnsafeSequence();
        final Thread t1 = this.startThread(unsafeSequence);
        final Thread t2 = this.startThread(unsafeSequence);

        t1.join();
        t2.join();

        Assertions.assertThat(unsafeSequence.getNext())
                  .as("Should return corrupted next value due to possible thread interference, try again to make the threads interleave.")
                  .isNotEqualTo(LAST_NEXT);
    }

    private Thread startThread(final UnsafeSequence unsafeSequence) {
        final Thread worker = new Thread(() -> {
            for (int i = 0; i < NEXT_LOOP; i++) {
                unsafeSequence.getNext();
            }
        });
        worker.start();
        return worker;
    }
}
