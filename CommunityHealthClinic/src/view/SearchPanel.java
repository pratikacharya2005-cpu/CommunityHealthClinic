package view;

import controller.AppointmentController;
import controller.PatientController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.*;
import model.Appointment;
import model.Patient;
import util.ValidationException;

/** Search screen demonstrating linear and binary search (Task 8). */
public class SearchPanel extends JPanel {

    private static final String MODE_LINEAR = "Patient by ID - Linear Search";
    private static final String MODE_BINARY = "Patient by ID - Binary Search";
    private static final String MODE_APPOINTMENTS = "Appointments by Patient ID";

    private final PatientController patientController;
    private final AppointmentController appointmentController;

    private final JComboBox<String> modeBox = new JComboBox<>(new String[]{
        MODE_LINEAR, MODE_BINARY, MODE_APPOINTMENTS});
    private final JTextField queryField = new JTextField(15);
    private final JTextArea resultArea = new JTextArea();

    public SearchPanel(PatientController patientController,
                       AppointmentController appointmentController) {
        this.patientController = patientController;
        this.appointmentController = appointmentController;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.add(new JLabel("Search type:"));
        top.add(modeBox);
        top.add(new JLabel("Search value:"));
        top.add(queryField);
        JButton search = new JButton("Search");
        search.addActionListener(e -> performSearch());
        top.add(search);
        add(top, BorderLayout.NORTH);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void performSearch() {
        String value = queryField.getText();
        String mode = (String) modeBox.getSelectedItem();
        resultArea.setText("");
        try {
            if (MODE_LINEAR.equals(mode)) searchPatientLinear(value);
            else if (MODE_BINARY.equals(mode)) searchPatientBinary(value);
            else searchAppointments(value);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPatientLinear(String value) throws ValidationException {
        long start = System.nanoTime();
        Patient patient = patientController.searchByIdLinear(value);
        long elapsed = System.nanoTime() - start;
        resultArea.setText("Algorithm : Linear Search (O(n))\n"
                + "Executed in " + elapsed + " ns\n"
                + "------------------------------------------------------------\n"
                + (patient == null
                        ? "No patient found with ID " + value + ".\n"
                        : formatPatient(patient)));
    }

    private void searchPatientBinary(String value) throws ValidationException {
        long start = System.nanoTime();
        Patient patient = patientController.searchByIdBinary(value);
        long elapsed = System.nanoTime() - start;
        resultArea.setText("Algorithm : Binary Search (O(log n)) - list sorted by ID first\n"
                + "Executed in " + elapsed + " ns\n"
                + "------------------------------------------------------------\n"
                + (patient == null
                        ? "No patient found with ID " + value + ".\n"
                        : formatPatient(patient)));
    }

    private void searchAppointments(String value) throws ValidationException {
        // Single validation pass - reuse for both the header and the search.
        Patient owner = patientController.searchByIdLinear(value);
        java.util.List<Appointment> results = appointmentController.searchByPatientId(value);

        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm : Linear Search over appointments\n");
        sb.append("Patient   : ").append(owner == null ? value : owner.getName()).append('\n');
        sb.append("------------------------------------------------------------\n");
        if (results.isEmpty()) {
            sb.append("No appointments found for patient ID ").append(value).append(".\n");
        } else {
            for (Appointment a : results) {
                sb.append(String.format("%-7s %-11s %-6s %-18s %-12s%n",
                        a.getAppointmentId(), a.getDate(), a.getTime(),
                        appointmentController.getDoctorName(a.getDoctorId()),
                        a.getStatus()));
            }
        }
        resultArea.setText(sb.toString());
    }

    private String formatPatient(Patient p) {
        return "Patient ID      : " + p.getId() + "\n"
                + "Name            : " + p.getName() + "\n"
                + "Phone           : " + p.getPhone() + "\n"
                + "Email           : " + p.getEmail() + "\n"
                + "Gender          : " + p.getGender() + "\n"
                + "Date of Birth   : " + p.getDateOfBirth() + "\n"
                + "Address         : " + p.getAddress() + "\n"
                + "Medical History : " + p.getMedicalHistory() + "\n";
    }
}