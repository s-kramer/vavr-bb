package org.skramer;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author created: skramer on 26.02.19 23:05
 */
@Slf4j
public class OptionTest {
    private static final int VALUE = 42;

    @Test
    public void optionCanBeCreated() {
        final Option<Integer> optionValue = Option.of(VALUE);
        assertThat(optionValue.isDefined()).isTrue();

        final Option<Integer> emptyOption = Option.none();
        assertThat(emptyOption.isEmpty()).isTrue();

    }

    @Test
    public void optionCanBeCreatedFromCondition() {
        Optional.of(VALUE)
                .filter(i -> complexLogic())
                .map(i -> i + 10);

        final Option<Integer> fromCondition = Option.when(complexLogic(), () -> VALUE);
        assertThat(fromCondition.isDefined()).isTrue();
    }

    private boolean complexLogic() {
        return true;
    }

    @Test
    public void optionValueCanBeKindaModified() {
        final Option<Integer> optionValue = Option.of(VALUE);
        final Option<Integer> newValue = optionValue.map(i -> i + 10);

        assertThat(optionValue.getOrElseThrow(() -> new IllegalStateException("whaaat?!"))).isEqualTo(42);
        assertThat(newValue.isDefined()).isTrue();
        assertThat(newValue.getOrElseThrow(() -> new IllegalStateException("whaaat?!"))).isEqualTo(52);
    }

    @Test
    public void optionValueCanChangeContext() {
        final Option<Integer> emptyOption = Option.of(null);
        assertThat(emptyOption.isDefined()).isFalse();

        final Option<Integer> mappedToNull = Option.of(VALUE)
                .map(i -> (Integer) null)
//                .map(thisIsNull -> thisIsNull + 10)
                ;
        assertThat(mappedToNull.isDefined()).isTrue();

        final Option<Integer> flatmappedToNull = Option.of(VALUE)
                .flatMap(i -> Option.<Integer>of(null)
                        .map(j -> j * 2)
                        .filter(j -> j % 3 == 0)
                        .onEmpty(() -> log.info("I'm empty"))
                )
                .map(thisIsNull -> thisIsNull + 10);
        assertThat(flatmappedToNull.isDefined()).isFalse();
    }

    @Test
    public void optionCanBePeekedAt() {
        final Option<Integer> optionValue = Option.of(VALUE)
                .peek(i -> log.info("Superduper hard calculation gave {}", i));
    }

    @Test
    public void optionImplementsAValue() {
        final Option<Integer> optionValue = Option.of(VALUE);
        assertThat(optionValue.isAsync()).isFalse();
        assertThat(optionValue.isLazy()).isFalse();
        assertThat(optionValue.isSingleValued()).isTrue();
    }

    @Test
    public void optionCanBeConvertedToStandardJava() {
        final Option<Integer> optionValue = Option.of(VALUE);

        assertThat(optionValue.toJavaOptional()).isEqualTo(Optional.of(VALUE));
        assertThat(optionValue.toJavaList()).isEqualTo(singletonList(VALUE));
        assertThat(optionValue).containsOnly(VALUE);
    }

}
