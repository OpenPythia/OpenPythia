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

import java.math.BigDecimal;

public class SQLStatementSnapshot {

    private SQLStatement sqlStatement;
    private int instanceId;
    private BigDecimal executions;
    private BigDecimal elapsedSeconds;
    private BigDecimal cpuSeconds;
    private BigDecimal bufferGets;
    private BigDecimal diskReads;
    private BigDecimal rowsProcessed;

    public SQLStatementSnapshot(SQLStatement sqlStatement,
                                int instanceId,
                                BigDecimal executions,
                                BigDecimal elapsedSeconds,
                                BigDecimal cpuSeconds,
                                BigDecimal bufferGets,
                                BigDecimal diskReads,
                                BigDecimal rowsProcessed) {
        this.sqlStatement = sqlStatement;
        this.instanceId = instanceId;
        this.executions = executions;
        this.elapsedSeconds = elapsedSeconds;
        this.cpuSeconds = cpuSeconds;
        this.bufferGets = bufferGets;
        this.diskReads = diskReads;
        this.rowsProcessed = rowsProcessed;
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

    public BigDecimal getRowsProcessed() {
        return rowsProcessed;
    }
}
