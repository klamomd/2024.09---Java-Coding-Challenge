package org.klamo.klamocodingchallenge;

import lombok.NonNull;

public class Event implements Comparable<Event> {
    public final long unixTimestampInMs;
    public final double x;
    public final int y;

    protected static final String EXCEPTION_MESSAGE_BLANK_STRING =
            "Event string is blank.";
    protected static final String EXCEPTION_MESSAGE_WRONG_NUMBER_SUBSTRINGS =
            "Event string must have 3 comma-separated values: long unixTimestampInMs, double x, int y.";
    protected static final String EXCEPTION_MESSAGE_INVALID_TIMESTAMP =
            "Event string must have a valid positive long for unixTimestampInMs. This timestamp cannot be from the future!";
    protected static final String EXCEPTION_MESSAGE_INVALID_X =
            "Event string must have a valid double for x, in the range of [0..1].";
    protected static final String EXCEPTION_MESSAGE_INVALID_Y =
            "Event string must have a valid int for y, in the range of [1073741823...2147483647].";

    Event(long unixTimestampInMs, double x, int y) {
        this.unixTimestampInMs = unixTimestampInMs;
        this.x = x;
        this.y = y;
    }

    public static Event parseEventFromString(@NonNull String s) {
        if (s.isBlank())
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_BLANK_STRING);

        s = s.trim();

        String[] subStrings = s.split(",", -1);

        if (subStrings.length != 3)
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_WRONG_NUMBER_SUBSTRINGS);

        long unixTimestampInMs;
        double x;
        int y;
        try {
            unixTimestampInMs = Long.parseLong(subStrings[0]);
            if (unixTimestampInMs < 0)
                throw new IllegalArgumentException("unixTimestampInMs cannot be negative");
            if (unixTimestampInMs > System.currentTimeMillis())
                throw new IllegalArgumentException("unixTimestampInMs cannot be from the future!");
        } catch (Exception e) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_INVALID_TIMESTAMP);
        }

        try {
            x = Double.parseDouble(subStrings[1]);
            if (x < 0 || x > 1)
                throw new IllegalArgumentException("x must be in the range of [0..1]");
        } catch (Exception e) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_INVALID_X);
        }

        try {
            y = Integer.parseInt(subStrings[2]);
            if (y < 1073741823)
                throw new IllegalArgumentException("y must be in the range of [1073741823...2147483647]");
        } catch (Exception e) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_INVALID_Y);
        }

        return new Event(unixTimestampInMs, x, y);
    }

    @Override
    public int compareTo(Event o) {
        int timestampComparison = Long.compare(this.unixTimestampInMs, o.unixTimestampInMs);
        if (timestampComparison != 0)
            return timestampComparison;

        int xComparison = Double.compare(this.x, o.x);
        if (xComparison != 0)
            return xComparison;

        return Integer.compare(this.y, o.y);
    }
}