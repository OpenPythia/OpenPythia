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
package org.openpythia.schemaprivileges;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openpythia.dbconnection.ConnectionPoolUtils;

public class PrivilegesHelper {

    // Yes, I know that these are dynamic views... But they behave like tables.
    private static final String[] USED_TABLES = {
            // buffer cache hit ratio
            "v$sysstat",
            // library cache hit ratio
            "v$librarycache",
            // snapshots, delta V$SQLAREA
            "gv$sqlarea", "gv$sqltext_with_newlines", "gv$sql_plan", "gv$active_session_history",
            // DB parameters
            "v$parameter",
            // missing/stale statistics
            "dba_indexes", "dba_tables",
            // wait events for objects
            "dba_objects"};

    private static final String TRY_SELECT_FROM_TABLE = "SELECT 1 "
            + "FROM %s "
            + "WHERE ROWNUM <= 1";

    public static List<String> getMissingObjectPrivileges() {
        List<String> grantedObjects = new ArrayList<>();

        Connection connection = ConnectionPoolUtils.getConnectionFromPool();
        try {
            for (String tableName : USED_TABLES) {
                try {
                    String sqlStatement = String.format(TRY_SELECT_FROM_TABLE, tableName);
                    PreparedStatement trySelect = connection.prepareStatement(sqlStatement);

                    ResultSet resultSet = trySelect.executeQuery();

                    if (resultSet != null) {
                        grantedObjects.add(tableName);
                    }

                    trySelect.close();

                } catch (SQLException e) {
                    // The user can't access this very table - nothing to do for the moment...
                }
            }
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return getMissingObjectPrivileges(grantedObjects);
    }

    public static List<String> getMissingObjectPrivileges(List<String> grantedObjects) {
        List<String> result = new ArrayList<>();

        // check if there are objects missing
        for (String currentObject : USED_TABLES) {
            if (grantedObjects == null
                    || !grantedObjects.contains(currentObject)) {
                result.add(currentObject);
            }
        }
        return result;
    }

    public static String createGrantScript(List<String> objectsToGrant, String grantee) {
        StringBuilder result = new StringBuilder();

        for (String currentObject : objectsToGrant) {
            result.append("GRANT select ON SYS."
                    + currentObject.replace("v$", "v_$") + " TO " + grantee
                    + ";\n");
        }

        return result.toString();
    }

}
