package org.rkoubsky.jcip.part1.fundamentals.chapter1.safetyhazards;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class SequenceTest {
    public static final int NEXT_LOOP = 100000;
    public static final int LAST_NEXT = 2 * NEXT_LOOP;

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[100][0]);
    }

    @Test
    public void whenRunningInMultipleThreadsThenReturnCorrectNextValue() throws Exception {
        final Sequence sequence = new Sequence();
        final Thread t1 = this.startThread(sequence);
        final Thread t2 = this.startThread(sequence);

        t1.join();
        t2.join();

        Assertions.assertThat(sequence.getNext())
                  .as("Should return correct next value even though there may be thread interference.")
                  .isEqualTo(LAST_NEXT);
    }

    private Thread startThread(final Sequence sequence) {
        final Thread worker = new Thread(() -> {
            for (int i = 0; i < NEXT_LOOP; i++) {
                sequence.getNext();
            }
        });
        worker.start();
        return worker;
    }
}
