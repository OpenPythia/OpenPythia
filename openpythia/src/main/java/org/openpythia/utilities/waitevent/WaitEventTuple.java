package org.openpythia.utilities.waitevent;

import java.math.BigDecimal;

public class WaitEventTuple {

    private String waitEventName;
    private BigDecimal waitedSeconds;

    public WaitEventTuple(String waitEventName, BigDecimal waitedSeconds) {
        this.waitEventName = waitEventName;
        this.waitedSeconds = waitedSeconds;
    }

    public String getWaitEventName() {
        return waitEventName;
    }

    public BigDecimal getWaitedSeconds() {
        return waitedSeconds;
    }
}
