package org.openpythia.utilities.sql;


/**
 * An ExecutionPlanStep represents one step on an execution plan as found in the
 * library cache of the Oracle database.
 */
public class ExecutionPlanStep {

    private int executionPlanStepId;
    private ExecutionPlanStep parentStep;
    private int parentExecutionPlanStepId;
    private String operation;
    private String options;
    private String objectOwner;
    private String objectName;
    private int depth;
    private int position;
    private int cost;
    private int cardinality;
    private int bytes;
    private int cpuCost;
    private int ioCost;
    private String accessPredicates;
    private String filterPredicates;

    public ExecutionPlanStep(int executionPlanStepId,
            ExecutionPlanStep parentStep, int parentExecutionPlanStepId,
            String operation, String options, String objectOwner,
            String objectName, int depth, int position, int cost,
            int cardinality, int bytes, int cpuCost, int ioCost,
            String accessPredicates, String filterPredicates) {
        this.executionPlanStepId = executionPlanStepId;
        this.parentStep = parentStep;
        // TODO: Do we need this information?
        this.parentExecutionPlanStepId = parentExecutionPlanStepId;
        this.operation = operation;
        this.options = options;
        this.objectOwner = objectOwner;
        this.objectName = objectName;
        this.depth = depth;
        this.position = position;
        this.cost = cost;
        this.cardinality = cardinality;
        this.bytes = bytes;
        this.cpuCost = cpuCost;
        this.ioCost = ioCost;
        this.accessPredicates = accessPredicates;
        this.filterPredicates = filterPredicates;
    }

    public int getExecutionPlanStepId() {
        return executionPlanStepId;
    }

    public ExecutionPlanStep getParentStep() {
        return parentStep;
    }

    public int getParentExecutionPlanStepId() {
        return parentExecutionPlanStepId;
    }

    public String getOperation() {
        return operation;
    }

    public String getOptions() {
        return options;
    }

    public String getObjectOwner() {
        return objectOwner;
    }

    public String getObjectName() {
        return objectName;
    }

    public int getDepth() {
        return depth;
    }

    public int getPosition() {
        return position;
    }

    public int getCost() {
        return cost;
    }

    public int getCardinality() {
        return cardinality;
    }

    public int getBytes() {
        return bytes;
    }

    public int getCpuCost() {
        return cpuCost;
    }

    public int getIoCost() {
        return ioCost;
    }

    public String getAccessPredicates() {
        return accessPredicates;
    }

    public String getFilterPredicates() {
        return filterPredicates;
    }

}
