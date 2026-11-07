package com.resort;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.text.SimpleDateFormat;

public abstract class ResortPanel extends JPanel {
    protected String name;
    protected MtBullerResort resort;
    protected ResortGUI gui;

    ResortPanel(String name, MtBullerResort resort, ResortGUI gui) {
        this.name = name;
        this.resort = resort;
        this.gui = gui;
        this.setLayout(new GridBagLayout());
    }

    protected abstract void createComponents();

    public String getName() {
        return this.name;
    }

    /** A GridBagLayout panel with the same bold, coloured titled border used across the program. */
    protected JPanel titledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(title);
        titledBorder.setTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titledBorder.setTitleColor(ResortGUI.getPrimaryColour());
        panel.setBorder(titledBorder);
        return panel;
    }

    /** Builds a GridBagConstraints at the given cell with consistent spacing. */
    protected GridBagConstraints cell(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.LINE_START;
        return gbc;
    }

    /** Shared yyyy-MM-dd formatter for the date fields. */
    protected DateFormatter dateFormatter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        dateFormatter.setCommitsOnValidEdit(true);
        return dateFormatter;
    }

    /** Standard read-only monospaced output area. */
    protected JTextArea outputArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    /** Shows an error dialog with the program's standard parent and icon. */
    protected void showError(String title, String message) {
        JOptionPane.showMessageDialog(gui, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
