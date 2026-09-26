package controller;

import java.util.List;
import model.Appointment;
import model.Clinic;
import model.Doctor;
import model.Patient;
import util.Comparators;
import util.SearchAlgorithms;
import util.SortAlgorithms;
import util.ValidationException;
import util.Validator;

/** Coordinates appointment booking, cancellation, searching and sorting. */
public class AppointmentController {

    private final Clinic clinic;

    public AppointmentController(Clinic clinic) {
        if (clinic == null) throw new IllegalArgumentException("Clinic model must not be null.");
        this.clinic = clinic;
    }

    public Appointment bookAppointment(String patientId, String doctorId, String date,
                                       String time, String notes) throws ValidationException {

        Validator.validateId(patientId, "Patient ID");
        Validator.validateId(doctorId, "Doctor ID");
        Validator.validateDate(date, "Appointment date");
        Validator.validateTime(time);

        if (clinic.findPatientById(patientId) == null)
            throw new ValidationException("Patient ID '" + patientId.trim() + "' does not exist.");
        if (clinic.findDoctorById(doctorId) == null)
            throw new ValidationException("Doctor ID '" + doctorId.trim() + "' does not exist.");
        if (clinic.isDoctorBooked(doctorId.trim(), date.trim(), time.trim(), null))
            throw new ValidationException("Dr. " + clinic.findDoctorById(doctorId).getName()
                    + " already has an appointment on " + date.trim() + " at " + time.trim() + ".");

        Appointment appointment = new Appointment(clinic.nextAppointmentId(), patientId.trim(),
                doctorId.trim(), date.trim(), time.trim(),
                Appointment.STATUS_SCHEDULED, notes == null ? "" : notes.trim());
        clinic.addAppointment(appointment);
        return appointment;
    }

    public void updateStatus(String appointmentId, String status) throws ValidationException {
        Validator.requireText(appointmentId, "Appointment ID");
        Validator.requireText(status, "Status");
        Appointment appointment = clinic.findAppointmentById(appointmentId);
        if (appointment == null)
            throw new ValidationException("Appointment '" + appointmentId + "' was not found.");
        appointment.setStatus(status);
        clinic.updateAppointment(appointment);
    }

    public void cancelAppointment(String appointmentId) throws ValidationException {
        updateStatus(appointmentId, Appointment.STATUS_CANCELLED);
    }

    public void deleteAppointment(String appointmentId) throws ValidationException {
        Validator.requireText(appointmentId, "Appointment ID");
        if (!clinic.deleteAppointment(appointmentId.trim()))
            throw new ValidationException("Appointment '" + appointmentId + "' was not found.");
    }

    public List<Appointment> getAllAppointments() { return clinic.getAppointments(); }

    /** Exposes patients so the AppointmentPanel combo can be populated. */
    public List<Patient> getAllPatients() { return clinic.getPatients(); }

    /** Exposes doctors so the AppointmentPanel combo can be populated. */
    public List<Doctor> getAllDoctors() { return clinic.getDoctors(); }

    public List<Appointment> getAppointmentsSortedByDateBubble() {
        List<Appointment> sorted = clinic.getAppointments();
        SortAlgorithms.bubbleSort(sorted, Comparators.APPOINTMENT_BY_DATE_TIME);
        return sorted;
    }

    public List<Appointment> getAppointmentsSortedByDateInsertion() {
        List<Appointment> sorted = clinic.getAppointments();
        SortAlgorithms.insertionSort(sorted, Comparators.APPOINTMENT_BY_DATE_TIME);
        return sorted;
    }

    public List<Appointment> getAppointmentsSortedByDateQuick() {
        List<Appointment> sorted = clinic.getAppointments();
        SortAlgorithms.quickSort(sorted, Comparators.APPOINTMENT_BY_DATE_TIME);
        return sorted;
    }

    public List<Appointment> searchByPatientId(String patientId) throws ValidationException {
        Validator.validateId(patientId, "Patient ID");
        return SearchAlgorithms.searchAppointmentsByPatientId(
                clinic.getAppointments(), patientId);
    }

    public String getPatientName(String patientId) {
        Patient patient = clinic.findPatientById(patientId);
        return patient == null ? patientId : patient.getName();
    }

    public String getDoctorName(String doctorId) {
        Doctor doctor = clinic.findDoctorById(doctorId);
        return doctor == null ? doctorId : doctor.getName();
    }

    public Appointment findById(String appointmentId) {
        return clinic.findAppointmentById(appointmentId);
    }
}