package com.resort;

import javax.sound.midi.SoundbankResource;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    private final JButton showAvailable = new JButton("Show Available");

    AccommodationPanel(MtBullerResort resort) {
        super("Accommodation", resort);
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
        titledBorder.setTitleFont(Font.getFont(Font.SANS_SERIF));
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
        filterPanel.add(showAll, gbc);

        gbc.gridx = 7;
        showAvailable.addActionListener(this);
        filterPanel.add(showAvailable, gbc);

        resultsText.setLineWrap(true);
        resultsText.setWrapStyleWord(true);
    }

    private DateFormatter dateFormatter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        // create the formatter
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        dateFormatter.setCommitsOnValidEdit(true);
        return dateFormatter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.showAvailable) {
            onAvailableButton();
        }
    }

    private void onAvailableButton() {
        this.resultsText.setText("");
        LocalDate date = LocalDate.parse(this.checkInDate.getText());
        int nights = (int) this.numNightsSpinner.getValue();
        int people = (int) this.numGuestsSpinner.getValue();

        ArrayList<Accommodation> rooms = resort.getAvailableRooms(date, nights, people);

        for (Accommodation room : rooms) {
            switch (this.typeDropdown.getSelectedItem().toString()) {
                case "All Types" -> this.resultsText.append(room + "\n");
                case "Hotel Room" -> {
                    if (room instanceof HotelRoom) {
                        this.resultsText.append(room + "\n");
                    }
                }
                case "Apartment" -> {
                    if (room instanceof Apartment) {
                        this.resultsText.append(room + "\n");
                    }
                }
                case "Lodge Room" -> {
                    if (room instanceof LodgeRoom) {
                        this.resultsText.append(room + "\n");
                    }
                }
            }
        }
    }
}
