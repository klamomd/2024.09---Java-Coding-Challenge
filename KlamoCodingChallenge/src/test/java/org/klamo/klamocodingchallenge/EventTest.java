package org.klamo.klamocodingchallenge;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.klamo.klamocodingchallenge.TestHelpers.*;

class EventTest {
    // Helper method to avoid code duplication. This verifies that attempting to parse the provided eventString results
    // in an IllegalArgumentException being thrown with the provided message.
    void assertParseEventThrowsIllegalArgumentExceptionWithMessage(String eventString, String expectedMessage) {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> Event.parseEventFromString(eventString),
                "Expected an IllegalArgumentException to be thrown, but it wasn't. Passed string: " + eventString
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }

    // Wrapper for above helper method, for tests which call it multiple times.
    void assertParseEventThrowsIllegalArgumentExceptionWithMessage(List<String> eventStrings, String expectedMessage) {
        for (String s : eventStrings) {
            assertParseEventThrowsIllegalArgumentExceptionWithMessage(s, expectedMessage);
        }
    }

    @Test
    void parseEventFromString_eventStringIsBlank_expectedExceptionThrown() {
        assertParseEventThrowsIllegalArgumentExceptionWithMessage(
                "",
                Event.EXCEPTION_MESSAGE_BLANK_STRING);
    }

    @Test
    void parseEventFromString_eventStringHasIncorrectNumberOfValues_expectedExceptionThrown() {
        assertParseEventThrowsIllegalArgumentExceptionWithMessage(
                List.of("a,b", "a,b,c,d"),
                Event.EXCEPTION_MESSAGE_WRONG_NUMBER_SUBSTRINGS);
    }

    @Test
    void parseEventFromString_timestampIsNotValidLong_expectedExceptionThrown() {
        String validRestOfString = "," + VALID_X + "," + VALID_Y;

        String badTimestamp = "sadfkljh" + validRestOfString;
        String emptyTimestamp = validRestOfString;
        String negativeTimestamp = "-1" + validRestOfString;
        String futureTimestamp = getFutureTimestamp() + validRestOfString;

        assertParseEventThrowsIllegalArgumentExceptionWithMessage(
                List.of(emptyTimestamp, badTimestamp, negativeTimestamp, futureTimestamp),
                Event.EXCEPTION_MESSAGE_INVALID_TIMESTAMP);
    }

    @Test
    void parseEventFromString_xIsNotValidDoubleOrOutOfRange_expectedExceptionThrown() {
        String invalidX = getCurrentTimestamp() + ",badX," + VALID_Y;
        String emptyX = getCurrentTimestamp() + ",," + VALID_Y;
        String xTooSmall = getCurrentTimestamp() + ",-0.1," + VALID_Y;
        String xTooLarge = getCurrentTimestamp() + ",1.1," + VALID_Y;

        assertParseEventThrowsIllegalArgumentExceptionWithMessage(
                List.of(invalidX, emptyX, xTooSmall, xTooLarge),
                Event.EXCEPTION_MESSAGE_INVALID_X);
    }

    @Test
    void parseEventFromString_yIsNotValidIntOrOutOfRange_expectedExceptionThrown() {
        String validStartOfString = getCurrentTimestamp() + "," + VALID_X + ",";

        String invalidY = validStartOfString + "badY";
        String emptyY = validStartOfString;
        String yTooSmall = validStartOfString + "1073741822";
        String yTooLarge = validStartOfString + "2147483648";

        assertParseEventThrowsIllegalArgumentExceptionWithMessage(
                List.of(invalidY, emptyY, yTooSmall, yTooLarge),
                Event.EXCEPTION_MESSAGE_INVALID_Y);
    }

    @Test
    void parseEventFromString_eventStringIsValid_eventCreatedAndReturned() {
        long validTimestamp = getCurrentTimestamp();
        String validEventString = validTimestamp + "," + VALID_X + "," + VALID_Y;

        Event parsedEvent = Event.parseEventFromString(validEventString);

        assertEquals(validTimestamp, parsedEvent.unixTimestampInMs);
        assertEquals(VALID_X, parsedEvent.x);
        assertEquals(VALID_Y, parsedEvent.y);
    }

    @Test
    void compareTo_otherEventWithDifferentTimestamp_returnsExpectedValue() {
        long timestampA = getCurrentTimestamp();
        long timestampB = timestampA - 1;

        Event a = new Event(timestampA, VALID_X, VALID_Y);
        Event b = new Event(timestampB, VALID_X, VALID_Y);

        assertEquals(1, a.compareTo(b));
        assertEquals(-1, b.compareTo(a));
    }

    @Test
    void compareTo_otherEventHasSameTimestampButDifferentX_returnsExpectedValue() {
        long timestamp = getCurrentTimestamp();

        double xA = VALID_X;
        double xB = xA - .1;

        Event a = new Event(timestamp, xA, VALID_Y);
        Event b = new Event(timestamp, xB, VALID_Y);

        assertEquals(1, a.compareTo(b));
        assertEquals(-1, b.compareTo(a));
    }

    @Test
    void compareTo_otherEventHasSameTimestampAndXButSmallerY_returnsExpectedValue() {
        long timestamp = getCurrentTimestamp();

        int yA = VALID_Y + 1;
        int yB = VALID_Y;

        Event a = new Event(timestamp, VALID_X, yA);
        Event b = new Event(timestamp, VALID_X, yB);

        assertEquals(1, a.compareTo(b));
        assertEquals(-1, b.compareTo(a));
    }

    @Test
    void compareTo_otherEventHasSameValues_returnsZero() {
        long timestamp = getCurrentTimestamp();

        Event a = new Event(timestamp, VALID_X, VALID_Y);
        Event b = new Event(timestamp, VALID_X, VALID_Y);

        assertEquals(0, a.compareTo(b));
        assertEquals(0, b.compareTo(a));
    }
}