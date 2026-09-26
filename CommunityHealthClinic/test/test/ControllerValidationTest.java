package test;

import controller.AppointmentController;
import controller.PatientController;
import model.Clinic;
import model.Patient;
import org.junit.Before;
import org.junit.Test;
import util.ValidationException;
import static org.junit.Assert.*;

/** Unit tests for the controller layer's validation rules. */
public class ControllerValidationTest {

    private Clinic clinic;
    private PatientController patientController;
    private AppointmentController appointmentController;

    @Before public void setUp() {
        clinic = new Clinic();
        patientController = new PatientController(clinic);
        appointmentController = new AppointmentController(clinic);
    }

    @Test public void testRegisterValidPatient() throws ValidationException {
        Patient created = patientController.registerPatient("P001", "Alice Adams",
                "0412345678", "alice@example.com", "Female",
                "1990-03-15", "1 Queen St", "None");
        assertNotNull(created);
        assertEquals(1, patientController.getAllPatients().size());
    }

    @Test public void testRegisterPatientWithBlankNameIsRejected() {
        try {
            patientController.registerPatient("P001", "   ",
                    "0412345678", "alice@example.com", "Female",
                    "1990-03-15", "1 Queen St", "None");
            fail("Expected ValidationException for a blank name.");
        } catch (ValidationException expected) {
            assertTrue(expected.getMessage().contains("required"));
        }
    }

    @Test public void testRegisterPatientWithInvalidEmailIsRejected() {
        try {
            patientController.registerPatient("P001", "Alice Adams",
                    "0412345678", "not-an-email", "Female",
                    "1990-03-15", "1 Queen St", "None");
            fail("Expected ValidationException for an invalid email.");
        } catch (ValidationException expected) {
            assertTrue(expected.getMessage().contains("Email"));
        }
    }

    @Test public void testRegisterPatientWithFutureDobIsRejected() {
        try {
            patientController.registerPatient("P001", "Alice Adams",
                    "0412345678", "alice@example.com", "Female",
                    "2099-01-01", "1 Queen St", "None");
            fail("Expected ValidationException for a future date of birth.");
        } catch (ValidationException expected) {
            assertTrue(expected.getMessage().contains("future"));
        }
    }

    @Test public void testDuplicatePatientIdIsRejected() throws ValidationException {
        patientController.registerPatient("P001", "Alice Adams",
                "0412345678", "alice@example.com", "Female",
                "1990-03-15", "1 Queen St", "None");
        try {
            patientController.registerPatient("P001", "Another Person",
                    "0499999999", "other@example.com", "Male",
                    "1980-01-01", "2 Queen St", "None");
            fail("Expected ValidationException for a duplicate patient id.");
        } catch (ValidationException expected) {
            assertTrue(expected.getMessage().contains("already registered"));
        }
    }

    @Test public void testBookAppointmentForUnknownPatientIsRejected() {
        try {
            appointmentController.bookAppointment("P999", "D001",
                    "2026-09-27", "09:30", "");
            fail("Expected ValidationException for an unknown patient.");
        } catch (ValidationException expected) {
            assertTrue(expected.getMessage().contains("does not exist"));
        }
    }

    @Test public void testBookAppointmentWithInvalidTimeIsRejected() {
        try {
            appointmentController.bookAppointment("P001", "D001",
                    "2026-09-27", "25:99", "");
            fail("Expected ValidationException for an invalid time.");
        } catch (ValidationException expected) {
            assertTrue(expected.getMessage().contains("HH:mm"));
        }
    }
}