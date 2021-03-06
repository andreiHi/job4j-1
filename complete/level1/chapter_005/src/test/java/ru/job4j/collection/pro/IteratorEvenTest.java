package ru.job4j.collection.pro;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 18.06.2017
 */
public class IteratorEvenTest {

    /**
     * Test method.
     */
    @Test
    public void whenTwoOnThreeThenOk() {
        int[] ar = {8, 2, 4, 3, 4, 7};
        IteratorEven it = new IteratorEven(ar);
        String result = "";
        while (it.hasNext()) {
            result = String.format("%s%s", result, it.next());
        }
        assertThat(result, is("8244"));
    }

}