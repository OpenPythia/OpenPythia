package org.openpythia.utilities.sql;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {
    
    private String snapshotId;
    private List<SQLStatementSnapshot> sqlStatementSnapshots;
    
    public Snapshot(String snapshotId) {
        this.snapshotId = snapshotId;
        
        sqlStatementSnapshots = new ArrayList<SQLStatementSnapshot>();
    }
    
    public String getSnapshotId() {
        return snapshotId;
    }

    public List<SQLStatementSnapshot> getSqlStatementSnapshots() {
        return sqlStatementSnapshots;
    }
    
    public void addSQLStatementSnapshot(SQLStatementSnapshot sqlStatementSnapshot) {
        sqlStatementSnapshots.add(sqlStatementSnapshot);
    }
}
