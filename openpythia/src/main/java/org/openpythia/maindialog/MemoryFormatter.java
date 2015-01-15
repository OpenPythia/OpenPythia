package org.openpythia.maindialog;

public class MemoryFormatter {

    private static final float toKB = 1024;
    private static final float toMB = toKB * 1024;
    private static final float toGB = toMB * 1024;

    public static String formatMemoryInBytes(long memory) {
        String result;

        if (memory / toGB > 1) {
            // more than a GB RAM
            int fullGB = (int) (memory / toGB);
            int partGB = (int) ((memory / toGB - fullGB) * 100);
            result = String.format("%dG%d", fullGB, partGB);
        } else if (memory / toMB > 1) {
            // less than a GB RAM but more than a MB
            int fullMB = (int) (memory / toMB);
            int partMB = (int) ((memory / toMB - fullMB) * 100);
            result = String.format("%dM%d", fullMB, partMB);
        } else {
            // less than a MB
            int fullKB = (int) (memory / toKB);
            int partKB = (int) ((memory / toKB - fullKB) * 100);
            result = String.format("%dK%d", fullKB, partKB);
        }

        return result;
    }
}
