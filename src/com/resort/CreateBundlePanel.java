package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Create Bundle tab (function 5): booking details, family members and choosing a suitable accommodation.
 */
public class CreateBundlePanel extends ResortPanel implements ActionListener {
    private static final int MAX_FAMILY = 5;

    // ---- 1. Booking details ----
    private final JComboBox<Customer> customerDropdown = new JComboBox<>();
    private final JFormattedTextField startDate = new JFormattedTextField(dateFormatter());
    private final JSpinner nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));

    // ---- 2. Family members ----
    private final JTextField memberName = new JTextField(14);
    private final JComboBox<SkiLevel> memberLevel = new JComboBox<>(SkiLevel.values());
    private final JButton addMember = new JButton("Add Member");
    private final JButton removeMember = new JButton("Remove");
    private final DefaultListModel<FamilyMember> familyModel = new DefaultListModel<>();
    private final JList<FamilyMember> familyList = new JList<>(familyModel);
    private final JLabel partyLabel = new JLabel();

    // ---- 3. Choose accommodation ----
    private final String[] accommodationTypes = {"All Types", "Hotel Room", "Apartment", "Lodge Room"};
    private final JComboBox<String> typeDropdown = new JComboBox<>(accommodationTypes);
    private final JTextField maxPriceField = new JTextField(8);
    private final JButton findAvailable = new JButton("Find Available");
    private final JLabel roomsInfoLabel = new JLabel(" ");
    private final DefaultListModel<Accommodation> roomModel = new DefaultListModel<>();
    private final JList<Accommodation> roomList = new JList<>(roomModel);
    private final JLabel costLabel = new JLabel("Accommodation cost: -");
    private final JButton createBundle = new JButton("Create Bundle");

    // the nights used for the last "Find available", so the cost label matches the list shown
    private int searchedNights = 1;

    CreateBundlePanel(MtBullerResort resort, ResortGUI gui) {
        super("Create Bundle", resort, gui);
        createComponents();
    }

    @Override
    protected void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(8, 4, 4, 4);

        // Left column: booking details above family members
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.35;
        this.add(initBookingPanel(), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);
        this.add(initFamilyPanel(), gbc);

        // Right column: accommodation spans both rows
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 4, 4, 4);
        this.add(initAccommodationPanel(), gbc);

        refreshCustomers();
        updatePartyLabel();
    }

    // ------------------------------------------------------------------ panels

    private JPanel initBookingPanel() {
        JPanel panel = titledPanel("1. Booking details");

        addRow(panel, 0, "Customer", customerDropdown, null);

        startDate.setValue(new java.util.Date());
        addRow(panel, 1, "Start date", startDate, null);
        addHint(panel, 2, "yyyy-mm-dd, today or later");

        addRow(panel, 3, "Duration", nightsSpinner, null);
        addHint(panel, 4, "Nights, 1 or more");
        return panel;
    }

    private JPanel initFamilyPanel() {
        JPanel panel = titledPanel("2. Family members (0-5)");

        addRow(panel, 0, "Name", memberName, null);
        addRow(panel, 1, "Ski level", memberLevel, null);

        JPanel buttons = new JPanel(new GridBagLayout());
        removeMember.addActionListener(this);
        buttons.add(removeMember, cell(0, 0));
        addMember.addActionListener(this);
        addMember.setBackground(ResortGUI.getPrimaryColour());
        addMember.setForeground(Color.WHITE);
        addMember.setOpaque(true);
        addMember.setBorderPainted(false);
        buttons.add(addMember, cell(1, 0));

        GridBagConstraints gbc = cell(0, 2);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(buttons, gbc);

        familyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gbc = cell(0, 3);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(familyList), gbc);

        gbc = cell(0, 4);
        gbc.gridwidth = 2;
        partyLabel.setForeground(Color.GRAY);
        panel.add(partyLabel, gbc);
        return panel;
    }

    private JPanel initAccommodationPanel() {
        JPanel panel = titledPanel("3. Choose accommodation");

        // filter row
        JPanel filters = new JPanel(new GridBagLayout());
        filters.add(new JLabel("Type"), cell(0, 0));
        filters.add(typeDropdown, cell(1, 0));
        filters.add(new JLabel("Max price/night"), cell(2, 0));
        filters.add(maxPriceField, cell(3, 0));
        maxPriceField.setToolTipText("Leave blank for any price, e.g. 250");

        GridBagConstraints gbc = cell(4, 0);
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        findAvailable.addActionListener(this);
        findAvailable.setBackground(ResortGUI.getPrimaryColour());
        findAvailable.setForeground(Color.WHITE);
        findAvailable.setOpaque(true);
        findAvailable.setBorderPainted(false);
        filters.add(findAvailable, gbc);

        gbc = cell(0, 0);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filters, gbc);

        gbc = cell(0, 1);
        gbc.gridwidth = 2;
        roomsInfoLabel.setForeground(Color.GRAY);
        panel.add(roomsInfoLabel, gbc);

        // room list
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Accommodation room) {
                    setText(String.format("%s · %s · sleeps %d · $%.2f/night",
                            room.getId(), room.getLabel(), room.getCapacity(), room.getPricePerNight()));
                }
                return this;
            }
        });
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCostLabel();
            }
        });
        gbc = cell(0, 2);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(roomList), gbc);

        // cost + create
        gbc = cell(0, 3);
        gbc.weightx = 1.0;
        panel.add(costLabel, gbc);

        gbc = cell(1, 3);
        gbc.anchor = GridBagConstraints.LINE_END;
        createBundle.addActionListener(this);
        createBundle.setBackground(ResortGUI.getPrimaryColour());
        createBundle.setForeground(Color.WHITE);
        createBundle.setOpaque(true);
        createBundle.setBorderPainted(false);
        panel.add(createBundle, gbc);
        return panel;
    }

    private void addHint(JPanel panel, int row, String hint) {
        GridBagConstraints gbc = cell(1, row);
        gbc.insets = new Insets(0, 6, 4, 6);
        JLabel label = new JLabel(hint);
        label.setForeground(Color.GRAY);
        panel.add(label, gbc);
    }

    // ------------------------------------------------------------------ public refresh

    /** Repopulates the customer drop down (called when this tab is opened), keeping the selection. */
    public void refreshCustomers() {
        Customer selected = (Customer) customerDropdown.getSelectedItem();
        customerDropdown.removeAllItems();
        for (Customer customer : resort.getCustomers()) {
            customerDropdown.addItem(customer);
            if (selected != null && customer.getId() == selected.getId()) {
                customerDropdown.setSelectedItem(customer);
            }
        }
    }

    // ------------------------------------------------------------------ actions

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addMember) {
            onAddMember();
        } else if (e.getSource() == removeMember) {
            onRemoveMember();
        } else if (e.getSource() == findAvailable) {
            onFindAvailable();
        } else if (e.getSource() == createBundle) {
            onCreateBundle();
        }
    }

    private void onAddMember() {
        if (familyModel.size() >= MAX_FAMILY) {
            showError("Too many family members", "A bundle can include at most 5 family members.");
            return;
        }
        try {
            String name = MtBullerResort.validateName(memberName.getText());
            // id 0 is a placeholder; MtBullerResort.addBundle hands out the real ids
            FamilyMember member = new FamilyMember(0, name, (SkiLevel) memberLevel.getSelectedItem());
            familyModel.addElement(member);
            memberName.setText("");
            clearRooms();
            updatePartyLabel();
            gui.setStatusText("Added family member " + member);
        } catch (InvalidBookingException ex) {
            showError("Invalid family member", ex.getMessage());
        }
    }

    private void onRemoveMember() {
        FamilyMember selected = familyList.getSelectedValue();
        if (selected == null) {
            showError("Nothing selected", "Select a family member in the list first.");
            return;
        }
        familyModel.removeElement(selected);
        clearRooms();
        updatePartyLabel();
        gui.setStatusText("Removed family member " + selected.getName());
    }

    private void onFindAvailable() {
        LocalDate date = readStartDate();
        if (date == null) {
            return;
        }
        Double maxPrice = readMaxPrice();
        if (maxPrice != null && maxPrice < 0) { // readMaxPrice signals a bad entry with a negative number
            return;
        }

        int nights = (int) nightsSpinner.getValue();
        int party = 1 + familyModel.size();
        ArrayList<Accommodation> rooms = resort.getAvailableRooms(
                date, nights, party, selectedType(), maxPrice);

        roomModel.clear();
        for (Accommodation room : rooms) {
            roomModel.addElement(room);
        }
        searchedNights = nights;
        roomsInfoLabel.setText(String.format("Rooms that sleep %d and are free %s -> %s:",
                party, date, date.plusDays(nights)));
        updateCostLabel();
        gui.setStatusText(String.format("Found %d accommodation options for a party of %d", rooms.size(), party));
    }

    private void onCreateBundle() {
        Customer customer = (Customer) customerDropdown.getSelectedItem();
        if (customer == null) {
            showError("No customer", "Please select a customer.");
            return;
        }
        LocalDate date = readStartDate();
        if (date == null) {
            return;
        }
        Accommodation room = roomList.getSelectedValue();
        if (room == null) {
            showError("No accommodation", "Please select an accommodation from the list.");
            return;
        }

        ArrayList<FamilyMember> family = new ArrayList<>();
        for (int i = 0; i < familyModel.size(); i++) {
            family.add(familyModel.get(i));
        }
        try {
            TravelBundle bundle = resort.addBundle(customer.getId(), date, (int) nightsSpinner.getValue(),
                    family, room.getId());
            familyModel.clear();
            clearRooms();
            updatePartyLabel();
            gui.setStatusText(String.format("Bundle B%03d created for %s in %s",
                    bundle.getId(), customer.getName(), room.getId()));
            JOptionPane.showMessageDialog(gui, bundle.prettyPrint(), "Bundle created",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidBookingException | DateInPastException ex) {
            showError("Could not create bundle", ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ helpers

    /** Reads and checks the start date, showing a dialog and returning null if it is not usable. */
    private LocalDate readStartDate() {
        try {
            LocalDate date = LocalDate.parse(startDate.getText());
            String problem = ResortGUI.dateInPastCheck(date, true);
            if (!problem.isEmpty()) {
                showError("Invalid start date", problem);
                return null;
            }
            return date;
        } catch (DateTimeParseException ex) {
            showError("Invalid start date", "Use yyyy-MM-dd (e.g. 2026-09-01).");
            return null;
        }
    }

    /**
     * Blank = no limit (null). A bad entry shows a dialog and returns a negative number so the caller can stop.
     */
    private Double readMaxPrice() {
        String text = maxPriceField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            double price = Double.parseDouble(text);
            if (price <= 0) {
                throw new NumberFormatException();
            }
            return price;
        } catch (NumberFormatException ex) {
            showError("Invalid maximum price", "Maximum price must be a positive number, e.g. 250.");
            return -1.0;
        }
    }

    private Class<? extends Accommodation> selectedType() {
        return switch (String.valueOf(typeDropdown.getSelectedItem())) {
            case "Hotel Room" -> HotelRoom.class;
            case "Apartment" -> Apartment.class;
            case "Lodge Room" -> LodgeRoom.class;
            default -> null;
        };
    }

    /** Family changed, so any rooms found earlier may no longer fit. */
    private void clearRooms() {
        roomModel.clear();
        roomsInfoLabel.setText(" ");
        updateCostLabel();
    }

    private void updatePartyLabel() {
        int party = 1 + familyModel.size();
        int left = MAX_FAMILY - familyModel.size();
        partyLabel.setText(String.format("Party size: %d (customer + %d) · %d places left",
                party, familyModel.size(), left));
        addMember.setEnabled(left > 0);
    }

    private void updateCostLabel() {
        Accommodation room = roomList.getSelectedValue();
        if (room == null) {
            costLabel.setText("Accommodation cost: -");
            return;
        }
        costLabel.setText(String.format("Accommodation cost: %d x $%.2f = $%,.2f",
                searchedNights, room.getPricePerNight(), searchedNights * room.getPricePerNight()));
    }
}
