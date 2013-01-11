package org.openpythia.schemaprivileges;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openpythia.dbconnection.ConnectionPool;

public class PrivilegesHelper {

    // Yes, I know that these are dynamic views... But they behave like tables.
    private static final String[] USED_TABLES = {
            // buffer cache hit ratio
            "v$sysstat",
            // library cache hit ratio
            "v$librarycache",
            // snapshots, delta V$SQLARE
            "v$sqlarea", "v$sqltext_with_newlines", "v$sql_plan",
            // DB parameters
            "v_$parameter",
            // missing/stale statistics
            "dba_indexes", "dba_tables" };

    private static final String SELECT_USER_PRIVILEGES = "SELECT table_name "
            + "FROM user_tab_privs "
            + "WHERE grantor = 'SYS' AND privilege = 'SELECT'";

    public static List<String> getMissingObjectPrivileges(
            List<String> grantedObjects) {
        List<String> result = new ArrayList<String>();

        // check if there are objects missing
        for (String currentObject : USED_TABLES) {
            if (grantedObjects == null
                    || !grantedObjects.contains(currentObject.toLowerCase()
                            .replace("v$", "v_$"))) {
                result.add(currentObject);
            }
        }
        return result;
    }

    public static List<String> getMissingObjectPrivileges(
            ConnectionPool connectionPool) {

        // get a list with all granted objects
        List<String> grantedObjects = new ArrayList<String>();
        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement grantedObjectsStatement = connection
                    .prepareStatement(SELECT_USER_PRIVILEGES);

            ResultSet grantedObjectsResultSet = grantedObjectsStatement
                    .executeQuery();

            if (grantedObjectsResultSet != null) {
                while (grantedObjectsResultSet.next()) {
                    grantedObjects.add(grantedObjectsResultSet.getString(1)
                            .toLowerCase());
                }
            }

            grantedObjectsStatement.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } finally {
            connectionPool.giveConnectionBack(connection);
        }

        return getMissingObjectPrivileges(grantedObjects);
    }

    public static String createGrantScript(List<String> objectsToGrant,
            String grantee) {
        StringBuffer result = new StringBuffer();

        for (String currentObject : objectsToGrant) {
            result.append("GRANT select ON SYS."
                    + currentObject.replace("v$", "v_$") + " TO " + grantee
                    + ";\n");
        }

        return result.toString();
    }

}
