package org.openpythia.utilities.waitevent;

import java.math.BigDecimal;

public class WaitEventForStatementTuple extends WaitEventForTimeSpanTuple {

    public WaitEventForStatementTuple(String waitEventClass, String waitEventName,
                                      String waitObjectOwner, String waitObjectName, BigDecimal waitedSeconds) {

        super(waitEventClass, waitEventName, waitObjectOwner, waitObjectName, waitedSeconds);
    }
}
