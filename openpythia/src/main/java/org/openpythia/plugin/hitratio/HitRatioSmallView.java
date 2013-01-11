/*
 * Created by JFormDesigner on Fri Jun 03 09:28:03 CEST 2011
 */

package org.openpythia.plugin.hitratio;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
public class HitRatioSmallView extends JPanel {
	
	// The dialogs are not designed to be serialized. But to avoid the warnings...
	private static final long serialVersionUID = 1L;

    public HitRatioSmallView() {
        initComponents();
    }

    public JTextField getTfBufferCacheHitRatio() {
        return tfBufferCacheHitRatio;
    }

    public JTextField getTfLibraryCacheHitRatio() {
        return tfLibraryCacheHitRatio;
    }

    public JLabel getLblIconBufferCacheHitRatio() {
        return lblIconBufferCacheHitRatio;
    }

    public JLabel getLblIconLibraryCacheHitRatio() {
        return lblIconLibraryCacheHitRatio;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        tfBufferCacheHitRatio = new JTextField();
        lblIconBufferCacheHitRatio = new JLabel();
        label2 = new JLabel();
        tfLibraryCacheHitRatio = new JTextField();
        lblIconLibraryCacheHitRatio = new JLabel();

        //======== this ========
        setBorder(new TitledBorder("Hit Ratios"));
        setMaximumSize(new Dimension(400, 83));
        setLayout(new FormLayout(
            "110dlu, $lcgap, 35dlu, $lcgap, default",
            "default, $lgap, default"));

        //---- label1 ----
        label1.setText("Buffer Cache Hit Ratio");
        add(label1, CC.xy(1, 1));

        //---- tfBufferCacheHitRatio ----
        tfBufferCacheHitRatio.setEditable(false);
        add(tfBufferCacheHitRatio, CC.xy(3, 1));

        //---- lblIconBufferCacheHitRatio ----
        lblIconBufferCacheHitRatio.setIcon(new ImageIcon(getClass().getResource("/circle-metal-24-ns.png")));
        add(lblIconBufferCacheHitRatio, CC.xy(5, 1));

        //---- label2 ----
        label2.setText("Library Cache Hit Ratio");
        add(label2, CC.xy(1, 3));

        //---- tfLibraryCacheHitRatio ----
        tfLibraryCacheHitRatio.setEditable(false);
        add(tfLibraryCacheHitRatio, CC.xy(3, 3));

        //---- lblIconLibraryCacheHitRatio ----
        lblIconLibraryCacheHitRatio.setIcon(new ImageIcon(getClass().getResource("/circle-metal-24-ns.png")));
        add(lblIconLibraryCacheHitRatio, CC.xy(5, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField tfBufferCacheHitRatio;
    private JLabel lblIconBufferCacheHitRatio;
    private JLabel label2;
    private JTextField tfLibraryCacheHitRatio;
    private JLabel lblIconLibraryCacheHitRatio;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
