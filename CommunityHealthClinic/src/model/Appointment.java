package model;

import java.io.Serializable;

/**
 * A scheduled consultation between one patient and one doctor.
 * Implements Comparable so natural ordering is by date then time.
 */
public class Appointment implements Serializable, Comparable<Appointment> {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_SCHEDULED = "Scheduled";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;     // yyyy-MM-dd
    private String time;     // HH:mm
    private String status;
    private String notes;

    public Appointment() {
        this("", "", "", "", "", STATUS_SCHEDULED, "");
    }

    public Appointment(String appointmentId, String patientId, String doctorId,
                       String date, String time, String status, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.notes = notes;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDateTimeKey() { return date + " " + time; }

    @Override
    public int compareTo(Appointment other) {
        return getDateTimeKey().compareTo(other.getDateTimeKey());
    }

    @Override
    public String toString() { return appointmentId + " - " + date + " " + time; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Appointment)) return false;
        Appointment a = (Appointment) other;
        return appointmentId != null && appointmentId.equals(a.appointmentId);
    }

    @Override
    public int hashCode() { return appointmentId == null ? 0 : appointmentId.hashCode(); }

    public String toRecord() {
        return safe(appointmentId) + "|" + safe(patientId) + "|" + safe(doctorId) + "|"
                + safe(date) + "|" + safe(time) + "|" + safe(status) + "|" + safe(notes);
    }

    public static Appointment fromRecord(String record) {
        String[] f = record.split("\\|", -1);
        if (f.length < 7)
            throw new IllegalArgumentException("Corrupt appointment record: " + record);
        return new Appointment(f[0], f[1], f[2], f[3], f[4], f[5], f[6]);
    }

    private static String safe(String value) {
        if (value == null) return "";
        return value.replace("|", "/").replace("\r", " ").replace("\n", " ").trim();
    }
}