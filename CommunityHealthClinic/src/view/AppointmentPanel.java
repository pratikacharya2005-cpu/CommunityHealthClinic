package view;

import controller.AppointmentController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Appointment;
import model.Doctor;
import model.Patient;
import util.ValidationException;

/** Appointment booking, status management, searching and sorting screen. */
public class AppointmentPanel extends JPanel implements Refreshable {

    private final AppointmentController controller;

    private final JComboBox<Patient> patientBox = new JComboBox<>();
    private final JComboBox<Doctor> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(12);
    private final JTextField timeField = new JTextField(8);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{
        Appointment.STATUS_SCHEDULED, Appointment.STATUS_COMPLETED,
        Appointment.STATUS_CANCELLED});
    private final JTextArea notesArea = new JTextArea(2, 25);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Appointment ID", "Patient ID", "Patient Name", "Doctor ID",
                "Doctor Name", "Date", "Time", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public AppointmentPanel(AppointmentController controller) {
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
        dateField.setToolTipText("Format: yyyy-MM-dd, e.g. 2026-09-27");
        timeField.setToolTipText("24-hour format, e.g. 09:30");
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        FormUtil.addRow(form, 0, "Patient *", patientBox);
        FormUtil.addRow(form, 1, "Doctor *", doctorBox);
        FormUtil.addRow(form, 2, "Date * (yyyy-MM-dd)", dateField);
        FormUtil.addRow(form, 3, "Time * (HH:mm)", timeField);
        FormUtil.addRow(form, 4, "Status", statusBox);
        FormUtil.addRow(form, 5, "Notes", new JScrollPane(notesArea));
        return form;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton book = new JButton("Book Appointment");
        JButton applyStatus = new JButton("Apply Status");
        JButton cancel = new JButton("Cancel Appointment");
        JButton delete = new JButton("Delete");
        JButton bubbleSort = new JButton("Sort by Date (Bubble)");
        JButton insertionSort = new JButton("Sort by Date (Insertion)");
        JButton clear = new JButton("Clear");

        book.addActionListener(e -> onBook());
        applyStatus.addActionListener(e -> onApplyStatus());
        cancel.addActionListener(e -> onCancel());
        delete.addActionListener(e -> onDelete());
        bubbleSort.addActionListener(e -> {
            fillTable(controller.getAppointmentsSortedByDateBubble());
            setStatus("Appointments sorted chronologically using bubble sort.");
        });
        insertionSort.addActionListener(e -> {
            fillTable(controller.getAppointmentsSortedByDateInsertion());
            setStatus("Appointments sorted chronologically using insertion sort.");
        });
        clear.addActionListener(e -> clearForm());

        panel.add(book);
        panel.add(applyStatus);
        panel.add(cancel);
        panel.add(delete);
        panel.add(bubbleSort);
        panel.add(insertionSort);
        panel.add(clear);
        return panel;
    }

    private void onBook() {
        Patient patient = (Patient) patientBox.getSelectedItem();
        Doctor doctor = (Doctor) doctorBox.getSelectedItem();
        if (patient == null || doctor == null) {
            showError("Please register at least one patient and one doctor first.");
            return;
        }
        try {
            Appointment created = controller.bookAppointment(
                    patient.getId(), doctor.getId(), dateField.getText(),
                    timeField.getText(), notesArea.getText());
            showInfo("Appointment " + created.getAppointmentId() + " booked for "
                    + patient.getName() + " on " + created.getDate() + " at "
                    + created.getTime() + ".");
            clearForm();
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onApplyStatus() {
        String id = getSelectedId();
        if (id == null) { showError("Select an appointment from the table first."); return; }
        try {
            controller.updateStatus(id, (String) statusBox.getSelectedItem());
            showInfo("Appointment " + id + " status set to " + statusBox.getSelectedItem() + ".");
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onCancel() {
        String id = getSelectedId();
        if (id == null) { showError("Select an appointment from the table first."); return; }
        try {
            controller.cancelAppointment(id);
            showInfo("Appointment " + id + " has been cancelled.");
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void onDelete() {
        String id = getSelectedId();
        if (id == null) { showError("Select an appointment from the table first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete appointment " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.deleteAppointment(id);
            showInfo("Appointment " + id + " deleted.");
            clearForm();
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private String getSelectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? null : String.valueOf(tableModel.getValueAt(row, 0));
    }

    private void populateForm(int row) {
        String appointmentId = String.valueOf(tableModel.getValueAt(row, 0));
        Appointment appointment = controller.findById(appointmentId);
        if (appointment == null) return;
        selectPatient(appointment.getPatientId());
        selectDoctor(appointment.getDoctorId());
        dateField.setText(appointment.getDate());
        timeField.setText(appointment.getTime());
        statusBox.setSelectedItem(appointment.getStatus());
        notesArea.setText(appointment.getNotes());
    }

    private void selectPatient(String patientId) {
        for (int i = 0; i < patientBox.getItemCount(); i++) {
            if (patientBox.getItemAt(i).getId().equalsIgnoreCase(patientId)) {
                patientBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectDoctor(String doctorId) {
        for (int i = 0; i < doctorBox.getItemCount(); i++) {
            if (doctorBox.getItemAt(i).getId().equalsIgnoreCase(doctorId)) {
                doctorBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void clearForm() {
        dateField.setText("");
        timeField.setText("");
        notesArea.setText("");
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void fillTable(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        for (Appointment a : appointments) {
            tableModel.addRow(new Object[]{
                a.getAppointmentId(),
                a.getPatientId(),
                controller.getPatientName(a.getPatientId()),
                a.getDoctorId(),
                controller.getDoctorName(a.getDoctorId()),
                a.getDate(),
                a.getTime(),
                a.getStatus()
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

    @Override
    public void refreshData() {
        Object selectedPatient = patientBox.getSelectedItem();
        Object selectedDoctor = doctorBox.getSelectedItem();

        patientBox.removeAllItems();
        doctorBox.removeAllItems();

        for (Patient p : controller.getAllPatients()) patientBox.addItem(p);
        for (Doctor d : controller.getAllDoctors()) doctorBox.addItem(d);

        if (selectedPatient instanceof Patient)
            selectPatient(((Patient) selectedPatient).getId());
        if (selectedDoctor instanceof Doctor)
            selectDoctor(((Doctor) selectedDoctor).getId());

        fillTable(controller.getAllAppointments());
    }
}