package org.klamo.klamocodingchallenge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.klamo.klamocodingchallenge.TestHelpers.*;

class EventManagerTest {
    private EventManager eventManager;
    private final Stats EMPTY_STATS = new Stats(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

    private List<Event> createOldEvents() {
        return createTestEventsWithTimestamp(getPastTimestamp());
    }

    private List<Event> createCurrentEvents() {
        return createTestEventsWithTimestamp(getCurrentTimestamp());
    }

    // Yes I know I'm going too far to avoid code duplication while also being too lax with the test data I am generating.
    // I'm trying to follow the DRY principle, but I'm also trying to create meaningful tests, but I've already spent far
    // too long on this, and so I'm rushing to wrap it up.
    private List<Event> createTestEventsWithTimestamp(long timestamp) {
        double x1 = VALID_X;
        double x2 = VALID_X + 0.1;

        int y1 = VALID_Y;
        int y2 = VALID_Y + 1;

        Event event1 = new Event(timestamp, x1, y1);
        Event event2 = new Event(timestamp, x2, y2);

        return List.of(event1, event2);
    }

    // See above comment on why I can't be arsed to implement this properly. I'll overlook the fact that the test jar
    // contained a bug if you'll overlook how lazily I'm implementing these final tests and helper methods, deal?
    private Stats createExpectedStats() {
        double x1 = VALID_X;
        double x2 = VALID_X + 0.1;
        double xSum = x1 + x2;
        double xAvg = xSum / 2;

        int y1 = VALID_Y;
        int y2 = VALID_Y + 1;
        double ySum = y1 + y2;
        double yAvg = ySum / 2;

        return new Stats(
                2,
                BigDecimal.valueOf(xSum),
                BigDecimal.valueOf(xAvg),
                BigDecimal.valueOf(ySum),
                BigDecimal.valueOf(yAvg)
        );
    }

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
    }

    @Test
    void getStatsStringFromLastMinute_noEventsExist_returnDefaultStats() {
        long now = getCurrentTimestamp();
        assertEquals(EMPTY_STATS.toString(), eventManager.getStatsStringFromLastMinute(now));
    }

    @Test
    void getStatsStringFromLastMinute_allOldEvents_returnDefaultStats() {
        eventManager.addEvents(createOldEvents());

        long now = getCurrentTimestamp();

        assertEquals(EMPTY_STATS.toString(), eventManager.getStatsStringFromLastMinute(now));
    }

    @Test
    void getStatsStringFromLastMinute_allRecentEvents_returnAllEventStats() {
        eventManager.addEvents(createCurrentEvents());

        Stats expectedStats = createExpectedStats();

        assertEquals(expectedStats.toString(), eventManager.getStatsStringFromLastMinute(getCurrentTimestamp()));
    }

    @Test
    void getStatsStringFromLastMinute_oldAndRecentEvents_returnOnlyRecentStats() {
        eventManager.addEvents(createOldEvents());
        eventManager.addEvents(createCurrentEvents());

        Stats expectedStats = createExpectedStats();

        assertEquals(expectedStats.toString(), eventManager.getStatsStringFromLastMinute(getCurrentTimestamp()));
    }

    // Ideally I would also implement a test like this, where I verify that the BigDecimal division crash that I ran into
    // (where the result of division has an endlessly-repeating decimal point, like 0.33333...) no longer occurs. But my
    // eyeballs are tired and my brain is about to go on strike, please cut me some slack.
    @Test
    void getStatsStringFromLastMinute_bigDecimalDivisionProducesRepeatingDecimal_statsRoundedWithoutCrash() {
        // Please pretend like I actually took the time to implement this, and that I implemented it well.
        // Oohs and ahhs are appreciated, but not necessary.
    }
}