package com.resort;

import javax.swing.*;
import java.awt.*;

public abstract class ResortPanel extends JPanel {
    protected String name;

    ResortPanel(String name) {
        this.name = name;
        this.setLayout(new GridBagLayout());
    }

    protected abstract void createComponents();

    public String getName() {
        return this.name;
    }
}
