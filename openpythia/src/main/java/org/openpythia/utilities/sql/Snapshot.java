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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = 9187082512305187559L;

    private Calendar snapshotTime;
    private String connectionName;
    private List<SQLStatementSnapshot> sqlStatementSnapshots;

    public Snapshot() {
        this.snapshotTime = null;

        sqlStatementSnapshots = new ArrayList<>();
    }

    public Snapshot(Calendar snapshotTime, String connectionName) {
        this.snapshotTime = snapshotTime;
        this.connectionName = connectionName;
        sqlStatementSnapshots = new ArrayList<>();
    }

    public Calendar getSnapshotTime() { return snapshotTime; }

    public String getSnapshotId() {
        if (snapshotTime == null) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        formatter.setTimeZone(snapshotTime.getTimeZone());

        return formatter.format(snapshotTime.getTime());
    }

    public String getSnapshotName() {
        return connectionName + " " + this.getSnapshotId();
    }

    private static String fillLeadingZero(int value) {
        if (value >= 10) {
            return String.valueOf(value);
        } else {
            return "0" + value;
        }
    }

    public List<SQLStatementSnapshot> getSqlStatementSnapshots() {
        return sqlStatementSnapshots;
    }
    
    public void addSQLStatementSnapshot(SQLStatementSnapshot sqlStatementSnapshot) {
        sqlStatementSnapshots.add(sqlStatementSnapshot);
    }
}
