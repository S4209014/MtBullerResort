package com.resort;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;

public class AccommodationPanel extends ResortPanel implements ActionListener {
    private final JTextArea resultsText = new JTextArea();
    private final JScrollPane resultsPanel = new JScrollPane(resultsText);

    private final JPanel filterPanel = new JPanel(new GridBagLayout());

    private final String[] accommodationTypes = {"All Types", "Hotel Room", "Apartment", "Lodge Room"};
    private final JComboBox<String> typeDropdown = new JComboBox<>(accommodationTypes);
    private final JFormattedTextField checkInDate = new JFormattedTextField(dateFormatter());
    private final JSpinner numNightsSpinner = new JSpinner();
    private final JSpinner numGuestsSpinner = new JSpinner();

    private final JButton showAll = new JButton("Show All");
    private final JButton showAvailable = new PrimaryButton("Show Available");

    AccommodationPanel(MtBullerResort resort, ResortGUI gui) {
        super("Accommodation", resort, gui);
        createComponents();
    }

    @Override
    protected void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 4, 2, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 20;
        gbc.ipady = 10;

        initFilterPanel();
        this.add(filterPanel, gbc);

        gbc.insets = new Insets(2, 4, 4, 4);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        this.add(resultsPanel, gbc);
    }

    private void initFilterPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 4, 4, 4);

        TitledBorder titledBorder = new TitledBorder("Filter");
        Font titleBorderFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        titledBorder.setTitleFont(titleBorderFont);
        titledBorder.setTitleColor(ResortGUI.getPrimaryColour());
        filterPanel.setBorder(titledBorder);

        JLabel typeLabel = new JLabel("Type");
        filterPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        filterPanel.add(typeDropdown, gbc);

        gbc.gridx = 2;
        JLabel checkInDateLabel = new JLabel("<html>Check in date<br>(yyyy-mm-dd)</html>");
        filterPanel.add(checkInDateLabel, gbc);

        gbc.gridx = 3;
        checkInDate.setValue(new Date());
        checkInDate.setMinimumSize(new Dimension(110, 20));
        checkInDate.setPreferredSize(new Dimension(120, 25));
        checkInDate.setHorizontalAlignment(SwingConstants.CENTER);
        filterPanel.add(checkInDate, gbc);

        gbc.gridx = 4;
        JLabel nightsLabel = new JLabel("Nights");
        filterPanel.add(nightsLabel, gbc);

        gbc.gridx = 5;
        numNightsSpinner.setValue(1);
        numNightsSpinner.setPreferredSize(new Dimension(50, 25));
        filterPanel.add(numNightsSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 4, 4, 4);
        JLabel guestsLabel = new JLabel("Guests (1-6)");
        filterPanel.add(guestsLabel, gbc);

        gbc.gridx = 1;
        numGuestsSpinner.setValue(1);
        numGuestsSpinner.setPreferredSize(new Dimension(50, 25));
        filterPanel.add(numGuestsSpinner, gbc);

        gbc.gridx = 6;
        showAll.addActionListener(this);
        filterPanel.add(showAll, gbc);

        gbc.gridx = 7;
        showAvailable.addActionListener(this);
        filterPanel.add(showAvailable, gbc);

        resultsText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultsText.setLineWrap(true);
        resultsText.setWrapStyleWord(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.showAvailable) {
            onAvailableButton();
        } else if (e.getSource() == this.showAll) {
            onShowAllButton();
        }
    }

    private void onShowAllButton() {
        this.resultsText.setText("");
        this.resultsText.append(gui.returnBarString());
        this.resultsText.append(gui.returnHeaderString("ALL ACCOMMODATIONS"));
        this.resultsText.append(gui.returnBarString());
        this.resultsText.append("\n");
        for (Accommodation room : resort.getAccommodations()) {
            this.resultsText.append(room + "\n");
        }
        this.gui.setStatusText(String.format("Displayed all %d accommodations", resort.getAccommodations().size()));
    }

    private void onAvailableButton() {
        this.resultsText.setText("");
        LocalDate date;
        try {
            date = LocalDate.parse(this.checkInDate.getText());
        } catch (DateTimeParseException ex) {
            showError("Invalid date", "Use yyyy-MM-dd (e.g. 2026-09-01).");
            return;
        }

        // Check if the date is in the past
        String inPastResult = ResortGUI.dateInPastCheck(date, true);
        if (!inPastResult.isEmpty())  {
            this.gui.setStatusText(inPastResult);
            return;
        }

        int nights = (int) this.numNightsSpinner.getValue();
        int people = (int) this.numGuestsSpinner.getValue();

        ArrayList<Accommodation> rooms = resort.getAvailableRooms(date, nights, people);
        // Count the amount of accommodations we find

        this.resultsText.append(
                String.format("Looking for accommodation for %d people from %s for %d night(s).\n", people, date, nights)
        );
        this.resultsText.append("\n");
        this.resultsText.append(gui.returnBarString());
        this.resultsText.append(gui.returnHeaderString("ACCOMMODATION OPTIONS"));
        this.resultsText.append(gui.returnBarString());
        this.resultsText.append("\n");
        int i = 0;

        for (Accommodation room : rooms) {
            switch (this.typeDropdown.getSelectedItem().toString()) {
                case "All Types" -> {
                    i++;
                    this.resultsText.append(room + "\n");
                }
                case "Hotel Room" -> {
                    if (room instanceof HotelRoom) {
                        i++;
                        this.resultsText.append(room + "\n");
                    }
                }
                case "Apartment" -> {
                    if (room instanceof Apartment) {
                        i++;
                        this.resultsText.append(room + "\n");
                    }
                }
                case "Lodge Room" -> {
                    if (room instanceof LodgeRoom) {
                        i++;
                        this.resultsText.append(room + "\n");
                    }
                }
                default -> {
                    this.gui.setStatusText("Error displaying all options - please try again");
                    return;
                }
            }
        }
        this.gui.setStatusText(
                String.format("Displayed %d accommodation options for %s", i, typeDropdown.getSelectedItem())
        );
    }
}
