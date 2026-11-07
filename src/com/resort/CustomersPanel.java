package com.resort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Customers tab: add a customer (function 3) and list customers with their family members (function 4).
 */
public class CustomersPanel extends ResortPanel implements ActionListener {
    private final JTextField idField = new JTextField(6);
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JComboBox<SkiLevel> levelDropdown = new JComboBox<>(SkiLevel.values());

    private final JButton addCustomer = new JButton("Add Customer");
    private final JButton clearForm = new JButton("Clear");
    private final JButton listCustomers = new JButton("List Customers");

    private final JTextArea resultsText = outputArea();
    private final JScrollPane resultsPanel = new JScrollPane(resultsText);

    CustomersPanel(MtBullerResort resort, ResortGUI gui) {
        super("Customers", resort, gui);
        createComponents();
    }

    @Override
    protected void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(8, 4, 2, 4);
        this.add(initFormPanel(), gbc);

        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(2, 4, 2, 4);
        listCustomers.addActionListener(this);
        this.add(listCustomers, gbc);

        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 4, 4, 4);
        this.add(resultsPanel, gbc);

        resetForm();
        showList();
    }

    private JPanel initFormPanel() {
        JPanel form = titledPanel("Add customer");

        addRow(form, 0, "Customer ID", idField, "Generated automatically");
        idField.setEditable(false);
        addRow(form, 1, "Full name", nameField, "Letters and spaces only, e.g. Priya Nair");
        addRow(form, 2, "Email", emailField, "e.g. priya.nair@email.com");
        addRow(form, 3, "Phone", phoneField, "10 digits, no spaces, e.g. 0412345678");
        addRow(form, 4, "Ski level", levelDropdown, "Choose one");

        GridBagConstraints gbc = cell(1, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridwidth = 2;
        JPanel buttons = new JPanel(new GridBagLayout());
        GridBagConstraints b = cell(0, 0);
        clearForm.addActionListener(this);
        buttons.add(clearForm, b);
        b = cell(1, 0);
        addCustomer.addActionListener(this);
        addCustomer.setBackground(ResortGUI.getPrimaryColour());
        addCustomer.setForeground(Color.WHITE);
        addCustomer.setOpaque(true);
        addCustomer.setBorderPainted(false);
        buttons.add(addCustomer, b);
        form.add(buttons, gbc);
        return form;
    }

    private void resetForm() {
        idField.setText(String.format("C%03d", resort.peekNextCustomerId()));
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        levelDropdown.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addCustomer) {
            onAddCustomer();
        } else if (e.getSource() == clearForm) {
            resetForm();
            gui.setStatusText("Customer form cleared");
        } else if (e.getSource() == listCustomers) {
            showList();
            gui.setStatusText(String.format("Listed %d customers", resort.getCustomers().size()));
        }
    }

    private void onAddCustomer() {
        try {
            Customer customer = resort.addCustomer(
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    (SkiLevel) levelDropdown.getSelectedItem());
            resetForm();
            showList();
            gui.setStatusText(String.format("Customer C%03d %s added", customer.getId(), customer.getName()));
        } catch (InvalidBookingException ex) {
            showError("Invalid customer details", ex.getMessage());
        }
    }

    /** Prints every customer, with their family members indented underneath. */
    private void showList() {
        resultsText.setText("");
        resultsText.append(gui.returnBarString());
        resultsText.append(gui.returnHeaderString("ALL CUSTOMERS"));
        resultsText.append(gui.returnBarString());
        resultsText.append("\n");
        resultsText.append(Customer.tableHeader() + "\n");
        for (Customer customer : resort.getCustomers()) {
            resultsText.append(customer.tableRow() + "\n");
            for (FamilyMember member : customer.getFamily()) {
                resultsText.append(String.format("        family: %s%n", member));
            }
        }
        resultsText.setCaretPosition(0);
    }
}
