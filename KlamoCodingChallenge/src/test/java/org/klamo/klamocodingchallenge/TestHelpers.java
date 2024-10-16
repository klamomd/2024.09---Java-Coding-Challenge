package org.klamo.klamocodingchallenge;

public class TestHelpers {
    static final long ONE_MINUTE_IN_MS = 60 * 1000;

    static final double VALID_X = 0.5;
    static final double INVALID_X = -1.0;
    static final int VALID_Y = 1073741823;
    static final int INVALID_Y = 1;

    static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    static long getFutureTimestamp() {
        return getCurrentTimestamp() + ONE_MINUTE_IN_MS;
    }

    static long getPastTimestamp() {
        return getCurrentTimestamp() - ONE_MINUTE_IN_MS;
    }

}
