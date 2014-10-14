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

/**
 * An execution plan as retrieved by the library cache of the Oracle database.
 */
public class ExecutionPlan implements Serializable {

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
