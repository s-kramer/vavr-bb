package org.skramer;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author created: skramer on 27.02.19 16:16
 */
@Slf4j
public class TryTest {
    private static final int VALUE = 42;
    private static final int DEFAULT_VALUE = 142;

    @Test
    public void shouldBeAbleToCreateTry() {
        final Try<Integer> success = Try.of(() -> VALUE);
        final Try<Integer> failure = Try.of(() -> {
            throw new RuntimeException("expected");
        });

        assertThat(success.isSuccess()).isTrue();
        assertThat(failure.isFailure()).isTrue();
    }

    @Test
    public void shouldBeAbleToChainFunctions() {
        final Try<Integer> modified = Try.of(() -> VALUE)
                .map(i -> i + 10)
                .onSuccess(i -> log.info("success with {}", i))
                .onFailure(throwable -> log.error("failure with {}", throwable))
                .filterTry(this::iCanFail)
                .recover(IllegalArgumentException.class, DEFAULT_VALUE)
                .recoverWith(IllegalStateException.class, Try.success(DEFAULT_VALUE));

        assertThat(modified.get()).isEqualTo(142);
    }

    private boolean iCanFail(int i) {
        if (i % 2 == 0) {
            throw new IllegalStateException("meh");
        }
        return true;
    }

    @Test
    public void canBeChangedToEither() {
        final Either<Throwable, Integer> convertedToEither = Try.of(() -> VALUE)
                .toEither();
        assertThat(convertedToEither.isRight()).isTrue();
    }

}
