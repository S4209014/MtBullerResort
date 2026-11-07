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
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
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
        // A native button's bounds include a margin around the visible shape (more room underneath for the
        // shadow). Paint inside that margin so this button looks the same size as a plain button.
        g2.fillRoundRect(5, 4, getWidth() - 10, getHeight() - 9, 8, 8);
        g2.dispose();
        super.paintComponent(g); // draws the text on top
    }
}
