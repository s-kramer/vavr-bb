package org.skramer;

import io.vavr.Lazy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author created: skramer on 28.02.19 09:33
 */
@Slf4j
public class LazyTest {

    @Test
    public void shouldCreateLazy() {
        Lazy<Double> lazy = Lazy.of(Math::random);
        lazy.isEvaluated();
        final Double firstGet = lazy.get();
        lazy.isEvaluated();
        final Double secondGet = lazy.get();

        assertThat(firstGet).isEqualTo(secondGet);
    }
}
