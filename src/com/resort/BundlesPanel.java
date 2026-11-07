package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Bundles tab: list bundles with details (function 6), add lift passes (function 7) and lessons (function 8).
 */
public class BundlesPanel extends ResortPanel implements ActionListener {
    // ---- Bundles list ----
    private final DefaultListModel<TravelBundle> bundleModel = new DefaultListModel<>();
    private final JList<TravelBundle> bundleList = new JList<>(bundleModel);
    private final JButton listAllBundles = new JButton("List All Bundles");

    // ---- Lift pass ----
    private final JComboBox<PersonOption> liftPerson = new JComboBox<>();
    private final JRadioButton dayPassRadio = new JRadioButton("Day passes", true);
    private final JRadioButton seasonPassRadio = new JRadioButton("Season pass");
    private final JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));
    private final JButton addLiftPass = new JButton("Add Lift Pass");

    // ---- Lessons ----
    private final JComboBox<PersonOption> lessonPerson = new JComboBox<>();
    private final JTextField lessonLevelField = new JTextField(14);
    private final JSpinner lessonsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
    private final JButton addLessons = new JButton("Add Lessons");

    // ---- Details ----
    private final JTextArea detailsText = outputArea();

    BundlesPanel(MtBullerResort resort, ResortGUI gui) {
        super("Bundles", resort, gui);
        createComponents();
    }

    @Override
    protected void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(8, 4, 4, 4);
        this.add(initBundleListPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.30;
        this.add(initActionsPanel(), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.45;
        this.add(initDetailsPanel(), gbc);

        updateEnabledState();
        refreshBundles();
    }

    // ------------------------------------------------------------------ panels

    private JPanel initBundleListPanel() {
        JPanel panel = titledPanel("Bundles");

        bundleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bundleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onBundleSelected();
            }
        });
        GridBagConstraints gbc = cell(0, 0);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(bundleList), gbc);

        gbc = cell(0, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        listAllBundles.addActionListener(this);
        panel.add(listAllBundles, gbc);
        return panel;
    }

    private JPanel initActionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panel.add(initLiftPassPanel(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0; // spare height goes below the two panels
        panel.add(initLessonsPanel(), gbc);
        return panel;
    }

    private JPanel initLiftPassPanel() {
        JPanel panel = titledPanel("Lift pass");

        addRow(panel, 0, "Person", liftPerson, null);

        ButtonGroup group = new ButtonGroup();
        group.add(dayPassRadio);
        group.add(seasonPassRadio);
        JPanel radios = new JPanel(new GridBagLayout());
        radios.add(dayPassRadio, cell(0, 0));
        radios.add(seasonPassRadio, cell(1, 0));
        GridBagConstraints gbc = cell(0, 1);
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Pass"), gbc);
        gbc = cell(1, 1);
        gbc.weightx = 1.0;
        panel.add(radios, gbc);

        // Choosing Season disables the days field
        dayPassRadio.addActionListener(e -> daysSpinner.setEnabled(true));
        seasonPassRadio.addActionListener(e -> daysSpinner.setEnabled(false));

        addRow(panel, 2, "Days", daysSpinner, null);

        gbc = cell(1, 3);
        gbc.insets = new Insets(0, 6, 4, 6);
        JLabel hint = new JLabel("<html>$26/day, 10% off at 5+ day passes<br>Season pass $200 (30 days)</html>");
        hint.setForeground(Color.GRAY);
        panel.add(hint, gbc);

        gbc = cell(1, 4);
        gbc.anchor = GridBagConstraints.LINE_END;
        addLiftPass.addActionListener(this);
        styleAction(addLiftPass);
        panel.add(addLiftPass, gbc);
        return panel;
    }

    private JPanel initLessonsPanel() {
        JPanel panel = titledPanel("Lessons");

        addRow(panel, 0, "Person", lessonPerson, null);

        lessonLevelField.setEditable(false);
        addRow(panel, 1, "Ski level", lessonLevelField, null);

        addRow(panel, 2, "Lessons", lessonsSpinner, null);

        GridBagConstraints gbc = cell(1, 3);
        gbc.anchor = GridBagConstraints.LINE_END;
        addLessons.addActionListener(this);
        styleAction(addLessons);
        panel.add(addLessons, gbc);

        lessonPerson.addActionListener(e -> updateLessonLevel());
        return panel;
    }

    private JPanel initDetailsPanel() {
        JPanel panel = titledPanel("Bundle details");
        GridBagConstraints gbc = cell(0, 0);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(detailsText), gbc);
        return panel;
    }

    private void styleAction(JButton button) {
        button.setBackground(ResortGUI.getPrimaryColour());
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    // ------------------------------------------------------------------ public refresh

    /** Re-reads the bundles from the resort (called when this tab is opened), keeping the selection. */
    public void refreshBundles() {
        TravelBundle selected = bundleList.getSelectedValue();
        bundleModel.clear();
        for (TravelBundle bundle : resort.getBundles()) {
            bundleModel.addElement(bundle);
        }
        if (selected != null && bundleModel.contains(selected)) {
            bundleList.setSelectedValue(selected, true);
        } else {
            onBundleSelected();
        }
    }

    // ------------------------------------------------------------------ actions

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == listAllBundles) {
            onListAll();
        } else if (e.getSource() == addLiftPass) {
            onAddLiftPass();
        } else if (e.getSource() == addLessons) {
            onAddLessons();
        }
    }

    private void onBundleSelected() {
        TravelBundle bundle = bundleList.getSelectedValue();
        liftPerson.removeAllItems();
        lessonPerson.removeAllItems();
        if (bundle != null) {
            Customer customer = bundle.getCustomer();
            addPerson(new PersonOption(customer.getId(), true, customer.getName(), customer.getLevel()));
            for (FamilyMember member : bundle.getFamily()) {
                addPerson(new PersonOption(member.getId(), false, member.getName(), member.getLevel()));
            }
            detailsText.setText(bundle.costBreakdown());
        } else {
            detailsText.setText("");
        }
        detailsText.setCaretPosition(0);
        updateLessonLevel();
        updateEnabledState();
    }

    private void addPerson(PersonOption person) {
        liftPerson.addItem(person);
        lessonPerson.addItem(person);
    }

    private void onListAll() {
        detailsText.setText("");
        detailsText.append(gui.returnBarString());
        detailsText.append(gui.returnHeaderString("ALL BUNDLES"));
        detailsText.append(gui.returnBarString());
        detailsText.append("\n");
        if (resort.getBundles().isEmpty()) {
            detailsText.append("No bundles yet. Create one in the Create Bundle tab.\n");
        }
        for (TravelBundle bundle : resort.getBundles()) {
            detailsText.append(bundle.prettyPrint() + "\n\n");
        }
        detailsText.setCaretPosition(0);
        bundleList.clearSelection();
        gui.setStatusText(String.format("Listed %d bundles", resort.getBundles().size()));
    }

    private void onAddLiftPass() {
        TravelBundle bundle = requireBundle();
        PersonOption person = (PersonOption) liftPerson.getSelectedItem();
        if (bundle == null || person == null) {
            return;
        }
        try {
            LiftPassType type = seasonPassRadio.isSelected() ? LiftPassType.SEASON : LiftPassType.DAY;
            resort.addLiftPass(bundle, person.id(), person.customer(), person.name(), type,
                    (int) daysSpinner.getValue());
            showBundle(bundle);
            gui.setStatusText(type == LiftPassType.SEASON
                    ? "Season pass added for " + person.name()
                    : String.format("%d day pass(es) added for %s", (int) daysSpinner.getValue(), person.name()));
        } catch (InvalidBookingException ex) {
            showError("Could not add lift pass", ex.getMessage());
        }
    }

    private void onAddLessons() {
        TravelBundle bundle = requireBundle();
        PersonOption person = (PersonOption) lessonPerson.getSelectedItem();
        if (bundle == null || person == null) {
            return;
        }
        int count = (int) lessonsSpinner.getValue();
        if (count == 0) {
            gui.setStatusText("Choose at least 1 lesson to add");
            return;
        }
        try {
            resort.addLessons(bundle, person.id(), person.customer(), person.name(), person.level(), count);
            showBundle(bundle);
            gui.setStatusText(String.format("%d lesson(s) added for %s", count, person.name()));
        } catch (InvalidBookingException ex) {
            showError("Could not add lessons", ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ helpers

    private TravelBundle requireBundle() {
        TravelBundle bundle = bundleList.getSelectedValue();
        if (bundle == null) {
            showError("No bundle selected", "Select a bundle in the list first.");
        }
        return bundle;
    }

    private void showBundle(TravelBundle bundle) {
        detailsText.setText(bundle.costBreakdown());
        detailsText.setCaretPosition(0);
    }

    private void updateLessonLevel() {
        PersonOption person = (PersonOption) lessonPerson.getSelectedItem();
        if (person == null) {
            lessonLevelField.setText("");
            return;
        }
        lessonLevelField.setText(String.format("%s · $%.0f/lesson",
                person.level(), person.level().getLessonPrice()));
    }

    private void updateEnabledState() {
        boolean hasBundle = bundleList.getSelectedValue() != null;
        liftPerson.setEnabled(hasBundle);
        lessonPerson.setEnabled(hasBundle);
        addLiftPass.setEnabled(hasBundle);
        addLessons.setEnabled(hasBundle);
        daysSpinner.setEnabled(dayPassRadio.isSelected());
    }

    /** One person on the selected bundle: the customer or a family member. */
    private record PersonOption(int id, boolean customer, String name, SkiLevel level) {
        @Override
        public String toString() {
            return name + (customer ? " (customer)" : " (family)");
        }
    }
}
