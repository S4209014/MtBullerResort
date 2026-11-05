package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePanel extends ResortPanel {
    private final JButton enterButton = new JButton("Enter");
    private ActionListener enterButtonListener;

    WelcomePanel() {
        super("Welcome");
        createComponents();
    }

    @Override
    protected void createComponents() {
        JLabel titleLabel = new JLabel("Welcome to Mt Buller Resort");
        JLabel descriptionLabel = new JLabel("Create custom travel bundles for customers and their families");

        // Add listener to button
        enterButton.addActionListener(e -> {
            if ((enterButtonListener != null)) {
                enterButtonListener.actionPerformed(e);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.CENTER;
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
