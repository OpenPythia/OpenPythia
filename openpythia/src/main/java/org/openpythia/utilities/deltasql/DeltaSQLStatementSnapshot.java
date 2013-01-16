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
package org.openpythia.utilities.deltasql;

import org.openpythia.utilities.sql.SQLStatement;
import org.openpythia.utilities.sql.SQLStatementSnapshot;

public class DeltaSQLStatementSnapshot {

    private SQLStatement sqlStatement;
    private int deltaExecutions;
    private int deltaElapsedSeconds;
    private int deltaCpuSeconds;
    private int deltaBufferGets;
    private int deltaDiskReads;
    private int deltaRowsProcessed;

    public DeltaSQLStatementSnapshot(
            SQLStatementSnapshot sqlStatementSnapshotA,
            SQLStatementSnapshot sqlStatementSnapshotB) {
        this.sqlStatement = sqlStatementSnapshotB.getSqlStatement();

        // For some reasons Oracle reports from time to time that one of the
        // metrics has decreased between two snapshots. For instance at 7:00
        // Oracle reports a statement to have used 120 CPU seconds. At 8:00
        // Oracle reports the same statement to have used 110 CPU seconds. So
        // the statement has given back some CPU seconds? We don't know why
        // Oracle sometimes reports such mess. We have decided to handle such
        // negative difference by setting them to zero.
        this.deltaExecutions = sqlStatementSnapshotB.getExecutions()
                - sqlStatementSnapshotA.getExecutions();
        if (this.deltaExecutions < 0) {
            // should never happen as the caller should filter out this case
            this.deltaExecutions = 0;
        }
        this.deltaElapsedSeconds = sqlStatementSnapshotB.getElapsedSeconds()
                - sqlStatementSnapshotA.getElapsedSeconds();
        if (this.deltaElapsedSeconds < 0) {
            this.deltaElapsedSeconds = 0;
        }
        this.deltaCpuSeconds = sqlStatementSnapshotB.getCpuSeconds()
                - sqlStatementSnapshotA.getCpuSeconds();
        if (this.deltaCpuSeconds < 0) {
            this.deltaCpuSeconds = 0;
        }
        this.deltaBufferGets = sqlStatementSnapshotB.getBufferGets()
                - sqlStatementSnapshotA.getBufferGets();
        if (this.deltaBufferGets < 0) {
            this.deltaBufferGets = 0;
        }
        this.deltaDiskReads = sqlStatementSnapshotB.getDiskReads()
                - sqlStatementSnapshotA.getDiskReads();
        if (this.deltaDiskReads < 0) {
            this.deltaDiskReads = 0;
        }
        this.deltaRowsProcessed = sqlStatementSnapshotB.getRowsProcessed()
                - sqlStatementSnapshotA.getRowsProcessed();
        if (this.deltaRowsProcessed < 0) {
            this.deltaRowsProcessed = 0;
        }
    }

    public DeltaSQLStatementSnapshot(SQLStatementSnapshot sqlStatementSnapshotB) {
        this.sqlStatement = sqlStatementSnapshotB.getSqlStatement();

        this.deltaExecutions = sqlStatementSnapshotB.getExecutions();
        this.deltaElapsedSeconds = sqlStatementSnapshotB.getElapsedSeconds();
        this.deltaCpuSeconds = sqlStatementSnapshotB.getCpuSeconds();
        this.deltaBufferGets = sqlStatementSnapshotB.getBufferGets();
        this.deltaDiskReads = sqlStatementSnapshotB.getDiskReads();
        this.deltaRowsProcessed = sqlStatementSnapshotB.getRowsProcessed();
    }

    public SQLStatement getSqlStatement() {
        return sqlStatement;
    }

    public int getDeltaExecutions() {
        return deltaExecutions;
    }

    public int getDeltaElapsedSeconds() {
        return deltaElapsedSeconds;
    }

    public int getDeltaCpuSeconds() {
        return deltaCpuSeconds;
    }

    public int getDeltaBufferGets() {
        return deltaBufferGets;
    }

    public int getDeltaDiskReads() {
        return deltaDiskReads;
    }

    public int getDeltaRowsProcessed() {
        return deltaRowsProcessed;
    }
}
