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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A SQLStatement represents a SQL statement like found in the library cache of
 * the Oracle database.
 */
public class SQLStatement implements Serializable {

    private String sqlId;
    private String address;
    private String parsingSchema;
    private int instanceId;
    private String sqlText;
    private String normalizedSQLText;

    private List<ExecutionPlan> executionPlans;

    protected SQLStatement(String sqlId, String address, String parsingSchema, int instanceId) {
        this.sqlId = sqlId;
        this.address = address;
        this.parsingSchema = parsingSchema;
        this.instanceId = instanceId;

        // just to be sure we don't run into problems when accessing the
        // execution plans from different threads
        executionPlans = new CopyOnWriteArrayList<>();
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

    public int getInstanceId() {
        return instanceId;
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
     * different SQL statements by the optimizer / database. But from some
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
            String statementStep1 = statementStep0.replaceAll("'.+?'", "?");

            // replace number literals with a question mark
            String statementStep2 = statementStep1.replaceAll("\\s\\d+", " ?");
            // also those directly after a comma
            String statementStep3 = statementStep2.replaceAll(",\\d+", ",?");
            // also those directly after a equals
            String statementStep4 = statementStep3.replaceAll("=\\d+", "=?");
            // also those directly after a smaller
            String statementStep5 = statementStep4.replaceAll("<\\d+", "<?");
            // also those directly after a bigger
            String statementStep6 = statementStep5.replaceAll(">\\d+", ">?");
            // also those directly after a opening bracket
            String statementStep7 = statementStep6.replaceAll("\\(\\d+", "(?");

            normalizedSQLText = statementStep7;
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
            return (sqlId + "|" + parsingSchema).hashCode();
        } else {
            return sqlId.hashCode();
        }
    }
}
