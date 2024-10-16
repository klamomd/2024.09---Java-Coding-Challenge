package org.klamo.klamocodingchallenge;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainRestController {
    private EventManager eventManager = EventManager.getInstance();

    static final HttpStatusCode HTTP_ACCEPTED = HttpStatusCode.valueOf(202);
    static final HttpStatusCode HTTP_MULTI_STATUS = HttpStatusCode.valueOf(207);
    static final HttpStatusCode HTTP_BAD_REQUEST = HttpStatusCode.valueOf(400);
    static final HttpStatusCode HTTP_TEAPOT = HttpStatusCode.valueOf(418);

    @GetMapping(value = "/stats", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getStats() {
        long currentUnixTime = System.currentTimeMillis();
        return eventManager.getStatsStringFromLastMinute(currentUnixTime);
    }

    @PostMapping(value = "/event", produces = { "application/xml", "text/xml" }, consumes = { MediaType.TEXT_PLAIN_VALUE, "text/csv" })
    public ResponseEntity postEvent(@RequestBody String payload) {
        // Null payload handling - don't think this is actually likely, as not providing a body or providing a non-text
        // body will result in a 415, but let's err on the side of caution.
        if (payload == null)
        {
            var result = new ParseResult("null", false, "Request body is null.");
            return ResponseEntity.badRequest().body(new ResultsWrapper(List.of(result)));
        }

        // Blank payload results in 400 error.
        if (payload.isBlank())
        {
            var result = new ParseResult(payload, false, "Request body is empty or whitespace.");
            return ResponseEntity.badRequest().body(new ResultsWrapper(List.of(result)));
        }

        // Multiple events can be provided, each on their own line.
        String[] payloadStrings = payload.split("\\r?\\n");

        List<ParseResult> parseResults = new ArrayList<>();
        List<Event> validEvents = new ArrayList<>();
        int invalidEvents = 0;

        for (String input : payloadStrings) {
            Event e;
            try {
                e = Event.parseEventFromString(input);
                validEvents.add(e);
                parseResults.add(new ParseResult(input, true, null));
            } catch (IllegalArgumentException ex) {
                invalidEvents++;
                parseResults.add(new ParseResult(input, false, ex.getMessage()));
            }
        }

        // Return 400 if all bad, 202 if all good, and 207 if mixed.
        HttpStatusCode responseCode;
        if (validEvents.isEmpty())
            responseCode = HTTP_BAD_REQUEST;
        else {
            responseCode = (invalidEvents > 0) ?
                    HTTP_MULTI_STATUS :
                    HTTP_ACCEPTED;

            eventManager.addEvents(validEvents);
        }

        var body = new ResultsWrapper(parseResults);
        return new ResponseEntity(body, responseCode);
    }

    // https://www.webfx.com/web-development/glossary/http-status-codes/what-is-a-418-status-code/
    @GetMapping(value = "/teapot", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getTeapot() {
        return ResponseEntity.status(HTTP_TEAPOT).body("☕🍵");
    }
}
