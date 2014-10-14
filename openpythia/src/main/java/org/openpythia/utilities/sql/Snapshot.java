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
import java.util.List;

public class Snapshot implements Serializable {
    
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
