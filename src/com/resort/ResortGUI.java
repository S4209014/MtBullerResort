package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Manages all the GUI elements in the program and calls on other classes to retrieve information to display.
 * <p>
 *     This class replaces the original CLI program by calling on its same methods and displaying it in a GUI format
 *     utilising Java Swing components.
 * </p>
 * <p>
 *     It is effectively a manager, a child of {@link JFrame} and implementing {@link ActionListener}.
 * </p>
 * @author BrodieCousins
 */
public class ResortGUI extends JFrame implements ActionListener {
    private final JMenuBar menuBar = new JMenuBar();
    private final EnumMap<MenuAction, JMenuItem> menuItems = new EnumMap<>(MenuAction.class);

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final WelcomePanel welcomePanel = new WelcomePanel();
    private final AccommodationPanel accommodationPanel = new AccommodationPanel();
    private final CustomersPanel customersPanel = new CustomersPanel();
    private final CreateBundlePanel createBundlePanel = new CreateBundlePanel();
    private final BundlesPanel bundlesPanel = new BundlesPanel();

    private final JLabel statusLabel = new JLabel("");

    /**
     * Running this function will create a window and initialise all functions for the program to be interacted with.
     * <p>
     *     It uses {@link GridBagLayout} as it's LayoutManager.
     * </p>
     * @see GridBagConstraints
     */
    public ResortGUI() {
        this.setTitle("Mt Buller Resort - Booking Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 680);
        this.setLayout(new GridBagLayout());
        this.setLocationRelativeTo(null);

        //------ All init functions ------//
        createMenuBar();
        tabManager();
        createStatusLabel();

        //------ Always set visible last ------//
        this.setVisible(true);
    }

    /**
     * Creates, initialises and manages all menu items across the program.
     * <p>
     *     It stores all of the instantiated menu items in an EnumMap called {@code menuItems}, where
     *     {@link com.resort.MenuAction} acts as the key and the value is the MenuItem.
     * </p>
     * @see JMenu
     */
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
        menuItems.put(MenuAction.DB_SAVE, dbSave);
        menuItems.put(MenuAction.EXIT, exit);

        // -------- Help menu --------
        JMenu helpMenu = new JMenu("Help");
        this.menuBar.add(helpMenu);

        JMenuItem howToUse = new JMenuItem("How to use");
        JMenuItem about = new JMenuItem("About");
        helpMenu.add(howToUse);
        helpMenu.add(about);

        menuItems.put(MenuAction.HOW_TO_USE, howToUse);
        menuItems.put(MenuAction.ABOUT, about);
    }

    /**
     * Instantiates and initialises all the tabbed panes in the navigation of the program.
     */
    private void tabManager() {
        tabbedPane.addTab("Accommodations", accommodationPanel.getPanel());
        tabbedPane.addTab("Customers", customersPanel.getPanel());
        tabbedPane.addTab("Create Bundle", createBundlePanel.getPanel());
        tabbedPane.addTab("Bundles", bundlesPanel.getPanel());

        this.add(tabbedPane);
    }

    /**
     * This program initialises a permanent status label at the bottom of program that displays the latest action.
     */
    private void createStatusLabel() {
        setStatusText("Testing...");

        this.add(statusLabel);
    }

    /**
     * This sets the text of the status bar at the bottom of the program.
     * <p>
     *     All actions made by the user <strong>must</strong> be displayed in the status bar.
     * </p>
     * @param statusText The string of text you want to display.
     */
    public void setStatusText(String statusText) {
        this.statusLabel.setText(statusText);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        new ResortGUI();
    }
}

enum MenuAction {
    // File Menu
    DB_SAVE,
    EXIT,

    // Help menu
    HOW_TO_USE,
    ABOUT
}
