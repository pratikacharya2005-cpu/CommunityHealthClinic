package view;

import controller.PatientController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Patient;
import util.ValidationException;

/** Patient registration, update, delete and listing screen. */
public class RegisterPatientPanel extends JPanel implements Refreshable {

    private final PatientController controller;

    private final JTextField idField = new JTextField(12);
    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JComboBox<String> genderBox =
            new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JTextField dobField = new JTextField(12);
    private final JTextField addressField = new JTextField(25);
    private final JTextArea historyArea = new JTextArea(3, 25);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Phone", "Email", "Gender", "DOB", "Address", "History"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public RegisterPatientPanel(PatientController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                populateForm(table.getSelectedRow());
        });

        refreshData();
    }

    private JPanel buildForm() {
        JPanel form = FormUtil.newForm();
        dobField.setToolTipText("Format: yyyy-MM-dd, e.g. 1985-04-12");
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        FormUtil.addRow(form, 0, "Patient ID *", idField);
        FormUtil.addRow(form, 1, "Full Name *", nameField);
        FormUtil.addRow(form, 2, "Phone *", phoneField);
        FormUtil.addRow(form, 3, "Email *", emailField);
        FormUtil.addRow(form, 4, "Gender *", genderBox);
        FormUtil.addRow(form, 5, "Date of Birth * (yyyy-MM-dd)", dobField);
        FormUtil.addRow(form, 6, "Address", addressField);
        FormUtil.addRow(form, 7, "Medical History", new JScrollPane(historyArea));
        return form;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton register = new JButton("Register Patient");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear Form");
        JButton sort = new JButton("Sort by Name");

        register.addActionListener(e -> onRegister());
        update.addActionListener(e -> onUpdate());
        delete.addActionListener(e -> onDelete());
        clear.addActionListener(e -> clearForm());
        sort.addActionListener(e -> {
            fillTable(controller.getPatientsSortedByName());
            setStatus("Patients sorted by name using insertion sort.");
        });

        panel.add(register);
        panel.add(update);
        panel.add(delete);
        panel.add(clear);
        panel.add(sort);
        return panel;
    }

    private void onRegister() {
        try {
            Patient created = controller.registerPatient(
                    idField.getText(), nameField.getText(), phoneField.getText(),
                    emailField.getText(), (String) genderBox.getSelectedItem(),
                    dobField.getText(), addressField.getText(), historyArea.getText());
            showInfo("Patient '" + created.getName() + "' registered with ID "
                    + created.getId() + ".");
            clearForm();
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onUpdate() {
        try {
            Patient updated = controller.updatePatient(
                    idField.getText(), nameField.getText(), phoneField.getText(),
                    emailField.getText(), (String) genderBox.getSelectedItem(),
                    dobField.getText(), addressField.getText(), historyArea.getText());
            showInfo("Patient '" + updated.getId() + "' updated successfully.");
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onDelete() {
        String id = idField.getText().trim().isEmpty() ? getSelectedId() : idField.getText();
        if (id == null || id.trim().isEmpty()) {
            showError("Select a patient from the table or enter a Patient ID.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete patient " + id + " and all associated appointments?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.deletePatient(id);
            showInfo("Patient " + id + " was deleted.");
            clearForm();
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private String getSelectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? null : String.valueOf(tableModel.getValueAt(row, 0));
    }

    private void populateForm(int row) {
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        emailField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        genderBox.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 4)));
        dobField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        addressField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        historyArea.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        genderBox.setSelectedIndex(0);
        dobField.setText("");
        addressField.setText("");
        historyArea.setText("");
        table.clearSelection();
        idField.requestFocusInWindow();
    }

    private void fillTable(java.util.List<Patient> patients) {
        tableModel.setRowCount(0);
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getPhone(), p.getEmail(), p.getGender(),
                p.getDateOfBirth(), p.getAddress(), p.getMedicalHistory()
            });
        }
    }

    private void setStatus(String message) {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainMenuFrame) ((MainMenuFrame) w).setStatus(message);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override public void refreshData() { fillTable(controller.getAllPatients()); }
}