package org.klamo.klamocodingchallenge;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

// TODO - Is this class even necessary? If we just return a string, then this is meaningless, no?
public record Stats(int total, BigDecimal sumX, BigDecimal avgX, BigDecimal sumY, BigDecimal avgY) {
    @Override
    public String toString() {
        return total + "," + sumX + "," + avgX + "," + sumY + "," + avgY;
    }
}
