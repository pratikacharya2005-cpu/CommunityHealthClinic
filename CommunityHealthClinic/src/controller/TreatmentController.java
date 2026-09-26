package controller;

import java.time.LocalDate;
import java.util.List;
import model.Appointment;
import model.Clinic;
import model.Treatment;
import util.ValidationException;
import util.Validator;

/** Coordinates treatment recording. */
public class TreatmentController {

    private final Clinic clinic;

    public TreatmentController(Clinic clinic) {
        if (clinic == null) throw new IllegalArgumentException("Clinic model must not be null.");
        this.clinic = clinic;
    }

    public Treatment recordTreatment(String appointmentId, String diagnosis,
                                     String prescription, String notes)
            throws ValidationException {

        Validator.requireText(appointmentId, "Appointment");
        Validator.requireText(diagnosis, "Diagnosis");
        Validator.requireText(prescription, "Prescription");

        Appointment appointment = clinic.findAppointmentById(appointmentId);
        if (appointment == null)
            throw new ValidationException("Appointment '" + appointmentId + "' was not found.");
        if (Appointment.STATUS_CANCELLED.equalsIgnoreCase(appointment.getStatus()))
            throw new ValidationException(
                    "A cancelled appointment cannot receive a treatment record.");

        Treatment treatment = new Treatment(clinic.nextTreatmentId(),
                appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), LocalDate.now().toString(),
                diagnosis.trim(), prescription.trim(), notes == null ? "" : notes.trim());

        clinic.addTreatment(treatment);
        appointment.setStatus(Appointment.STATUS_COMPLETED);
        clinic.updateAppointment(appointment);
        return treatment;
    }

    public List<Treatment> getAllTreatments() { return clinic.getTreatments(); }

    public List<Appointment> getAllAppointments() { return clinic.getAppointments(); }

    public String getPatientName(String patientId) {
        return clinic.findPatientById(patientId) == null
                ? patientId : clinic.findPatientById(patientId).getName();
    }
}