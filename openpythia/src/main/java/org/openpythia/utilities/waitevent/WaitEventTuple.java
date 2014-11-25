package org.openpythia.utilities.waitevent;

import java.math.BigDecimal;

public class WaitEventTuple {

    private String waitEventClass;
    private String waitEventName;
    private String waitObjectOwner;
    private String waitObjectName;
    private BigDecimal waitedSeconds;

    public WaitEventTuple(String waitEventClass, String waitEventName,
                          String waitObjectOwner, String waitObjectName, BigDecimal waitedSeconds) {
        this.waitEventClass = waitEventClass;
        this.waitEventName = waitEventName;
        this.waitObjectOwner = waitObjectOwner;
        this.waitObjectName = waitObjectName;
        this.waitedSeconds = waitedSeconds;
    }

    public String getWaitEventClass() {
        return waitEventClass;
    }

    public String getWaitEventName() {
        return waitEventName;
    }

    public String getWaitObjectOwner() {
        return waitObjectOwner;
    }

    public String getWaitObjectName() {
        return waitObjectName;
    }

    public BigDecimal getWaitedSeconds() {
        return waitedSeconds;
    }
}
