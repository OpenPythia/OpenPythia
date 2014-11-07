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
import java.math.BigDecimal;

public class SQLStatementSnapshot implements Serializable {

    private SQLStatement sqlStatement;
    private int instanceId;
    private BigDecimal executions;
    private BigDecimal elapsedSeconds;
    private BigDecimal cpuSeconds;
    private BigDecimal bufferGets;
    private BigDecimal diskReads;
    private BigDecimal concurrencySeconds;
    private BigDecimal clusterSeconds;
    private BigDecimal rowsProcessed;
    private BigDecimal numberStatements;

    public SQLStatementSnapshot(SQLStatement sqlStatement,
                                int instanceId,
                                BigDecimal executions,
                                BigDecimal elapsedSeconds,
                                BigDecimal cpuSeconds,
                                BigDecimal bufferGets,
                                BigDecimal diskReads,
                                BigDecimal concurrencySeconds,
                                BigDecimal clusterSeconds,
                                BigDecimal rowsProcessed) {
        this.sqlStatement = sqlStatement;
        this.instanceId = instanceId;
        this.executions = executions;
        this.elapsedSeconds = elapsedSeconds;
        this.cpuSeconds = cpuSeconds;
        this.bufferGets = bufferGets;
        this.diskReads = diskReads;
        this.concurrencySeconds = concurrencySeconds;
        this.clusterSeconds = clusterSeconds;
        this.rowsProcessed = rowsProcessed;
    }

    public SQLStatementSnapshot(SQLStatement sqlStatement,
                                int instanceId,
                                BigDecimal executions,
                                BigDecimal elapsedSeconds,
                                BigDecimal cpuSeconds,
                                BigDecimal bufferGets,
                                BigDecimal diskReads,
                                BigDecimal concurrencySeconds,
                                BigDecimal clusterSeconds,
                                BigDecimal rowsProcessed,
                                BigDecimal numberStatements) {

        this(sqlStatement, instanceId, executions, elapsedSeconds, cpuSeconds, bufferGets,
                diskReads, concurrencySeconds, clusterSeconds, rowsProcessed);

        this.numberStatements = numberStatements;
    }

    public SQLStatement getSqlStatement() {
        return sqlStatement;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public BigDecimal getExecutions() {
        return executions;
    }

    public BigDecimal getElapsedSeconds() {
        return elapsedSeconds;
    }

    public BigDecimal getCpuSeconds() {
        return cpuSeconds;
    }

    public BigDecimal getBufferGets() {
        return bufferGets;
    }

    public BigDecimal getDiskReads() {
        return diskReads;
    }

    public BigDecimal getConcurrencySeconds() { return concurrencySeconds; }

    public BigDecimal getClusterSeconds() {
        return clusterSeconds;
    }

    public BigDecimal getRowsProcessed() {
        return rowsProcessed;
    }

    public BigDecimal getNumberStatements() {
        return numberStatements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SQLStatementSnapshot) {
            SQLStatementSnapshot that = (SQLStatementSnapshot) obj;

            if (this.numberStatements == null && that.numberStatements == null) {
                // not condensed for missing bind variables
                return this.getSqlStatement().equals(that.getSqlStatement());
            } else {
                // SQL statements with missing bind variables have to be compared by the
                // normalized SQL statement
                return this.getSqlStatement().getNormalizedSQLText().equals(
                        that.getSqlStatement().getNormalizedSQLText());
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.numberStatements == null) {
            return this.getSqlStatement().hashCode();
        } else {
            return this.getSqlStatement().getNormalizedSQLText().hashCode();
        }
    }
}
