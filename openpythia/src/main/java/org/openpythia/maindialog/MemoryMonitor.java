package org.openpythia.maindialog;

import javax.swing.*;

public class MemoryMonitor implements Runnable {

    private JProgressBar progressBar;
    private JLabel maxMemoryLabel;
    private Runtime runtime;

    private final int toMB = 1024 * 1024;

    public MemoryMonitor(JProgressBar progressBar, JLabel maxMemoryLabel) {
        this.progressBar = progressBar;
        this.maxMemoryLabel = maxMemoryLabel;

        runtime  = Runtime.getRuntime();

        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
    }

    @Override
    public void run() {

        //noinspection InfiniteLoopStatement
        while (true) {
            maxMemoryLabel.setText(MemoryFormatter.formatMemoryInBytes(runtime.maxMemory()));

            progressBar.setMaximum((int)(runtime.maxMemory() / toMB));
            long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
            int memoryUsedMb = (int)(memoryUsed / toMB);
            progressBar.setValue(memoryUsedMb);
            progressBar.setString(MemoryFormatter.formatMemoryInBytes(memoryUsed));

            try {
                // update each second - so wait a second for the next run
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // we don't care for being interrupted
            }
        }
    }
}
