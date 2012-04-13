package org.openpythia.progress;

/**
 * This interface is used to inform a listener of the end of a parallel running
 * task.
 */
public interface FinishedListener {

    /**
     * The task was finished.
     */
    void informFinished();

}
