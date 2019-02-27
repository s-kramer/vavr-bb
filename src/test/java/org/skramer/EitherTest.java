package org.skramer;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author created: skramer on 27.02.19 08:38
 */
@Slf4j
public class EitherTest {
    private static final int VALUE = 42;
    private static final int DEFAULT_VALUE = 142;

    private enum CalculationError {
        INSTANCE,
        OTHER_INSTANCE
    }

    @Test
    public void eitherCanBeCreated() {
        final Either<CalculationError, Integer> theGoodGuy = Either.right(VALUE);
        final Either<CalculationError, Integer> theBadBoy = Either.left(CalculationError.INSTANCE);

        assertThat(theGoodGuy.isRight()).isTrue();
        assertThat(theBadBoy.isLeft()).isTrue();
    }

    @Test
    public void valuesCanBePeekedAt() {
        final Either<CalculationError, Integer> theGoodGuy = Either.right(VALUE);
        final Either<?, Integer> peeker = theGoodGuy
                .peek(i -> log.info("The good guy is {}", i))
                .peekLeft((calcError) -> log.info("bad boy here: {}", calcError));
    }

    @Test
    public void valuesCanBeMapped() {
        final Either<CalculationError, Integer> theGoodGuy = Either.right(VALUE)
                .map(i -> i + 10)
                .mapLeft(x -> CalculationError.OTHER_INSTANCE);

        assertThat(theGoodGuy.get()).isEqualTo(52);
        assertThatThrownBy(theGoodGuy::getLeft).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void canGetValueOutOfEither() {
        final int theGoodGuy = Either.right(VALUE).getOrElseGet(error -> DEFAULT_VALUE);

        assertThat(theGoodGuy).isEqualTo(VALUE);

        final Either<CalculationError, Integer> theBadBoy = Either.left(CalculationError.INSTANCE);
        final int getOrElseGetResult = theBadBoy.getOrElseGet(error -> DEFAULT_VALUE);

        assertThat(getOrElseGetResult).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    public void canMapBothSidesAtOnce() {
        final Either<CalculationError, Integer> theGoodGuy = Either.right(VALUE);
        final Either<CalculationError, Integer> modified = theGoodGuy.bimap(error -> CalculationError.OTHER_INSTANCE, i -> i + 10);

        assertThat(modified.get()).isEqualTo(52);
    }

    @Test
    public void canChangeContext() {
        final Either<CalculationError, Integer> theGoodGuy = Either.right(VALUE);

        final Either<CalculationError, Integer> iWentBad = theGoodGuy.flatMap( i -> Either.left(CalculationError.OTHER_INSTANCE));
        assertThat(iWentBad.getLeft()).isEqualTo(CalculationError.OTHER_INSTANCE);

        final Either<CalculationError, Integer> iRemainGood = theGoodGuy.flatMap( i -> Either.right(i + 10));
        assertThat(iRemainGood.get()).isEqualTo(52);
    }
}
