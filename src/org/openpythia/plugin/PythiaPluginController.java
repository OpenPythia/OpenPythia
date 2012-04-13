package org.openpythia.plugin;

import javax.swing.JPanel;

public interface PythiaPluginController {

    /**
     * @return The JPanel to be displayed in the left part of the main dialog.
     */
    JPanel getSmallView();

}
