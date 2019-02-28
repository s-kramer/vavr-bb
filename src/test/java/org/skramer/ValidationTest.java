package org.skramer;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.Value;
import org.junit.Test;

import static io.vavr.control.Validation.combine;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author created: skramer on 27.02.19 18:16
 */
public class ValidationTest {
    private static final int TICKET_PRICE = 42;
    private static final int BAD_TICKET_PRICE = 43;
    private static final String EVENT_NAME = "cool event";

    @Value
    private class Request {
        private String cardId;
        private int ticketPrice;
        private String eventName;
        private Guest guest;
    }

    @Value
    private class Card {
        private String cardId;
    }

    @Value
    private class Ticket {
        private int price;
        private String eventName;
    }

    @Value
    private class Guest {
        private String name;
    }

    @Value
    private class ValidatedRequest {
        private Card card;
        private Ticket ticket;
        private Guest guest;
    }

    private Validation<String, Card> validateCardId(String cardId) {
        return Validation.valid(new Card(cardId));
    }

    private Validation<String, Ticket> validateTicketPrice(int ticketPrice, String eventName) {
        if (ticketPrice % 2 == 1) {
            return Validation.invalid("this price is weird");
        }
        if (!eventName.contains("cool")) {
            return Validation.invalid("this is not a cool event");
        }

        return Validation.valid(new Ticket(ticketPrice, eventName));
    }

    private Validation<String, Guest> validateGuest(Guest guest) {
        if (guest.getName().isEmpty()) {
            return Validation.invalid("Guest name cannot be empty");
        }
        return Validation.valid(guest);
    }

    @Test
    public void shouldBeAbleToValidate() {
        Request request = new Request("cardId", TICKET_PRICE, EVENT_NAME, new Guest("guestName"));
        final Validation<String, ValidatedRequest> validation = combine(validateCardId(request.getCardId()),
                validateTicketPrice(request.getTicketPrice(), request.getEventName()),
                validateGuest(request.getGuest()))
                .ap(ValidatedRequest::new)
                .mapError(this::asJson);

        assertThat(validation.isValid()).isTrue();
        assertThat(validation.get()).isInstanceOf(ValidatedRequest.class);
    }

    @Test
    public void shouldBeAbleToAccumulateErrors() {
        Request request = new Request("cardId", BAD_TICKET_PRICE, EVENT_NAME, new Guest(""));
        final Seq<String> validationErrors = combine(validateCardId(request.getCardId()),
                validateTicketPrice(request.getTicketPrice(), request.getEventName()),
                validateGuest(request.getGuest()))
                .ap(ValidatedRequest::new)
                .getError();

        assertThat(validationErrors).hasSize(2);
    }
    private String asJson(Seq<String> errs) {
        return null;
    }
}
