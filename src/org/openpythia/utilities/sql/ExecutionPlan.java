package org.openpythia.utilities.sql;

/**
 * An execution plan as retrieved by the library cache of the Oracle database.
 */
public class ExecutionPlan {

    private int childNumber;
    private String address;
    private ExecutionPlanStep parentStep;

    public ExecutionPlan(int childNumber, String address) {
        this.childNumber = childNumber;
        this.address = address;
    }

    public ExecutionPlanStep getParentStep() {
        return parentStep;
    }

    public void setParentStep(ExecutionPlanStep parentStep) {
        this.parentStep = parentStep;
    }

    public int getChildNumber() {
        return childNumber;
    }

    public String getAddress() {
        return address;
    }

}
