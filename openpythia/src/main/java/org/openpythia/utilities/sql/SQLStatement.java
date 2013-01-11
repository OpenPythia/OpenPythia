package org.openpythia.utilities.sql;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A SQLStatement represents a SQL statement like found in the library cache of
 * the Oracle database.
 */
public class SQLStatement {

    private String sqlId;
    private String address;
    private String parsingSchema;
    private String sqlText;
    private String normalizedSQLText;

    private List<ExecutionPlan> executionPlans;

    protected SQLStatement(String sqlId, String address, String parsingSchema) {
        this.sqlId = sqlId;
        this.address = address;
        this.parsingSchema = parsingSchema;

        // just to be sure we don't run into problems when accessing the
        // execution plans from different threads
        executionPlans = new CopyOnWriteArrayList<ExecutionPlan>();
    }

    public String getSqlId() {
        return sqlId;
    }

    public String getAddress() {
        return address;
    }

    public String getParsingSchema() {
        return parsingSchema;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
        this.normalizedSQLText = null;
    }

    /**
     * Return the SQL statement with all the string and number literals replaced
     * with question marks.
     * 
     * SQL statements which just differ in contained literals are handled as
     * different SQL statements by the optimizer / database. Fut from some
     * logical perspective they are all the some statement. To identify these
     * statements, the literals have to be removed/replaced. This methods
     * replace the literals with question marks so two statements can easily be
     * compared as being logically identical.
     * 
     * @return The SQL statement with all literals replaced by question marks.
     */
    public String getNormalizedSQLText() {
        if (normalizedSQLText == null) {
            String statementStep0;
            // if the SQL text was not set or if the database returned an empty
            // statement (what happens from time to time) we assume the
            // statement being just a dash (-).
            if (sqlText == null || sqlText.equals("")) {
                statementStep0 = "-";
            } else {
                statementStep0 = sqlText;
            }
            // replace string literals with a question mark
            String statementStep1 = statementStep0.replaceAll("'.+'", "?");

            // replace number literals with a question mark
            String statementStep2 = statementStep1.replaceAll("\\s\\d+", " ?");
            // also those directly after a comma
            String statementStep3 = statementStep2.replaceAll(",\\d+", ",?");

            normalizedSQLText = statementStep3;
        }

        return normalizedSQLText;
    }

    public List<ExecutionPlan> getExecutionPlans() {
        return executionPlans;
    }

    public void addExecutionPlan(ExecutionPlan executionPlan) {
        executionPlans.add(executionPlan);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SQLStatement) {
            SQLStatement statement = (SQLStatement) obj;

            return sqlId.equals(statement.sqlId)
                    && address.equals(statement.address)
                    && isEqual(parsingSchema, statement.parsingSchema);
        } else {
            return false;
        }
    }

    private boolean isEqual(String a, String b) {
        if (a == null && b == null) {
            return true;
        } else if (a == null && b != null) {
            return false;
        } else {
            if (a != null) {
                return a.equals(b);
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        if (parsingSchema != null) {
            return (sqlId + "|" + address + "|" + parsingSchema).hashCode();
        } else {
            return (sqlId + "|" + address).hashCode();
        }
    }
}
