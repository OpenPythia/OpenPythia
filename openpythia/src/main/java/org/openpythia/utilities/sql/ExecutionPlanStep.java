package org.openpythia.utilities.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An ExecutionPlanStep represents one step on an execution plan as found in the
 * library cache of the Oracle database.
 */
public class ExecutionPlanStep {

    private int executionPlanStepId;
    private int parentExecutionPlanStepId;
    private ExecutionPlanStep parentStep;
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

    // The Child-Steps of this step
    private List<ExecutionPlanStep> childSteps = new ArrayList<ExecutionPlanStep>();;

    public ExecutionPlanStep(int executionPlanStepId,
            int parentExecutionPlanStepId, String operation, String options,
            String objectOwner, String objectName, int depth, int position,
            int cost, int cardinality, int bytes, int cpuCost, int ioCost,
            String accessPredicates, String filterPredicates) {

        this.executionPlanStepId = executionPlanStepId;
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

    public int getParentExecutionPlanStepId() {
        return parentExecutionPlanStepId;
    }

    private void setParentExecutionPlanStep(ExecutionPlanStep parentStep) {
        this.parentStep = parentStep;
    }

    public ExecutionPlanStep getParentExecutionPlanStep() {
        return parentStep;
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

    public List<ExecutionPlanStep> getChildSteps() {
        return childSteps;
    }

    public boolean insertStepToCorrectionPositionInStepOrChilds(
            ExecutionPlanStep stepToAdd) {
        boolean stepStored = false;

        if (stepToAdd.parentExecutionPlanStepId == this.executionPlanStepId) {
            // the given step is a child of this step
            addChildStep(stepToAdd);
            stepToAdd.setParentExecutionPlanStep(this);

            stepStored = true;
        } else {
            // the given step belongs to one of the child elements
            Iterator<ExecutionPlanStep> stepIterator = childSteps.iterator();
            while (!stepStored && stepIterator.hasNext()) {
                stepStored = stepIterator
                        .next()
                        .insertStepToCorrectionPositionInStepOrChilds(stepToAdd);
            }
        }
        return stepStored;
    }

    private void addChildStep(ExecutionPlanStep childStep) {
        int insertAt = -1;
        for (ExecutionPlanStep currentStep : childSteps) {
            if (currentStep.position > childStep.position) {
                insertAt = childSteps.indexOf(currentStep);
            }
        }
        if (insertAt == -1) {
            childSteps.add(childStep);
        } else {
            childSteps.add(insertAt, childStep);
        }
    }
}
