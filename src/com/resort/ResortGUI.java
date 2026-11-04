package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ResortGUI extends JFrame implements ActionListener {
    private final JMenuBar menuBar = new JMenuBar();
    private final ArrayList<JMenuItem> menuItems = new ArrayList<>();

    private final WelcomePanel welcomePanel = new WelcomePanel();
    private final AccommodationPanel accommodationPanel = new AccommodationPanel();
    private final CustomersPanel customersPanel = new CustomersPanel();
    private final CreateBundlePanel createBundlePanel = new CreateBundlePanel();
    private final BundlesPanel bundlesPanel = new BundlesPanel();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JLabel statusLabel = new JLabel("");

    public ResortGUI() {
        this.setTitle("Mt Buller Resort - Booking Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 680);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);

        //------ All init functions ------//
        createMenuBar();
        tabManager();
        createStatusLabel();

        //------ Always set visible last ------//
        this.setVisible(true);
    }

    private void createMenuBar() {
        // menu bar config
        this.setJMenuBar(this.menuBar);

        // -------- File menu --------
        JMenu fileMenu = new JMenu("File");
        this.menuBar.add(fileMenu);

        JMenuItem dbSave = new JMenuItem("Save to database");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(dbSave);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        // Make accessible throughout class
        menuItems.add(MenuItem.DB_SAVE.ordinal(), dbSave);
        menuItems.add(MenuItem.EXIT.ordinal(), exit);

        // -------- Help menu --------
        JMenu helpMenu = new JMenu("Help");
        this.menuBar.add(helpMenu);

        JMenuItem howToUse = new JMenuItem("How to use");
        JMenuItem about = new JMenuItem("About");
        helpMenu.add(howToUse);
        helpMenu.add(about);

        menuItems.add(MenuItem.HOW_TO_USE.ordinal(), howToUse);
        menuItems.add(MenuItem.ABOUT.ordinal(), about);
    }

    private void tabManager() {
        tabbedPane.addTab("Accommodations", accommodationPanel.getPanel());
        tabbedPane.addTab("Customers", customersPanel.getPanel());
        tabbedPane.addTab("Create Bundle", createBundlePanel.getPanel());
        tabbedPane.addTab("Bundles", bundlesPanel.getPanel());

        this.add(tabbedPane);
    }

    private void createStatusLabel() {
        setStatusText("Testing...");

        this.add(statusLabel, BorderLayout.SOUTH);
    }

    public String setStatusText(String statusText) {
        this.statusLabel.setText(statusText);
        return statusLabel.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        new ResortGUI();
    }
}

enum MenuItem {
    // File Menu
    DB_SAVE,
    EXIT,

    // Help menu
    HOW_TO_USE,
    ABOUT
}
