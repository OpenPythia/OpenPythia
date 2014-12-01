package org.openpythia.utilities.waitevent;

import java.math.BigDecimal;

public class WaitEventForTimeSpanTuple {

    private String waitEventClass;
    private String waitEventName;
    private String waitObjectOwner;
    private String waitObjectName;
    private BigDecimal waitedSeconds;

    public WaitEventForTimeSpanTuple(String waitEventClass, String waitEventName,
                                     String waitObjectOwner, String waitObjectName, BigDecimal waitedSeconds) {

        this.waitEventClass = waitEventClass;
        this.waitEventName = waitEventName;

        if (waitEventClass.equals("Application") ||
                waitEventClass.equals("Cluster") ||
                waitEventClass.equals("Concurrency") ||
                waitEventClass.equals("User I/O")) {

            // for these wait classes the following fields contain valid information
            this.waitObjectOwner = waitObjectOwner;
            this.waitObjectName = waitObjectName;
        } else {
            this.waitObjectOwner = "";
            this.waitObjectName = "";
        }


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
