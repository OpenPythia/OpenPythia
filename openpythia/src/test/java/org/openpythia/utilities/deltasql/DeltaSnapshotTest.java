package org.openpythia.utilities.deltasql;

import org.junit.Assert;
import org.junit.Test;
import org.openpythia.utilities.sql.SQLHelper;
import org.openpythia.utilities.sql.SQLStatement;
import org.openpythia.utilities.sql.SQLStatementSnapshot;
import org.openpythia.utilities.sql.Snapshot;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class DeltaSnapshotTest {

    @Test
    public void justOneStatementInSecondSnapshotTest() {
        Calendar fiveMinutesAgo = new GregorianCalendar();
        fiveMinutesAgo.add(Calendar.MINUTE, -5);
        Calendar now = new GregorianCalendar();

        Snapshot snapshotA = new Snapshot(fiveMinutesAgo);
        Snapshot snapshotB = new Snapshot(now);

        SQLStatement sqlStatement = SQLHelper.getSQLStatement("sqlId1", "parsingUser", 0, "SELECT 'just testing' FROM dual");

        snapshotB.addSQLStatementSnapshot(new SQLStatementSnapshot(sqlStatement,
                0, // instance ID
                new BigDecimal(1), // executions
                new BigDecimal(2), // elapsed
                new BigDecimal(3), // cpu
                new BigDecimal(4), // buffer gets
                new BigDecimal(5), // disk reads
                new BigDecimal(6), // concurrency
                new BigDecimal(7), // cluster
                new BigDecimal(8)  // rows
                ));

        DeltaSnapshot deltaSnapshot;

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, false, false, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, false, true, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, true, false, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, true, true, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);
    }

    private void justOneStatementInSecondSnapshotVerifyResult(DeltaSnapshot deltaSnapshot, SQLStatement sqlStatement) {
        Assert.assertEquals(1, deltaSnapshot.getDeltaSqlStatementSnapshots().size());
        Assert.assertEquals(sqlStatement, deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getSqlStatement());
        Assert.assertEquals(0, deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getInstanceId());
        Assert.assertEquals(new BigDecimal(1), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaExecutions());
        Assert.assertEquals(new BigDecimal(2), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaElapsedSeconds());
        Assert.assertEquals(new BigDecimal(3), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaCpuSeconds());
        Assert.assertEquals(new BigDecimal(4), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaBufferGets());
        Assert.assertEquals(new BigDecimal(5), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaDiskReads());
        Assert.assertEquals(new BigDecimal(6), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaConcurrencySeconds());
        Assert.assertEquals(new BigDecimal(7), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaClusterSeconds());
        Assert.assertEquals(new BigDecimal(8), deltaSnapshot.getDeltaSqlStatementSnapshots().get(0).getDeltaRowsProcessed());
    }

    @Test
    public void oneStatementInOneInstanceTest() {
        Calendar fiveMinutesAgo = new GregorianCalendar();
        fiveMinutesAgo.add(Calendar.MINUTE, -5);
        Calendar now = new GregorianCalendar();

        Snapshot snapshotA = new Snapshot(fiveMinutesAgo);
        Snapshot snapshotB = new Snapshot(now);

        SQLStatement sqlStatement = SQLHelper.getSQLStatement("sqlId2", "parsingUser", 0, "SELECT 'just testing' FROM dual");

        snapshotA.addSQLStatementSnapshot(new SQLStatementSnapshot(sqlStatement,
                0, // instance ID
                new BigDecimal(1), // executions
                new BigDecimal(2), // elapsed
                new BigDecimal(3), // cpu
                new BigDecimal(4), // buffer gets
                new BigDecimal(5), // disk reads
                new BigDecimal(6), // concurrency
                new BigDecimal(7), // cluster
                new BigDecimal(8)  // rows
        ));

        snapshotB.addSQLStatementSnapshot(new SQLStatementSnapshot(sqlStatement,
                0, // instance ID
                new BigDecimal(2), // executions
                new BigDecimal(4), // elapsed
                new BigDecimal(6), // cpu
                new BigDecimal(8), // buffer gets
                new BigDecimal(10), // disk reads
                new BigDecimal(12), // concurrency
                new BigDecimal(14), // cluster
                new BigDecimal(16)  // rows
        ));

        DeltaSnapshot deltaSnapshot;

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, false, false, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, false, true, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, true, false, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, true, true, null);
        justOneStatementInSecondSnapshotVerifyResult(deltaSnapshot, sqlStatement);
    }

    @Test
    public void skipOneStatementInOneInstanceTest() {
        Calendar fiveMinutesAgo = new GregorianCalendar();
        fiveMinutesAgo.add(Calendar.MINUTE, -5);
        Calendar now = new GregorianCalendar();

        Snapshot snapshotA = new Snapshot(fiveMinutesAgo);
        Snapshot snapshotB = new Snapshot(now);

        SQLStatement sqlStatement = SQLHelper.getSQLStatement("sqlId2", "parsingUser", 0, "SELECT 'just testing' FROM dual");

        snapshotA.addSQLStatementSnapshot(new SQLStatementSnapshot(sqlStatement,
                0, // instance ID
                new BigDecimal(1), // executions
                new BigDecimal(2), // elapsed
                new BigDecimal(3), // cpu
                new BigDecimal(4), // buffer gets
                new BigDecimal(5), // disk reads
                new BigDecimal(6), // concurrency
                new BigDecimal(7), // cluster
                new BigDecimal(8)  // rows
        ));

        snapshotB.addSQLStatementSnapshot(new SQLStatementSnapshot(sqlStatement,
                0, // instance ID
                new BigDecimal(1), // executions
                new BigDecimal(4), // elapsed
                new BigDecimal(6), // cpu
                new BigDecimal(8), // buffer gets
                new BigDecimal(10), // disk reads
                new BigDecimal(12), // concurrency
                new BigDecimal(14), // cluster
                new BigDecimal(16)  // rows
        ));

        DeltaSnapshot deltaSnapshot;

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, false, false, null);
        Assert.assertEquals(0, deltaSnapshot.getDeltaSqlStatementSnapshots().size());

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, false, true, null);
        Assert.assertEquals(0, deltaSnapshot.getDeltaSqlStatementSnapshots().size());

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, true, false, null);
        Assert.assertEquals(0, deltaSnapshot.getDeltaSqlStatementSnapshots().size());

        deltaSnapshot = new DeltaSnapshot(snapshotA, snapshotB, true, true, null);
        Assert.assertEquals(0, deltaSnapshot.getDeltaSqlStatementSnapshots().size());
    }

}
