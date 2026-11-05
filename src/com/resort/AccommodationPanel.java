package com.resort;

import javax.swing.*;
import java.awt.*;

public class AccommodationPanel extends ResortPanel {
    AccommodationPanel() {
        super("Accommodation");
        createComponents();
    }

    @Override
    protected void createComponents() {
        JLabel label = new JLabel(this.name);
        this.add(label);
    }
}
