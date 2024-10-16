package org.klamo.klamocodingchallenge;

import lombok.NonNull;
import org.assertj.core.util.VisibleForTesting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class EventManager {
    private static final int ONE_MINUTE_IN_MS = 60 * 1000;
    private static final Stats EMPTY_STATS = new Stats(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

    private final SortedSet<Event> eventSet;

    @VisibleForTesting
    EventManager(){
        eventSet = Collections.synchronizedSortedSet(new TreeSet<Event>());
    }

    // Lazy singleton.
    private static EventManager instance;
    public static EventManager getInstance(){
        if (instance == null)
            instance = new EventManager();

        return instance;
    }

    public void addEvents(@NonNull List<Event> events) {
        eventSet.addAll(events);
    }

    private Set<Event> getEventsWithinLastMinute(long unixTimestampInMs) {
        long oneMinuteAgo = unixTimestampInMs - ONE_MINUTE_IN_MS;

        for (Event event : eventSet) {
            if (event.unixTimestampInMs > oneMinuteAgo)
                return eventSet.tailSet(event);
        }

        return new HashSet<>();
    }

    public String getStatsStringFromLastMinute(long unixTimestampInMs) {
        Set<Event> events = getEventsWithinLastMinute(unixTimestampInMs);

        int total = events.size();
        if (total == 0)
            return EMPTY_STATS.toString();

        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;

        for (Event event : events) {
            sumX = sumX.add(BigDecimal.valueOf(event.x));
            sumY = sumY.add(BigDecimal.valueOf(event.y));
        }

        BigDecimal totalAsBD = BigDecimal.valueOf(total);
        BigDecimal avgX = sumX.divide(totalAsBD, 10, RoundingMode.HALF_UP).stripTrailingZeros();
        BigDecimal avgY = sumY.divide(totalAsBD, 10, RoundingMode.HALF_UP).stripTrailingZeros();

        Stats lastMinuteStats = new Stats(total, sumX, avgX, sumY, avgY);
        return lastMinuteStats.toString();
    }
}
