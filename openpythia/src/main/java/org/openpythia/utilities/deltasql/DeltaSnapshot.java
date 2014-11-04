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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openpythia.utilities.sql.SQLStatement;
import org.openpythia.utilities.sql.SQLStatementSnapshot;
import org.openpythia.utilities.sql.Snapshot;

public class DeltaSnapshot {

    private Snapshot snapshotA;
    private Snapshot snapshotB;

    private List<DeltaSQLStatementSnapshot> deltaSQLStatementSnapshots;

    /**
     * Create a new delta snapshot - the delta of two snapshots.
     * <p/>
     * When snapshotB is not younger than snapshotA, a runtime exception is
     * thrown.
     * On RAC (Real Application Cluster) one SQL statement can be executed on multiple
     * instances / cluster nodes. This leads to having multiple versions of the statements.
     * Sometimes it is easier to read the result if the statements from the different
     * instances are condensed into one statement. This can be achieved using the option
     * condenseResult. The condensed SQL statements get the instance ID -1.
     *
     * @param snapshotA The older snapshot.
     * @param snapshotB The newer snapshot.
     * @param condenseResult Should the result be condensed?
     */
    public DeltaSnapshot(Snapshot snapshotA, Snapshot snapshotB, boolean condenseResult) {
        if (snapshotA.getSnapshotId().compareTo(snapshotB.getSnapshotId()) > 0) {
            throw new IllegalArgumentException(
                    "Snapshot B not younger than Snapshot A.");
        }

        if (condenseResult) {
            this.snapshotA = condenseSnapshot(snapshotA);
            this.snapshotB = condenseSnapshot(snapshotB);
        } else {
            this.snapshotA = snapshotA;
            this.snapshotB = snapshotB;
        }

        createDelta();
    }

    /**
     * If there are SQL statements that were executed in several instances / cluster nodes of the DB,
     * condense these statements into one new statement containing the sum of all the statements.
     * @param snapshot The snapshot to condense.
     * @return A condensed snapshot containing each SQL statement only one time. The condensed statements
     * have the instance id -1.
     */
    private static Snapshot condenseSnapshot(Snapshot snapshot) {
        // for a faster lookup we put the snapshot into a hash map
        Map<String, List<SQLStatementSnapshot>> snapshotLookup = new HashMap<>();
        for (SQLStatementSnapshot sqlStatementSnapshot : snapshot.getSqlStatementSnapshots()) {
            if (!snapshotLookup.containsKey(sqlStatementSnapshot.getSqlStatement().getSqlId())) {
                snapshotLookup.put(sqlStatementSnapshot.getSqlStatement().getSqlId(), new ArrayList<SQLStatementSnapshot>());
            }
            snapshotLookup.get(sqlStatementSnapshot.getSqlStatement().getSqlId()).add(sqlStatementSnapshot);
        }

        Snapshot result = new Snapshot(snapshot.getSnapshotId());

        for (SQLStatementSnapshot sqlStatementSnapshot : snapshot.getSqlStatementSnapshots()) {
            String sqlId = sqlStatementSnapshot.getSqlStatement().getSqlId();
            if (snapshotLookup.containsKey(sqlId)) {
                // lookup table (still) contains the key - so we have to work in this statement
                if (snapshotLookup.get(sqlId).size() == 1) {
                    // there is only only one statement with this SQL-ID
                    result.addSQLStatementSnapshot(sqlStatementSnapshot);
                } else {
                    // There are multiple entries for this SQL statement. All the entries from the
                    // different instances have to be condensed into one new entry.
                    BigDecimal sumExecutions = BigDecimal.valueOf(0);
                    BigDecimal sumElapsedSeconds = BigDecimal.valueOf(0);
                    BigDecimal sumCpuSeconds = BigDecimal.valueOf(0);
                    BigDecimal sumBufferGets = BigDecimal.valueOf(0);
                    BigDecimal sumDiskReads = BigDecimal.valueOf(0);
                    BigDecimal sumConcurrencySeconds = BigDecimal.valueOf(0);
                    BigDecimal sumClusterSeconds = BigDecimal.valueOf(0);
                    BigDecimal sumRowsProcessed = BigDecimal.valueOf(0);
                    for (SQLStatementSnapshot currentStatement : snapshotLookup.get(sqlId)) {
                        sumExecutions = sumExecutions.add(currentStatement.getExecutions());
                        sumElapsedSeconds = sumElapsedSeconds.add(currentStatement.getElapsedSeconds());
                        sumCpuSeconds = sumCpuSeconds.add(currentStatement.getCpuSeconds());
                        sumBufferGets = sumBufferGets.add(currentStatement.getBufferGets());
                        sumDiskReads = sumDiskReads.add(currentStatement.getDiskReads());
                        sumConcurrencySeconds = sumConcurrencySeconds.add(currentStatement.getConcurrencySeconds());
                        sumClusterSeconds = sumClusterSeconds.add(currentStatement.getClusterSeconds());
                        sumRowsProcessed = sumRowsProcessed.add(currentStatement.getRowsProcessed());
                    }
                    result.addSQLStatementSnapshot(new SQLStatementSnapshot(
                            sqlStatementSnapshot.getSqlStatement(),
                            // identifier for the condensed entry - no longer just one instance
                            -1,
                            sumExecutions,
                            sumElapsedSeconds,
                            sumCpuSeconds,
                            sumBufferGets,
                            sumDiskReads,
                            sumConcurrencySeconds,
                            sumClusterSeconds,
                            sumRowsProcessed));
                }
                snapshotLookup.remove(sqlId);
            }
        }
        return result;
    }

