package org.openpythia.maindialog;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

        view.setVisible(true);
    }

    private void fillSmallViews() {
        pluginControllers = new ArrayList<PythiaPluginController>();
        pluginControllers.add(new HitRatioController(connectionPool));
        pluginControllers.add(new WorstStatementsSmallController(view, this,
                connectionPool));
        // TODO add the other plugin controllers

        for (PythiaPluginController controller : pluginControllers) {
            view.getPanelOverview().add(controller.getSmallView());
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
        view.getPanelDetails().removeAll();
        view.getPanelDetails().add(detailView);
        view.pack();
        view.repaint();
    }

}
