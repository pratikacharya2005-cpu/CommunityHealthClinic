package view;

import controller.DoctorController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Doctor;
import util.ValidationException;

/** Doctor registration, update, delete and listing screen. */
public class RegisterDoctorPanel extends JPanel implements Refreshable {

    private final DoctorController controller;

    private final JTextField idField = new JTextField(12);
    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JComboBox<String> genderBox =
            new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JTextField specialisationField = new JTextField(20);
    private final JTextField feeField = new JTextField(10);
    private final JTextField daysField = new JTextField(20);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Phone", "Email", "Gender",
                "Specialisation", "Fee", "Available Days"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public RegisterDoctorPanel(DoctorController controller) {
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
        feeField.setToolTipText("Numeric value, e.g. 75.00");
        daysField.setToolTipText("Comma separated, e.g. Mon,Wed,Fri");

        FormUtil.addRow(form, 0, "Doctor ID *", idField);
        FormUtil.addRow(form, 1, "Full Name *", nameField);
        FormUtil.addRow(form, 2, "Phone *", phoneField);
        FormUtil.addRow(form, 3, "Email *", emailField);
        FormUtil.addRow(form, 4, "Gender *", genderBox);
        FormUtil.addRow(form, 5, "Specialisation *", specialisationField);
        FormUtil.addRow(form, 6, "Consultation Fee *", feeField);
        FormUtil.addRow(form, 7, "Available Days *", daysField);
        return form;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton register = new JButton("Register Doctor");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear Form");
        JButton sort = new JButton("Sort by Name");

        register.addActionListener(e -> onRegister());
        update.addActionListener(e -> onUpdate());
        delete.addActionListener(e -> onDelete());
        clear.addActionListener(e -> clearForm());
        sort.addActionListener(e -> fillTable(controller.getDoctorsSortedByName()));

        panel.add(register);
        panel.add(update);
        panel.add(delete);
        panel.add(clear);
        panel.add(sort);
        return panel;
    }

    private void onRegister() {
        try {
            Doctor created = controller.registerDoctor(
                    idField.getText(), nameField.getText(), phoneField.getText(),
                    emailField.getText(), (String) genderBox.getSelectedItem(),
                    specialisationField.getText(), feeField.getText(), daysField.getText());
            showInfo("Dr. " + created.getName() + " registered with ID " + created.getId() + ".");
            clearForm();
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onUpdate() {
        try {
            controller.updateDoctor(
                    idField.getText(), nameField.getText(), phoneField.getText(),
                    emailField.getText(), (String) genderBox.getSelectedItem(),
                    specialisationField.getText(), feeField.getText(), daysField.getText());
            showInfo("Doctor " + idField.getText() + " updated successfully.");
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onDelete() {
        String id = idField.getText().trim().isEmpty() ? getSelectedId() : idField.getText();
        if (id == null || id.trim().isEmpty()) {
            showError("Select a doctor from the table or enter a Doctor ID.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete doctor " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.deleteDoctor(id);
            showInfo("Doctor " + id + " was deleted.");
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
        specialisationField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        feeField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        daysField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        genderBox.setSelectedIndex(0);
        specialisationField.setText("");
        feeField.setText("");
        daysField.setText("");
        table.clearSelection();
        idField.requestFocusInWindow();
    }

    private void fillTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getPhone(), d.getEmail(), d.getGender(),
                d.getSpecialisation(), String.format("%.2f", d.getConsultationFee()),
                d.getAvailableDays()
            });
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override public void refreshData() { fillTable(controller.getAllDoctors()); }
}