package controller;

import java.util.List;
import model.Clinic;
import model.Patient;
import util.Comparators;
import util.SearchAlgorithms;
import util.SortAlgorithms;
import util.ValidationException;
import util.Validator;

/** Coordinates all patient use cases. */
public class PatientController {

    private final Clinic clinic;

    public PatientController(Clinic clinic) {
        if (clinic == null) throw new IllegalArgumentException("Clinic model must not be null.");
        this.clinic = clinic;
    }

    public Patient registerPatient(String id, String name, String phone, String email,
                                   String gender, String dateOfBirth, String address,
                                   String medicalHistory) throws ValidationException {

        Validator.validateId(id, "Patient ID");
        Validator.validateName(name, "Patient name");
        Validator.validatePhone(phone);
        Validator.validateEmail(email);
        Validator.validateGender(gender);
        Validator.validatePastDate(dateOfBirth);

        if (clinic.findPatientById(id) != null)
            throw new ValidationException("Patient ID '" + id.trim()
                    + "' is already registered. Use Update instead.");

        Patient patient = new Patient(id.trim(), name.trim(), phone.trim(), email.trim(),
                gender, dateOfBirth.trim(), safe(address), safe(medicalHistory));
        clinic.addPatient(patient);
        return patient;
    }

    public Patient updatePatient(String id, String name, String phone, String email,
                                 String gender, String dateOfBirth, String address,
                                 String medicalHistory) throws ValidationException {

        Validator.validateId(id, "Patient ID");
        Validator.validateName(name, "Patient name");
        Validator.validatePhone(phone);
        Validator.validateEmail(email);
        Validator.validateGender(gender);
        Validator.validatePastDate(dateOfBirth);

        if (clinic.findPatientById(id) == null)
            throw new ValidationException("Patient ID '" + id.trim() + "' was not found.");

        Patient updated = new Patient(id.trim(), name.trim(), phone.trim(), email.trim(),
                gender, dateOfBirth.trim(), safe(address), safe(medicalHistory));
        clinic.updatePatient(updated);
        return updated;
    }

    public void deletePatient(String id) throws ValidationException {
        Validator.validateId(id, "Patient ID");
        if (!clinic.deletePatient(id.trim()))
            throw new ValidationException("Patient ID '" + id.trim() + "' was not found.");
    }

    public List<Patient> getAllPatients() { return clinic.getPatients(); }

    public Patient searchByIdLinear(String id) throws ValidationException {
        Validator.validateId(id, "Patient ID");
        return SearchAlgorithms.linearSearchPatient(clinic.getPatients(), id);
    }

    public Patient searchByIdBinary(String id) throws ValidationException {
        Validator.validateId(id, "Patient ID");
        List<Patient> sorted = clinic.getPatients();
        SortAlgorithms.quickSort(sorted, Comparators.PATIENT_BY_ID);
        return SearchAlgorithms.binarySearchPatientById(sorted, id);
    }

    public List<Patient> getPatientsSortedByName() {
        List<Patient> sorted = clinic.getPatients();
        SortAlgorithms.insertionSort(sorted, Comparators.PATIENT_BY_NAME);
        return sorted;
    }

    public List<Patient> getPatientsSortedById() {
        List<Patient> sorted = clinic.getPatients();
        SortAlgorithms.quickSort(sorted, Comparators.PATIENT_BY_ID);
        return sorted;
    }

    private String safe(String value) { return value == null ? "" : value.trim(); }
}