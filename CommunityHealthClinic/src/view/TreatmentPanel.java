package view;

import controller.TreatmentController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Appointment;
import model.Treatment;
import util.ValidationException;

/** Treatment entry - records a diagnosis against an appointment. */
public class TreatmentPanel extends JPanel implements Refreshable {

    private final TreatmentController controller;

    private final JComboBox<Appointment> appointmentBox = new JComboBox<>();
    private final JTextArea diagnosisArea = new JTextArea(2, 28);
    private final JTextArea prescriptionArea = new JTextArea(2, 28);
    private final JTextArea notesArea = new JTextArea(2, 28);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Treatment ID", "Appointment", "Patient", "Doctor",
                "Date", "Diagnosis", "Prescription"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public TreatmentPanel(TreatmentController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshData();
    }

    private JPanel buildForm() {
        JPanel form = FormUtil.newForm();
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        prescriptionArea.setLineWrap(true);
        prescriptionArea.setWrapStyleWord(true);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        FormUtil.addRow(form, 0, "Appointment *", appointmentBox);
        FormUtil.addRow(form, 1, "Diagnosis *", new JScrollPane(diagnosisArea));
        FormUtil.addRow(form, 2, "Prescription *", new JScrollPane(prescriptionArea));
        FormUtil.addRow(form, 3, "Additional Notes", new JScrollPane(notesArea));
        return form;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton save = new JButton("Save Treatment");
        JButton clear = new JButton("Clear Form");
        save.addActionListener(e -> onSave());
        clear.addActionListener(e -> clearForm());
        panel.add(save);
        panel.add(clear);
        return panel;
    }

    private void onSave() {
        Appointment appointment = (Appointment) appointmentBox.getSelectedItem();
        if (appointment == null) {
            showError("There are no appointments available to treat.");
            return;
        }
        try {
            Treatment created = controller.recordTreatment(
                    appointment.getAppointmentId(),
                    diagnosisArea.getText(),
                    prescriptionArea.getText(),
                    notesArea.getText());
            showInfo("Treatment " + created.getTreatmentId()
                    + " saved. Appointment marked as completed.");
            clearForm();
            refreshData();
        } catch (ValidationException ex) { showError(ex.getMessage()); }
    }

    private void clearForm() {
        diagnosisArea.setText("");
        prescriptionArea.setText("");
        notesArea.setText("");
    }

    private void fillTable(List<Treatment> treatments) {
        tableModel.setRowCount(0);
        for (Treatment t : treatments) {
            tableModel.addRow(new Object[]{
                t.getTreatmentId(), t.getAppointmentId(),
                controller.getPatientName(t.getPatientId()),
                t.getDoctorId(), t.getDate(), t.getDiagnosis(), t.getPrescription()
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

    @Override
    public void refreshData() {
        appointmentBox.removeAllItems();
        for (Appointment a : controller.getAllAppointments()) {
            if (!Appointment.STATUS_CANCELLED.equalsIgnoreCase(a.getStatus()))
                appointmentBox.addItem(a);
        }
        fillTable(controller.getAllTreatments());
    }
}