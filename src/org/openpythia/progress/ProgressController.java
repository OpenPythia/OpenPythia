package org.openpythia.progress;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ProgressController implements ProgressListener {

    private FinishedListener finishedListener;

    private ProgressView view;

    public ProgressController(Frame owner, FinishedListener finishedListener,
            String dialogTitel, String message) {
        this.finishedListener = finishedListener;

        view = new ProgressView(owner);
        view.setTitle(dialogTitel);
        view.getLblMessage().setText(message);

        view.addWindowListener(new CloseWindowListener());

        view.setVisible(true);
    }

    private static class CloseWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            // ignore so the window keeps open
        }
    }

    @Override
    public void informFinished() {
        view.dispose();

        // route the event to the original listener
        finishedListener.informFinished();
    }

    @Override
    public void setStartValue(int startValue) {
        view.getLblStart().setText(String.valueOf(startValue));
        view.getProgressBar().setMinimum(startValue);
    }

    @Override
    public void setEndValue(int endValue) {
        view.getLblEnd().setText(String.valueOf(endValue));
        view.getProgressBar().setMaximum(endValue);
    }

    @Override
    public void setCurrentValue(int currentValue) {
        view.getProgressBar().setValue(currentValue);
    }
}
