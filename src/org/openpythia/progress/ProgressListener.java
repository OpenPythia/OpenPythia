package org.openpythia.progress;


/**
 * Listen for the progress of a parallel thread.
 * 
 * Often some work is done in a parallel thread - but the user should be kept
 * informed. This interface is implemented by the part of the software that
 * informs the user. The other thread gets an implementation of this interface
 * and reports the progress by calling the methods of this interface.
 */
public interface ProgressListener extends FinishedListener {

    /**
     * On which value does the work start?
     * 
     * @param startValue
     *            The value on which the work starts.
     */
    void setStartValue(int startValue);

    /**
     * On which value will the work be done?
     * 
     * @param endValue
     *            The value on which the work will be done.
     */
    void setEndValue(int endValue);

    /**
     * How much of the work is done?
     * 
     * @param currentValue
     *            The value of items which were already finished.
     */
    void setCurrentValue(int currentValue);
}
