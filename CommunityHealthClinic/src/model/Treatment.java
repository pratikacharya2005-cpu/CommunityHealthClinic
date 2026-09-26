package model;

import java.io.Serializable;

/** A clinical treatment recorded against a completed appointment. */
public class Treatment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String treatmentId;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;
    private String diagnosis;
    private String prescription;
    private String notes;

    public Treatment() { this("", "", "", "", "", "", "", ""); }

    public Treatment(String treatmentId, String appointmentId, String patientId,
                     String doctorId, String date, String diagnosis,
                     String prescription, String notes) {
        this.treatmentId = treatmentId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
    }

    public String getTreatmentId() { return treatmentId; }
    public void setTreatmentId(String treatmentId) { this.treatmentId = treatmentId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() { return treatmentId + " - " + date + " (" + diagnosis + ")"; }

    public String toRecord() {
        return safe(treatmentId) + "|" + safe(appointmentId) + "|" + safe(patientId) + "|"
                + safe(doctorId) + "|" + safe(date) + "|" + safe(diagnosis) + "|"
                + safe(prescription) + "|" + safe(notes);
    }

    public static Treatment fromRecord(String record) {
        String[] f = record.split("\\|", -1);
        if (f.length < 8)
            throw new IllegalArgumentException("Corrupt treatment record: " + record);
        return new Treatment(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
    }

    private static String safe(String value) {
        if (value == null) return "";
        return value.replace("|", "/").replace("\r", " ").replace("\n", " ").trim();
    }
}