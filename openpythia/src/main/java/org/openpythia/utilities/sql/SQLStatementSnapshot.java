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

public class SQLStatementSnapshot {

    private SQLStatement sqlStatement;
    private int executions;
    private int elapsedSeconds;
    private int cpuSeconds;
    private int bufferGets;
    private int diskReads;
    private int rowsProcessed;

    public SQLStatementSnapshot(SQLStatement sqlStatement, int executions,
            int elapsedSeconds, int cpuSeconds, int bufferGets, int diskReads,
            int rowsProcessed) {
        this.sqlStatement = sqlStatement;
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

    public int getExecutions() {
        return executions;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public int getCpuSeconds() {
        return cpuSeconds;
    }

    public int getBufferGets() {
        return bufferGets;
    }

    public int getDiskReads() {
        return diskReads;
    }

    public int getRowsProcessed() {
        return rowsProcessed;
    }

}
