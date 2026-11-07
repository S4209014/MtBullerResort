package com.resort;

import javax.swing.*;
import java.awt.*;

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
}
