package com.resort;

import javax.swing.*;
import java.awt.*;

public abstract class ResortPanel extends JPanel {
    protected String name;
    protected MtBullerResort resort;

    ResortPanel(String name, MtBullerResort resort) {
        this.name = name;
        this.resort = resort;
        this.setLayout(new GridBagLayout());
    }

    protected abstract void createComponents();

    public String getName() {
        return this.name;
    }
}
