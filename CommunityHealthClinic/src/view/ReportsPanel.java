package view;

import controller.DoctorController;
import controller.ReportController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import javax.swing.*;
import model.Doctor;
import util.ValidationException;

/** Report generation screen (appointment report + doctor schedule). */
public class ReportsPanel extends JPanel implements Refreshable {

    private final ReportController reportController;
    private final DoctorController doctorController;

    private final JComboBox<Doctor> doctorBox = new JComboBox<>();
    private final JTextArea outputArea = new JTextArea();
    private String lastReport = "";

    public ReportsPanel(ReportController reportController, DoctorController doctorController) {
        this.reportController = reportController;
        this.doctorController = doctorController;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton appointmentReport = new JButton("Appointment Report");
        appointmentReport.addActionListener(e -> showAppointmentReport());
        top.add(appointmentReport);

        top.add(new JLabel("  Doctor:"));
        top.add(doctorBox);
        JButton schedule = new JButton("Doctor Schedule");
        schedule.addActionListener(e -> showDoctorSchedule());
        top.add(schedule);

        JButton save = new JButton("Save Report to File");
        save.addActionListener(e -> saveReport());
        top.add(save);

        add(top, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        refreshData();
    }

    private void showAppointmentReport() {
        lastReport = reportController.generateAppointmentReport();
        outputArea.setText(lastReport);
        outputArea.setCaretPosition(0);
    }

    private void showDoctorSchedule() {
        Doctor doctor = (Doctor) doctorBox.getSelectedItem();
        if (doctor == null) {
            JOptionPane.showMessageDialog(this,
                    "No doctors have been registered yet.",
                    "Reports", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            lastReport = reportController.generateDoctorSchedule(doctor.getId());
            outputArea.setText(lastReport);
            outputArea.setCaretPosition(0);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveReport() {
        if (lastReport == null || lastReport.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Generate a report before saving.",
                    "Reports", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            reportController.saveReport("clinic_report.txt", lastReport);
            JOptionPane.showMessageDialog(this,
                    "Report saved to the clinic data folder as clinic_report.txt.",
                    "Report Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the report:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refreshData() {
        doctorBox.removeAllItems();
        for (Doctor d : doctorController.getDoctorsSortedByName()) doctorBox.addItem(d);
    }
}