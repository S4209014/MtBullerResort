package com.resort;

import javax.swing.*;
import java.awt.event.*;

public class ResortGUI extends JFrame implements ActionListener {
    private JButton button;

    public ResortGUI() {
        button = new JButton("Click Me Now!");
        button.addActionListener(this);

        this.add(button);
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JLabel label = new JLabel("Hello World!");

        this.add(label);
        if (e.getSource() == button) {
            System.out.println("Button was clicked");
        }
    }

    public static void main(String[] args) {
        new ResortGUI();
    }
}
