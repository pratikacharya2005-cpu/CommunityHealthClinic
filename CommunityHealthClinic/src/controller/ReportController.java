package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import model.Appointment;
import model.Clinic;
import model.Doctor;
import model.Patient;
import model.Treatment;
import util.Comparators;
import util.DataStore;
import util.SortAlgorithms;
import util.ValidationException;
import util.Validator;

/** Produces printable reports. */
public class ReportController {

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Clinic clinic;

    public ReportController(Clinic clinic) {
        if (clinic == null) throw new IllegalArgumentException("Clinic model must not be null.");
        this.clinic = clinic;
    }

    public String generateAppointmentReport() {
        List<Appointment> appointments = clinic.getAppointments();
        SortAlgorithms.quickSort(appointments, Comparators.APPOINTMENT_BY_DATE_TIME);

        int scheduled = 0, completed = 0, cancelled = 0;
        for (Appointment a : appointments) {
            if (Appointment.STATUS_SCHEDULED.equalsIgnoreCase(a.getStatus())) scheduled++;
            else if (Appointment.STATUS_COMPLETED.equalsIgnoreCase(a.getStatus())) completed++;
            else if (Appointment.STATUS_CANCELLED.equalsIgnoreCase(a.getStatus())) cancelled++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================\n");
        sb.append("            COMMUNITY HEALTH CLINIC - APPOINTMENT REPORT\n");
        sb.append("            Generated: ").append(LocalDateTime.now().format(STAMP)).append('\n');
        sb.append("================================================================\n");
        sb.append(String.format("Total appointments : %d%n", appointments.size()));
        sb.append(String.format("Scheduled          : %d%n", scheduled));
        sb.append(String.format("Completed          : %d%n", completed));
        sb.append(String.format("Cancelled          : %d%n", cancelled));
        sb.append("----------------------------------------------------------------\n");
        sb.append(String.format("%-7s %-11s %-6s %-18s %-18s %-10s%n",
                "ID", "DATE", "TIME", "PATIENT", "DOCTOR", "STATUS"));
        sb.append("----------------------------------------------------------------\n");

        if (appointments.isEmpty()) {
            sb.append("No appointments have been recorded yet.\n");
        } else {
            for (Appointment a : appointments) {
                Patient patient = clinic.findPatientById(a.getPatientId());
                Doctor doctor = clinic.findDoctorById(a.getDoctorId());
                sb.append(String.format("%-7s %-11s %-6s %-18s %-18s %-10s%n",
                        a.getAppointmentId(), a.getDate(), a.getTime(),
                        trim(patient == null ? a.getPatientId() : patient.getName(), 18),
                        trim(doctor == null ? a.getDoctorId() : "Dr. " + doctor.getName(), 18),
                        a.getStatus()));
            }
        }
        sb.append("================================================================\n");
        return sb.toString();
    }

    public String generateDoctorSchedule(String doctorId) throws ValidationException {
        Validator.validateId(doctorId, "Doctor ID");
        Doctor doctor = clinic.findDoctorById(doctorId);
        if (doctor == null)
            throw new ValidationException("Doctor ID '" + doctorId + "' was not found.");

        List<Appointment> appointments = clinic.getAppointments();
        SortAlgorithms.insertionSort(appointments, Comparators.APPOINTMENT_BY_DATE_TIME);

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================\n");
        sb.append("            DOCTOR SCHEDULE - Dr. ").append(doctor.getName()).append('\n');
        sb.append("            Specialisation : ").append(doctor.getSpecialisation()).append('\n');
        sb.append("            Available days : ").append(doctor.getAvailableDays()).append('\n');
        sb.append("            Generated      : ")
                .append(LocalDateTime.now().format(STAMP)).append('\n');
        sb.append("================================================================\n");
        sb.append(String.format("%-7s %-11s %-6s %-20s %-12s%n",
                "ID", "DATE", "TIME", "PATIENT", "STATUS"));
        sb.append("----------------------------------------------------------------\n");

        int count = 0;
        for (Appointment a : appointments) {
            if (!a.getDoctorId().equalsIgnoreCase(doctorId)) continue;
            Patient patient = clinic.findPatientById(a.getPatientId());
            sb.append(String.format("%-7s %-11s %-6s %-20s %-12s%n",
                    a.getAppointmentId(), a.getDate(), a.getTime(),
                    trim(patient == null ? a.getPatientId() : patient.getName(), 20),
                    a.getStatus()));
            count++;
        }
        if (count == 0) sb.append("No appointments are currently allocated to this doctor.\n");
        sb.append("----------------------------------------------------------------\n");
        sb.append("Total appointments listed: ").append(count).append('\n');
        sb.append("================================================================\n");
        return sb.toString();
    }

    public String generatePatientTreatmentHistory(String patientId) throws ValidationException {
        Validator.validateId(patientId, "Patient ID");
        Patient patient = clinic.findPatientById(patientId);
        if (patient == null)
            throw new ValidationException("Patient ID '" + patientId + "' was not found.");

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================\n");
        sb.append("            TREATMENT HISTORY - ").append(patient.getName()).append('\n');
        sb.append("            Generated: ").append(LocalDateTime.now().format(STAMP)).append('\n');
        sb.append("================================================================\n");

        int count = 0;
        for (Treatment t : clinic.getTreatments()) {
            if (!t.getPatientId().equalsIgnoreCase(patientId)) continue;
            sb.append("Treatment ID   : ").append(t.getTreatmentId()).append('\n');
            sb.append("Appointment ID : ").append(t.getAppointmentId()).append('\n');
            sb.append("Date           : ").append(t.getDate()).append('\n');
            sb.append("Diagnosis      : ").append(t.getDiagnosis()).append('\n');
            sb.append("Prescription   : ").append(t.getPrescription()).append('\n');
            sb.append("Notes          : ").append(t.getNotes()).append('\n');
            sb.append("----------------------------------------------------------------\n");
            count++;
        }
        if (count == 0) {
            sb.append("No treatments have been recorded for this patient.\n");
            sb.append("================================================================\n");
        }
        return sb.toString();
    }

    public void saveReport(String fileName, String content) throws IOException {
        String path = new File(clinic.getDataFolder(), fileName).getPath();
        DataStore.writeLines(path, Arrays.asList(content.split("\\R", -1)));
    }

    private String trim(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 1) + ".";
    }
}