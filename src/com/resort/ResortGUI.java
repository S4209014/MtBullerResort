package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
 * <p>
 *     This class implements the Layout Manager: {@link GridBagLayout}
 * </p>
 * @see GridBagConstraints
 * @author BrodieCousins
 */
public class ResortGUI extends JFrame implements ActionListener {
    private final MtBullerResort resort = new MtBullerResort();

    private final JMenuBar menuBar = new JMenuBar();
    private final EnumMap<MenuAction, JMenuItem> menuItems = new EnumMap<>(MenuAction.class);

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final WelcomePanel welcomePanel = new WelcomePanel(resort, this);
    private final AccommodationPanel accommodationPanel = new AccommodationPanel(resort, this);
    private final CustomersPanel customersPanel = new CustomersPanel(resort, this);
    private final CreateBundlePanel createBundlePanel = new CreateBundlePanel(resort, this);
    private final BundlesPanel bundlesPanel = new BundlesPanel(resort, this);

    private final JLabel statusLabel = new JLabel("");

    private static final Color primaryColour = new Color(0x1d2e52);
    private final int barWidth = 33; // this is how many characters the header and equal sign bars are in a line.

    /**
     * Running this function will create a window and initialise all functions for the program to be interacted with.
     * <p>
     *     It uses {@link GridBagLayout} as it's LayoutManager.
     * </p>
     * @see GridBagConstraints
     */
    public ResortGUI() {
        resort.setupDefaults();
        // Standard GUI configuration
        this.setTitle("Mt Buller Resort - Booking Manager");
        // The close button runs the same safe-exit code as File > Exit (confirm, save, then quit)
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitProgram();
            }
        });
        this.setSize(1100, 680);
        this.setLayout(new GridBagLayout());
        this.setLocationRelativeTo(null); // Spawns window in the middle of the screen
        this.setMinimumSize(new Dimension(900, 650));

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

        // One listener (this class) handles every menu item
        for (JMenuItem item : menuItems.values()) {
            item.addActionListener(this);
        }
    }

    /**
     * Handles all menu item clicks by looking up which {@link MenuAction} was pressed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        for (MenuAction action : menuItems.keySet()) {
            if (e.getSource() == menuItems.get(action)) {
                switch (action) {
                    case DB_SAVE -> saveToDatabase();
                    case EXIT -> exitProgram();
                    case HOW_TO_USE -> showHowToUse();
                    case ABOUT -> showAbout();
                }
            }
        }
    }

    /**
     * Writes every bundle to the SQLite database and tells the user how it went.
     * @return true if the save worked (or there was nothing to save)
     */
    private boolean saveToDatabase() {
        if (resort.getBundles().isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no bundles to save yet.",
                    "Nothing to save", JOptionPane.INFORMATION_MESSAGE);
            setStatusText("No bundles to save");
            return true;
        }
        try {
            resort.saveToDatabase();
            String message = resort.getBundles().size() + " bundle(s) saved to " + MtBullerResort.DB_FILE + ".";
            JOptionPane.showMessageDialog(this, message, "Saved", JOptionPane.INFORMATION_MESSAGE);
            setStatusText(message);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save to " + MtBullerResort.DB_FILE + ": " + ex.getMessage(),
                    "Save failed", JOptionPane.ERROR_MESSAGE);
            setStatusText("Save failed");
            return false;
        }
    }

    /**
     * Asks before quitting, saves everything to the database, then closes the program.
     * Used by both File &gt; Exit and the window's close button.
     */
    private void exitProgram() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Save all bundles to the database and exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        if (!saveToDatabase()) {
            int quitAnyway = JOptionPane.showConfirmDialog(this,
                    "Your data could not be saved. Exit anyway?", "Exit without saving",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (quitAnyway != JOptionPane.YES_OPTION) {
                return;
            }
        }
        this.dispose();
        System.exit(0);
    }

    private void showHowToUse() {
        JOptionPane.showMessageDialog(this,
                """
                Accommodation: show all rooms, or filter by dates, nights, guests and type.
                Customers: add a customer and list everyone (with family members).
                Create Bundle: pick a customer, dates and family, find a room, then Create Bundle.
                Bundles: select a bundle to add lift passes and lessons per person.
                File > Save to database stores your bundles. Closing the window saves too.""",
                "How to use", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Mt Buller Resort - Booking Manager\nCustom travel bundles for customers and their families.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Instantiates and adds all the tabbed panes in the navigation of the program.
     */
    private void tabManager() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0 ;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 8, 2, 8); // top, left, bottom, right

        tabbedPane.addTab("Accommodation", createWelcomeScreen());
        tabbedPane.addTab("Customers", customersPanel);
        tabbedPane.addTab("Create Bundle", createBundlePanel);
        tabbedPane.addTab("Bundles", bundlesPanel);

        // Disable the 3 tabs until the welcome screen is clicked
        int[] enabledTabbedPaneIndex = new int[]{1, 2, 3};
        boolean[] enabledTabbedPanes = new boolean[]{false, false, false};
        setTabEnabled(enabledTabbedPaneIndex, enabledTabbedPanes);

        // Refresh a tab's data whenever it is opened, so it reflects changes made in other tabs
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected == createBundlePanel) {
                createBundlePanel.refreshCustomers();
            } else if (selected == bundlesPanel) {
                bundlesPanel.refreshBundles();
            }
        });

        this.add(tabbedPane, gbc);
    }

    /**
     * This method allows you to set which tabs in tabbedPane are enabled vs disabled.
     * <p>
     * </p>
     * @param index Specifies which index's of tabbedPane to edit / redefine
     * @param enabled Matching the order of the index array, true enables the tab to be clicked false disables
     */
    private void setTabEnabled(int[] index, boolean[] enabled) {
        for (int i=0; i < index.length; i++) {
            tabbedPane.setEnabledAt(index[i], enabled[i]);
        }
    }

    /**
     * This method creates a cardLayout to display and remove the welcome screen on top of the accommodations panel
     */
    private JPanel createWelcomeScreen() {
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        cardPanel.add(welcomePanel, String.valueOf(ResortPanelType.WELCOME));
        cardPanel.add(accommodationPanel, String.valueOf(ResortPanelType.ACCOMMODATION));

        // Add listener for welcome button
        welcomePanel.addEnterButtonListener(e -> welcomeButtonPressed(cardLayout, cardPanel));

        return cardPanel;
    }

    /**
     * This method is called when the welcomeButton on the welcomeScreen is pressed
     * @param cardLayout
     * @param cardPanel
     */
    private void welcomeButtonPressed(CardLayout cardLayout, JPanel cardPanel) {
        cardLayout.show(cardPanel, ResortPanelType.ACCOMMODATION.name());

        // The welcome screen only appears once, so remove it for good
        cardPanel.remove(welcomePanel);
        cardPanel.revalidate();
        cardPanel.repaint();
        setStatusText(String.format("Loaded %d accommodations · %d customers · %d bundles",
                resort.getAccommodations().size(), resort.getCustomers().size(), resort.getBundles().size()));

        // Re-enable tabs
        int[] tabsIndex = {1, 2, 3}; // Customers, Create Bundle, Bundles
        boolean[] tabsEnabled = {true, true, true}; // Enable all tabs;
        setTabEnabled(tabsIndex, tabsEnabled);
    }

    /**
     * This program initialises a permanent status label at the bottom of program that displays the latest action.
     */
    private void createStatusLabel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(2, 4, 4, 4);
        gbc.ipadx = 4;
        gbc.ipady = 4;
        setStatusText("Welcome - click Enter to begin");

        this.add(statusLabel, gbc);
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

    public static Color getPrimaryColour() {
        return primaryColour;
    }

    /**
     * This method takes in a date and checks if it was in the past. You can also check whether
     * it is too far into the future.
     * @param date The date you want to check (yyyy-MM-dd)
     * @param limitTwoYears If you want to limit it to only 2 years into the future too.
     * @return Only returns a string as a feedback warning e.g "cannot be in past"
     */
    public static String dateInPastCheck(LocalDate date, boolean limitTwoYears) {
        try {
            if (date.isBefore(LocalDate.now())) {
                return "The date cannot be in the past. Please try again";
            }
            if (limitTwoYears && date.isAfter(LocalDate.now().plusYears(2))) {
                return "Let's not plan that far ahead - 2 years out is the limit.";
            }
            return "";
        } catch (DateInPastException e) {
            return e.getMessage();
        } catch (DateTimeParseException e) {
            return "Use yyyy-MM-dd  (e.g. 2026-09-01)";
        }
    }

    public String returnHeaderString (String title) {
        return labelledBar(title) + "\n";
    }

    public String returnBarString () {
        return fill('=') + "\n";
    }

    private String fill(char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < barWidth; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // =====  TITLE  =====  same width as the welcome bars
    private String labelledBar(String title) {
        String mid = "  " + title + "  ";
        int leftover = barWidth - mid.length();
        if (leftover < 2) {
            leftover = 2;
        }
        int left = leftover / 2;
        int right = leftover - left;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) {
            sb.append('=');
        }
        sb.append(mid);
        for (int i = 0; i < right; i++) {
            sb.append('=');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ResortGUI();
            }
        });
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
