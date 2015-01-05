/**
 * Copyright 2012 msg systems ag
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.openpythia.utilities.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = 9187082512305187559L;

    private Calendar snapshotTime;
    private List<SQLStatementSnapshot> sqlStatementSnapshots;
    
    public Snapshot(Calendar snapshotTime) {
        this.snapshotTime = snapshotTime;

        sqlStatementSnapshots = new ArrayList<>();
    }

    public Calendar getSnapshotTime() { return snapshotTime; }

    public String getSnapshotId() {
        return snapshotTime.get(Calendar.YEAR) + "."
                + fillLeadingZero(snapshotTime.get(Calendar.MONTH) + 1) + "."
                + fillLeadingZero(snapshotTime.get(Calendar.DAY_OF_MONTH)) + " "
                + fillLeadingZero(snapshotTime.get(Calendar.HOUR_OF_DAY)) + ":"
                + fillLeadingZero(snapshotTime.get(Calendar.MINUTE)) + ":"
                + fillLeadingZero(snapshotTime.get(Calendar.SECOND));
    }

    private static String fillLeadingZero(int value) {
        if (value >= 10) {
            return String.valueOf(value);
        } else {
            return "0" + String.valueOf(value);
        }
    }

    public List<SQLStatementSnapshot> getSqlStatementSnapshots() {
        return sqlStatementSnapshots;
    }
    
    public void addSQLStatementSnapshot(SQLStatementSnapshot sqlStatementSnapshot) {
        sqlStatementSnapshots.add(sqlStatementSnapshot);
    }
}