    private void createDelta() {
        deltaSQLStatementSnapshots = new ArrayList<DeltaSQLStatementSnapshot>();

        // for a faster lookup we put snapshot A into a hash map
        Map<SQLStatement, SQLStatementSnapshot> snapshotALookup = new HashMap<SQLStatement, SQLStatementSnapshot>();
        for (SQLStatementSnapshot sqlStatementSnapshot : snapshotA
                .getSqlStatementSnapshots()) {
            snapshotALookup.put(sqlStatementSnapshot.getSqlStatement(),
                    sqlStatementSnapshot);
        }

        // now we iterate over the younger snapshot
        for (SQLStatementSnapshot sqlStatementSnapshotB : snapshotB
                .getSqlStatementSnapshots()) {
            SQLStatementSnapshot sqlStatementSnapshotA = snapshotALookup
                    .get(sqlStatementSnapshotB.getSqlStatement());

            if (sqlStatementSnapshotA == null) {
                // if there is no old snapshot of this statement, the statement
                // was executed for the first time after the first snapshot was
                // taken (or was dropped from the library cache). We take only
                // the new snapshot into account.
                sortDeltaSQLStatementSnapshotIn(new DeltaSQLStatementSnapshot(
                        sqlStatementSnapshotB));
            } else if (sqlStatementSnapshotA.getExecutions().compareTo(sqlStatementSnapshotB.getExecutions()) >= 0) {
                // the statement was not executed between the two snapshots. So
                // it will be ignored for the delta snapshot.
                //
                // OR the statements was executed a negative number of times.
                // For instance at 8:00 the statement had been executes 800
                // times. At 9:00 Oracle reports the same statement to been
                // executed 780 times. So in the meantime it was executes -20
                // times!?!
                // We decided to ignore such statements.
            } else {
                sortDeltaSQLStatementSnapshotIn(new DeltaSQLStatementSnapshot(
                        sqlStatementSnapshotA, sqlStatementSnapshotB));
            }
        }
    }

    private void sortDeltaSQLStatementSnapshotIn(
            DeltaSQLStatementSnapshot snapshot) {
        boolean putten = false;

        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSQLStatementSnapshots) {
            if (currentSnapshot.getDeltaElapsedSeconds().compareTo(snapshot.getDeltaElapsedSeconds()) < 0) {
                deltaSQLStatementSnapshots.add(
                        deltaSQLStatementSnapshots.indexOf(currentSnapshot),
                        snapshot);
                putten = true;
                break;
            }
        }
        if (!putten) {
            deltaSQLStatementSnapshots.add(snapshot);
        }
    }

    public List<DeltaSQLStatementSnapshot> getDeltaSqlStatementSnapshots() {
        return deltaSQLStatementSnapshots;
    }

    public void addDeltaSQLStatementSnapshot(
            DeltaSQLStatementSnapshot deltaSqlStatementSnapshot) {
        deltaSQLStatementSnapshots.add(deltaSqlStatementSnapshot);
    }

    public Snapshot getSnapshotA() {
        return snapshotA;
    }

    public Snapshot getSnapshotB() {
        return snapshotB;
    }
}
