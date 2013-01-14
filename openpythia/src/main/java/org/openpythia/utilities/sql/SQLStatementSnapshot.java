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
