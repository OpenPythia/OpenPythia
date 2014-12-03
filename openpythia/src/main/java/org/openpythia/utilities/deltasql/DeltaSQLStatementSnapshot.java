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

import java.math.BigDecimal;

public class DeltaSQLStatementSnapshot {

    private SQLStatement sqlStatement;
    private int instanceId;
    private BigDecimal deltaExecutions;
    private BigDecimal deltaElapsedSeconds;
    private BigDecimal deltaCpuSeconds;
    private BigDecimal deltaBufferGets;
    private BigDecimal deltaDiskReads;
    private BigDecimal deltaConcurrencySeconds;
    private BigDecimal deltaClusterSeconds;
    private BigDecimal deltaRowsProcessed;
    private BigDecimal deltaNumberStatements;

    public DeltaSQLStatementSnapshot(
            SQLStatementSnapshot sqlStatementSnapshotA,
            SQLStatementSnapshot sqlStatementSnapshotB) {
        this.sqlStatement = sqlStatementSnapshotB.getSqlStatement();
        // as the instance id can be overwritten due to condensing the snapshots, we can't use the instance id
        // of the original statement
        this.instanceId = sqlStatementSnapshotB.getInstanceId();

        // For some reasons Oracle reports from time to time that one of the
        // metrics has decreased between two snapshots. For instance at 7:00
        // Oracle reports a statement to have used 120 CPU seconds. At 8:00
        // Oracle reports the same statement to have used 110 CPU seconds. So
        // the statement has given back some CPU seconds? We don't know why
        // Oracle sometimes reports such mess. We have decided to handle such
        // negative difference by setting them to zero.
        this.deltaExecutions = sqlStatementSnapshotB.getExecutions().subtract(sqlStatementSnapshotA.getExecutions());
        if (this.deltaExecutions.compareTo(BigDecimal.ZERO) < 0) {
            // should never happen as the caller should filter out this case
            this.deltaExecutions = BigDecimal.ZERO;
        }
        this.deltaElapsedSeconds = sqlStatementSnapshotB.getElapsedSeconds().subtract(sqlStatementSnapshotA.getElapsedSeconds());
        if (this.deltaElapsedSeconds.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaElapsedSeconds = BigDecimal.ZERO;
        }
        this.deltaCpuSeconds = sqlStatementSnapshotB.getCpuSeconds().subtract(sqlStatementSnapshotA.getCpuSeconds());
        if (this.deltaCpuSeconds.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaCpuSeconds = BigDecimal.ZERO;
        }
        this.deltaBufferGets = sqlStatementSnapshotB.getBufferGets().subtract( sqlStatementSnapshotA.getBufferGets());
        if (this.deltaBufferGets.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaBufferGets = BigDecimal.ZERO;
        }
        this.deltaDiskReads = sqlStatementSnapshotB.getDiskReads().subtract(sqlStatementSnapshotA.getDiskReads());
        if (this.deltaDiskReads.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaDiskReads = BigDecimal.ZERO;
        }
        this.deltaConcurrencySeconds = sqlStatementSnapshotB.getConcurrencySeconds().subtract(sqlStatementSnapshotA.getConcurrencySeconds());
        if (this.deltaConcurrencySeconds.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaConcurrencySeconds = BigDecimal.ZERO;
        }
        this.deltaClusterSeconds = sqlStatementSnapshotB.getClusterSeconds().subtract(sqlStatementSnapshotA.getClusterSeconds());
        if (this.deltaClusterSeconds.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaClusterSeconds = BigDecimal.ZERO;
        }
        this.deltaRowsProcessed = sqlStatementSnapshotB.getRowsProcessed().subtract(sqlStatementSnapshotA.getRowsProcessed());
        if (this.deltaRowsProcessed.compareTo(BigDecimal.ZERO) < 0) {
            this.deltaRowsProcessed = BigDecimal.ZERO;
        }

        // The following code is a bit dirty: we know how many variants of the statement exist per snapshot.
        // But we don't know how many variants have been executed between the snapshot. As a workaround we use the
        // maximum of both.
        if (this.deltaNumberStatements != null || sqlStatementSnapshotA.getNumberStatements() != null) {
            BigDecimal thisNumberStatements = this.deltaNumberStatements != null ? this.deltaNumberStatements : BigDecimal.ONE;
            BigDecimal thatNumberStatements = sqlStatementSnapshotA.getNumberStatements() != null ? sqlStatementSnapshotA.getNumberStatements() : BigDecimal.ONE;

            this.deltaNumberStatements = thisNumberStatements.max(thatNumberStatements);
        }
    }

    public DeltaSQLStatementSnapshot(SQLStatementSnapshot sqlStatementSnapshotB) {
        this.sqlStatement = sqlStatementSnapshotB.getSqlStatement();

        this.deltaExecutions = sqlStatementSnapshotB.getExecutions();
        this.deltaElapsedSeconds = sqlStatementSnapshotB.getElapsedSeconds();
        this.deltaCpuSeconds = sqlStatementSnapshotB.getCpuSeconds();
        this.deltaBufferGets = sqlStatementSnapshotB.getBufferGets();
        this.deltaDiskReads = sqlStatementSnapshotB.getDiskReads();
        this.deltaConcurrencySeconds = sqlStatementSnapshotB.getConcurrencySeconds();
        this.deltaClusterSeconds = sqlStatementSnapshotB.getClusterSeconds();
        this.deltaRowsProcessed = sqlStatementSnapshotB.getRowsProcessed();
        this.deltaNumberStatements = sqlStatementSnapshotB.getNumberStatements();
    }

    public SQLStatement getSqlStatement() {
        return sqlStatement;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public BigDecimal getDeltaExecutions() {
        return deltaExecutions;
    }

    public BigDecimal getDeltaElapsedSeconds() {
        return deltaElapsedSeconds;
    }

    public BigDecimal getDeltaCpuSeconds() {
        return deltaCpuSeconds;
    }

    public BigDecimal getDeltaBufferGets() {
        return deltaBufferGets;
    }

    public BigDecimal getDeltaDiskReads() {
        return deltaDiskReads;
    }

    public BigDecimal getDeltaConcurrencySeconds() { return deltaConcurrencySeconds; }

    public BigDecimal getDeltaClusterSeconds() {
        return deltaClusterSeconds;
    }

    public BigDecimal getDeltaRowsProcessed() {
        return deltaRowsProcessed;
    }

    public BigDecimal getDeltaNumberStatements() {
        return deltaNumberStatements;
    }
}
