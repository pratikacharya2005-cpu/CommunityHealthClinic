package controller;

import java.util.List;
import model.Clinic;
import model.Doctor;
import util.Comparators;
import util.SortAlgorithms;
import util.ValidationException;
import util.Validator;

/** Coordinates all doctor use cases. */
public class DoctorController {

    private final Clinic clinic;

    public DoctorController(Clinic clinic) {
        if (clinic == null) throw new IllegalArgumentException("Clinic model must not be null.");
        this.clinic = clinic;
    }

    public Doctor registerDoctor(String id, String name, String phone, String email,
                                 String gender, String specialisation, String fee,
                                 String availableDays) throws ValidationException {

        Validator.validateId(id, "Doctor ID");
        Validator.validateName(name, "Doctor name");
        Validator.validatePhone(phone);
        Validator.validateEmail(email);
        Validator.validateGender(gender);
        Validator.requireText(specialisation, "Specialisation");
        double consultationFee = Validator.validateFee(fee);
        Validator.requireText(availableDays, "Available days");

        if (clinic.findDoctorById(id) != null)
            throw new ValidationException("Doctor ID '" + id.trim()
                    + "' already exists. Use Update instead.");

        Doctor doctor = new Doctor(id.trim(), name.trim(), phone.trim(), email.trim(),
                gender, specialisation.trim(), consultationFee, availableDays.trim());
        clinic.addDoctor(doctor);
        return doctor;
    }

    public Doctor updateDoctor(String id, String name, String phone, String email,
                               String gender, String specialisation, String fee,
                               String availableDays) throws ValidationException {

        Validator.validateId(id, "Doctor ID");
        Validator.validateName(name, "Doctor name");
        Validator.validatePhone(phone);
        Validator.validateEmail(email);
        Validator.validateGender(gender);
        Validator.requireText(specialisation, "Specialisation");
        double consultationFee = Validator.validateFee(fee);
        Validator.requireText(availableDays, "Available days");

        if (clinic.findDoctorById(id) == null)
            throw new ValidationException("Doctor ID '" + id.trim() + "' was not found.");

        Doctor updated = new Doctor(id.trim(), name.trim(), phone.trim(), email.trim(),
                gender, specialisation.trim(), consultationFee, availableDays.trim());
        clinic.updateDoctor(updated);
        return updated;
    }

    public void deleteDoctor(String id) throws ValidationException {
        Validator.validateId(id, "Doctor ID");
        if (!clinic.deleteDoctor(id.trim()))
            throw new ValidationException("Doctor ID '" + id.trim() + "' was not found.");
    }

    public List<Doctor> getAllDoctors() { return clinic.getDoctors(); }

    public List<Doctor> getDoctorsSortedByName() {
        List<Doctor> sorted = clinic.getDoctors();
        SortAlgorithms.insertionSort(sorted, Comparators.DOCTOR_BY_NAME);
        return sorted;
    }

    public Doctor findById(String id) { return clinic.findDoctorById(id); }
}