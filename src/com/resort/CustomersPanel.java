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
    private final JTextField nameField = new JTextField(14);
    private final JTextField emailField = new JTextField(14);
    private final JTextField phoneField = new JTextField(14);
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
        // Left: add-customer form. Right: customer list. Side by side, the list takes the spare width.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // form keeps its natural width
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(8, 4, 4, 4);
        this.add(initFormPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // list takes all the spare width
        this.add(initListPanel(), gbc);

        resetForm();
        showList();
    }

    private JPanel initListPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = cell(0, 0);
        gbc.weightx = 1.0;
        JLabel title = new JLabel("All customers");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        title.setForeground(ResortGUI.getPrimaryColour());
        panel.add(title, gbc);

        gbc = cell(1, 0);
        gbc.anchor = GridBagConstraints.LINE_END;
        listCustomers.addActionListener(this);
        panel.add(listCustomers, gbc);

        gbc = cell(0, 1);
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(resultsPanel, gbc);
        return panel;
    }

    private JPanel initFormPanel() {
        JPanel form = titledPanel("Add customer");

        // Hints sit under each field (not beside it) so the form stays narrow
        addRow(form, 0, "Customer ID", idField, null);
        idField.setEditable(false);
        addRow(form, 1, "Full name", nameField, null);
        addHint(form, 2, "Letters and spaces only");
        addRow(form, 3, "Email", emailField, null);
        addHint(form, 4, "e.g. name@mail.com");
        addRow(form, 5, "Phone", phoneField, null);
        addHint(form, 6, "10 digits, no spaces");
        addRow(form, 7, "Ski level", levelDropdown, null);

        GridBagConstraints gbc = cell(0, 8);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        gbc.weighty = 1.0;
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

    private void addHint(JPanel panel, int row, String hint) {
        GridBagConstraints gbc = cell(1, row);
        gbc.insets = new Insets(0, 6, 4, 6);
        JLabel label = new JLabel(hint);
        label.setForeground(Color.GRAY);
        panel.add(label, gbc);
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

    private static String capitalise(SkiLevel level) {
        String text = level.name();
        return text.charAt(0) + text.substring(1).toLowerCase();
    }

    /**
     * Refreshes anything that depends on resort data (called when this tab is opened). The ID box is only
     * updated if nothing is being typed, so a half-filled form is never wiped.
     */
    public void refresh() {
        idField.setText(String.format("C%03d", resort.peekNextCustomerId()));
        showList();
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
                resultsText.append(String.format("          \u2514 family: %s (%s)%n",
                        member.getName(), capitalise(member.getLevel())));
            }
        }
        resultsText.setCaretPosition(0);
    }
}
