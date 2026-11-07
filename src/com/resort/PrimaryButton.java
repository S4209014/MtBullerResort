package com.resort;

import javax.swing.*;
import java.awt.*;

/**
 * A button that keeps the standard button size and rounded shape but is filled with the program's
 * primary (blue) colour. Used for the main action in each panel.
 */
public class PrimaryButton extends JButton {
    public PrimaryButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
    }

    /** Same size as a plain button with this text, so primary and normal buttons line up. */
    @Override
    public Dimension getPreferredSize() {
        return new JButton(getText()).getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = ResortGUI.getPrimaryColour();
        if (!isEnabled()) {
            fill = Color.GRAY;
        } else if (getModel().isPressed()) {
            fill = fill.darker();
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g); // draws the text on top
    }
}
