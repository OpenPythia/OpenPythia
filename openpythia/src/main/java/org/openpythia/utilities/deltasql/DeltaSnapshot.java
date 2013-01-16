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
     * 
     * When snapshotB is not younger than snapshotA, a runtime exception is
     * thrown.
     * 
     * @param snapshotA
     *            The older snapshot.
     * @param snapshotB
     *            The newer snapshot.
     */
    public DeltaSnapshot(Snapshot snapshotA, Snapshot snapshotB) {
        if (snapshotA.getSnapshotId().compareTo(snapshotB.getSnapshotId()) > 0) {
            throw new IllegalArgumentException(
                    "Snapshot B not younger than Snapshot A.");
        }

        this.snapshotA = snapshotA;
        this.snapshotB = snapshotB;

        createDelta();
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
            } else if (sqlStatementSnapshotA.getExecutions() >= sqlStatementSnapshotB
                    .getExecutions()) {
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
            if (currentSnapshot.getDeltaElapsedSeconds() < snapshot
                    .getDeltaElapsedSeconds()) {
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
