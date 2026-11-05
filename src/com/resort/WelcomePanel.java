package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class WelcomePanel extends ResortPanel {
    private final JButton enterButton = new JButton("Enter");
    private ActionListener enterButtonListener;
    private final ImageIcon logo = new ImageIcon(
            Objects.requireNonNull(WelcomePanel.class.getResource("/assets/MtBullerResort_Logo.png")));

    WelcomePanel() {
        super("Welcome");
        createComponents();
    }

    @Override
    protected void createComponents() {
        JLabel titleLabel = new JLabel("Welcome to Mt Buller Resort", logo, SwingConstants.CENTER);
        titleLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        JLabel descriptionLabel = new JLabel("Create custom travel bundles for customers and their families");

        // Add listener to button
        enterButton.addActionListener(e -> {
            if ((enterButtonListener != null)) {
                enterButtonListener.actionPerformed(e);
            }
        });

        // Positioning and constraints, adding to Panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        this.add(titleLabel, gbc);

        gbc.gridy = 1;
        this.add(descriptionLabel, gbc);

        gbc.gridy = 2;
        this.add(enterButton, gbc);
    }

    public ActionListener addEnterButtonListener(ActionListener listener) {
        this.enterButtonListener = listener;
        return listener;
    }
}
