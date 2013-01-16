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
package org.openpythia.plugin.hitratio;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
public class HitRatioSmallView extends JPanel {
    public HitRatioSmallView() {
        initComponents();
    }

    public JTextField getTfBufferCacheHitRatio() {
        return tfBufferCacheHitRatio;
    }

    public JLabel getLblIconBufferCacheHitRatio() {
        return lblIconBufferCacheHitRatio;
    }

    public JTextField getTfLibraryCacheHitRatio() {
        return tfLibraryCacheHitRatio;
    }

    public JLabel getLblIconLibraryCacheHitRatio() {
        return lblIconLibraryCacheHitRatio;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        tfBufferCacheHitRatio = new JTextField();
        label3 = new JLabel();
        lblIconBufferCacheHitRatio = new JLabel();
        label2 = new JLabel();
        tfLibraryCacheHitRatio = new JTextField();
        label4 = new JLabel();
        lblIconLibraryCacheHitRatio = new JLabel();

        //======== this ========
        setBorder(new TitledBorder("Hit Ratios"));
        setMaximumSize(new Dimension(400, 83));
        setLayout(new FormLayout(
            "left:110dlu, $lcgap, 35dlu, 0px, default, $ugap, left:default:grow",
            "default, $lgap, default"));

        //---- label1 ----
        label1.setText("Buffer Cache Hit Ratio");
        add(label1, CC.xy(1, 1));

        //---- tfBufferCacheHitRatio ----
        tfBufferCacheHitRatio.setEditable(false);
        tfBufferCacheHitRatio.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tfBufferCacheHitRatio, CC.xy(3, 1));

        //---- label3 ----
        label3.setText("%");
        add(label3, CC.xy(5, 1));

        //---- lblIconBufferCacheHitRatio ----
        lblIconBufferCacheHitRatio.setIcon(new ImageIcon(getClass().getResource("/circle-metal-24-ns.png")));
        add(lblIconBufferCacheHitRatio, CC.xy(7, 1));

        //---- label2 ----
        label2.setText("Library Cache Hit Ratio");
        add(label2, CC.xy(1, 3));

        //---- tfLibraryCacheHitRatio ----
        tfLibraryCacheHitRatio.setEditable(false);
        tfLibraryCacheHitRatio.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tfLibraryCacheHitRatio, CC.xy(3, 3));

        //---- label4 ----
        label4.setText("%");
        add(label4, CC.xy(5, 3));

        //---- lblIconLibraryCacheHitRatio ----
        lblIconLibraryCacheHitRatio.setIcon(new ImageIcon(getClass().getResource("/circle-metal-24-ns.png")));
        add(lblIconLibraryCacheHitRatio, CC.xy(7, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JTextField tfBufferCacheHitRatio;
    private JLabel label3;
    private JLabel lblIconBufferCacheHitRatio;
    private JLabel label2;
    private JTextField tfLibraryCacheHitRatio;
    private JLabel label4;
    private JLabel lblIconLibraryCacheHitRatio;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
