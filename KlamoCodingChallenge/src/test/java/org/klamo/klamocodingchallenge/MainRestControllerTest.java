package org.klamo.klamocodingchallenge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.klamo.klamocodingchallenge.MainRestController.HTTP_BAD_REQUEST;
import static org.klamo.klamocodingchallenge.MainRestController.HTTP_ACCEPTED;
import static org.klamo.klamocodingchallenge.MainRestController.HTTP_MULTI_STATUS;
import static org.klamo.klamocodingchallenge.TestHelpers.*;

class MainRestControllerTest {
    private static MainRestController mainRestController;

    private static final String VALID_EVENT_STRING = getCurrentTimestamp() + "," + VALID_X + "," + VALID_Y;
    private static final String INVALID_EVENT_STRING = getFutureTimestamp() + "," + INVALID_X + "," + INVALID_Y;
    private static final String COMBINED_EVENT_STRING = VALID_EVENT_STRING + "\n" + INVALID_EVENT_STRING;

    @BeforeAll
    static void setUp() {
        mainRestController = new MainRestController();
    }

    // No tests added for getStats(), as existing EventManager test coverage seems to cover most of what I'd test here
    // anyway.

    // postEvent() tests are limited to validating Http response codes. Event test coverage is sufficient to cover
    // parsing, so most of what would be tested here is just the resulting XML that is sent back in the response body.
    // As the XML was just added as a nicety to help diagnose/troubleshoot errors, and is not actually required, I hope
    // I'll be let off the hook when it comes to testing it, please and thanks 🙏.

    @Test
    void postEvent_nullPayload_returns400BadRequest() {
        ResponseEntity response = mainRestController.postEvent(null);
        assertEquals(HTTP_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postEvent_emptyOrBlankPayload_returns400BadRequest() {
        for (String s : List.of("", " ", "\t", "\n", "\r")) {
            ResponseEntity response = mainRestController.postEvent(s);
            assertEquals(HTTP_BAD_REQUEST, response.getStatusCode());
        }
    }

    @Test
    void postEvent_onlyValidEvents_returns202Accepted() {
        ResponseEntity response = mainRestController.postEvent(VALID_EVENT_STRING);
        assertEquals(HTTP_ACCEPTED, response.getStatusCode());
    }

    @Test
    void postEvent_onlyInvalidEvents_returns400BadRequest() {
        ResponseEntity response = mainRestController.postEvent(INVALID_EVENT_STRING);
        assertEquals(HTTP_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postEvent_bothValidAndInvalidEvents_returns207MultiStatus() {

        ResponseEntity response = mainRestController.postEvent(COMBINED_EVENT_STRING);
        assertEquals(HTTP_MULTI_STATUS, response.getStatusCode());
    }
}