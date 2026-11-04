package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ResortGUI extends JFrame implements ActionListener {
    private final JMenuBar menuBar = new JMenuBar();
    private final ArrayList<JMenuItem> menuItems = new ArrayList<>();

    public ResortGUI() {
        this.setTitle("Mt Buller Resort - Booking Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 680);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        //------ All init functions ------//
        this.createMenuBar();

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
