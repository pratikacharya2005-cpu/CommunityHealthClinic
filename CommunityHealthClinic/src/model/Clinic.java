package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.DataStore;

/**
 * In-memory repository (facade) for the whole clinic domain.
 * High cohesion: it owns the collections and exposes domain operations.
 * Low coupling: it only knows DataStore, never Swing or controllers.
 */
public class Clinic {

    private final ArrayList<Patient> patients = new ArrayList<>();
    private final ArrayList<Doctor> doctors = new ArrayList<>();
    private final ArrayList<Appointment> appointments = new ArrayList<>();
    private final ArrayList<Treatment> treatments = new ArrayList<>();
    private final ArrayList<Administrator> administrators = new ArrayList<>();

    private String dataFolder = "data";

    public Clinic() { }

    public String getDataFolder() { return dataFolder; }
    public void setDataFolder(String dataFolder) { this.dataFolder = dataFolder; }

    private String path(String fileName) { return new File(dataFolder, fileName).getPath(); }

    // ---- Patient ----------------------------------------------------------
    public void addPatient(Patient patient) { patients.add(patient); }

    public boolean updatePatient(Patient updated) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equalsIgnoreCase(updated.getId())) {
                patients.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public boolean deletePatient(String id) {
        Patient found = findPatientById(id);
        if (found == null) return false;
        patients.remove(found);
        appointments.removeIf(a -> a.getPatientId().equalsIgnoreCase(id));
        return true;
    }

    public Patient findPatientById(String id) {
        if (id == null) return null;
        for (Patient p : patients) {
            if (p.getId().equalsIgnoreCase(id.trim())) return p;
        }
        return null;
    }

    public List<Patient> getPatients() { return new ArrayList<>(patients); }

    // ---- Doctor -----------------------------------------------------------
    public void addDoctor(Doctor doctor) { doctors.add(doctor); }

    public boolean updateDoctor(Doctor updated) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equalsIgnoreCase(updated.getId())) {
                doctors.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public boolean deleteDoctor(String id) {
        Doctor found = findDoctorById(id);
        if (found == null) return false;
        doctors.remove(found);
        return true;
    }

    public Doctor findDoctorById(String id) {
        if (id == null) return null;
        for (Doctor d : doctors) {
            if (d.getId().equalsIgnoreCase(id.trim())) return d;
        }
        return null;
    }

    public List<Doctor> getDoctors() { return new ArrayList<>(doctors); }

    // ---- Appointment ------------------------------------------------------
    public void addAppointment(Appointment appointment) { appointments.add(appointment); }

    public boolean updateAppointment(Appointment updated) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getAppointmentId()
                    .equalsIgnoreCase(updated.getAppointmentId())) {
                appointments.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public boolean deleteAppointment(String appointmentId) {
        Appointment found = findAppointmentById(appointmentId);
        if (found == null) return false;
        appointments.remove(found);
        return true;
    }

    public Appointment findAppointmentById(String appointmentId) {
        if (appointmentId == null) return null;
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equalsIgnoreCase(appointmentId.trim())) return a;
        }
        return null;
    }

    public List<Appointment> getAppointments() { return new ArrayList<>(appointments); }

    public boolean isDoctorBooked(String doctorId, String date, String time,
                                  String excludeAppointmentId) {
        for (Appointment a : appointments) {
            if (Appointment.STATUS_CANCELLED.equalsIgnoreCase(a.getStatus())) continue;
            boolean sameDoctor = a.getDoctorId().equalsIgnoreCase(doctorId);
            boolean sameSlot = a.getDate().equalsIgnoreCase(date)
                    && a.getTime().equalsIgnoreCase(time);
            boolean notExcluded = excludeAppointmentId == null
                    || !a.getAppointmentId().equalsIgnoreCase(excludeAppointmentId);
            if (sameDoctor && sameSlot && notExcluded) return true;
        }
        return false;
    }

    public String nextAppointmentId() {
        int max = 0;
        for (Appointment a : appointments) max = Math.max(max, extractNumber(a.getAppointmentId()));
        return String.format("A%04d", max + 1);
    }

    // ---- Treatment --------------------------------------------------------
    public void addTreatment(Treatment treatment) { treatments.add(treatment); }

    public Treatment findTreatmentById(String treatmentId) {
        if (treatmentId == null) return null;
        for (Treatment t : treatments) {
            if (t.getTreatmentId().equalsIgnoreCase(treatmentId.trim())) return t;
        }
        return null;
    }

    public List<Treatment> getTreatments() { return new ArrayList<>(treatments); }

    public String nextTreatmentId() {
        int max = 0;
        for (Treatment t : treatments) max = Math.max(max, extractNumber(t.getTreatmentId()));
        return String.format("T%04d", max + 1);
    }

    // ---- Administrator ----------------------------------------------------
    public void addAdministrator(Administrator administrator) { administrators.add(administrator); }

    public List<Administrator> getAdministrators() { return new ArrayList<>(administrators); }

    public Administrator findAdministratorByUsername(String username) {
        for (Administrator a : administrators) {
            if (a.getUsername().equalsIgnoreCase(username)) return a;
        }
        return null;
    }

    public void seedDefaultAdministrator() {
        if (administrators.isEmpty()) {
            administrators.add(new Administrator("ADM01", "System Administrator",
                    "0399998888", "admin@clinic.com", "Other",
                    "admin", "admin123", "System Administrator"));
        }
    }

    // ---- Persistence ------------------------------------------------------
    public void saveAll() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Patient p : patients) lines.add(p.toRecord());
        DataStore.writeLines(path("patients.txt"), lines);

        lines = new ArrayList<>();
        for (Doctor d : doctors) lines.add(d.toRecord());
        DataStore.writeLines(path("doctors.txt"), lines);

        lines = new ArrayList<>();
        for (Appointment a : appointments) lines.add(a.toRecord());
        DataStore.writeLines(path("appointments.txt"), lines);

        lines = new ArrayList<>();
        for (Treatment t : treatments) lines.add(t.toRecord());
        DataStore.writeLines(path("treatments.txt"), lines);

        lines = new ArrayList<>();
        for (Administrator a : administrators) lines.add(a.toRecord());
        DataStore.writeLines(path("administrators.txt"), lines);
    }

    public void loadAll() throws IOException {
        try {
            patients.clear();
            for (String line : DataStore.readLines(path("patients.txt"))) {
                if (!line.trim().isEmpty()) patients.add(Patient.fromRecord(line));
            }

            doctors.clear();
            for (String line : DataStore.readLines(path("doctors.txt"))) {
                if (!line.trim().isEmpty()) doctors.add(Doctor.fromRecord(line));
            }

            appointments.clear();
            for (String line : DataStore.readLines(path("appointments.txt"))) {
                if (!line.trim().isEmpty()) appointments.add(Appointment.fromRecord(line));
            }

            treatments.clear();
            for (String line : DataStore.readLines(path("treatments.txt"))) {
                if (!line.trim().isEmpty()) treatments.add(Treatment.fromRecord(line));
            }

            administrators.clear();
            for (String line : DataStore.readLines(path("administrators.txt"))) {
                if (!line.trim().isEmpty()) administrators.add(Administrator.fromRecord(line));
            }
        } catch (IllegalArgumentException ex) {
            throw new IOException("Data file is corrupt: " + ex.getMessage(), ex);
        }
    }

    public List<Patient> patientsSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(patients));
    }

    private int extractNumber(String id) {
        if (id == null) return 0;
        String digits = id.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        try { return Integer.parseInt(digits); }
        catch (NumberFormatException ex) { return 0; }
    }
}