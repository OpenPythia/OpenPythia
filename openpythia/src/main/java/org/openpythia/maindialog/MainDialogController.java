package org.openpythia.maindialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.openpythia.aboutdialog.AboutController;
import org.openpythia.dbconnection.ConnectionPool;
import org.openpythia.main.PythiaMain;
import org.openpythia.plugin.MainDialog;
import org.openpythia.plugin.PythiaPluginController;
import org.openpythia.plugin.hitratio.HitRatioController;
import org.openpythia.plugin.worststatements.WorstStatementsSmallController;

public class MainDialogController implements MainDialog {

    private ConnectionPool connectionPool;

    private MainDialogView view;
    private List<PythiaPluginController> pluginControllers;

    public MainDialogController(ConnectionPool connectionPool) {

        this.connectionPool = connectionPool;

        view = new MainDialogView();

        bindMenus();
        fillSmallViews();
        view.addWindowListener(new CloseWindowListener());

        view.getPanelDetails().setVisible(false);
        view.pack();

        view.setVisible(true);
    }

    private void fillSmallViews() {
        pluginControllers = new ArrayList<PythiaPluginController>();
        pluginControllers.add(new HitRatioController(connectionPool));
        pluginControllers.add(new WorstStatementsSmallController(view, this,
                connectionPool));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx  = 0;
        constraints.gridy  = 0;
        constraints.fill   = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;

        for (PythiaPluginController controller : pluginControllers) {
            view.getPanelOverview().add(controller.getSmallView(), constraints);
            constraints.gridy++;
        }
    }

    private void bindMenus() {
        view.getMiQuit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExitApplication();
            }
        });
        view.getMiOnlineHelp().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOnlineHelp();
            }
        });
        view.getMiAbout().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
    }

    private static class CloseWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            PythiaMain.gracefullExit();
        }
    }

    private void ExitApplication() {
        PythiaMain.gracefullExit();
    }

    private void openOnlineHelp() {
        try {
            Desktop.getDesktop()
                    .browse(new URI(
                            "https://andy-net.de/pythia/wiki/bin/view/Main/OnlineHelpStart"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog((Component) view,
                    "The online help could not be opened.\n"
                            + "The error message is " + e.toString());
        }
    }

    private void showAboutDialog() {
        new AboutController(view);
    }

    @Override
    public void showDetailView(JPanel detailView) {
        if(!view.getPanelDetails().isVisible()) {
            view.getPanelDetails().setVisible(true);
        }

        view.getPanelDetails().removeAll();
        view.getPanelDetails().add(new JScrollPane(detailView));
        view.pack();
        view.repaint();
    }

}
