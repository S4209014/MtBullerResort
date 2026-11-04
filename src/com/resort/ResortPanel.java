package com.resort;

import javax.swing.*;

public abstract class ResortPanel {
    private final JPanel panel = new JPanel();
    private String name;

    ResortPanel(String name) {
        JLabel label = new JLabel();
        label.setText(name);

        panel.add(label);
    }

    public JPanel getPanel() {
        return this.panel;
    }
}
