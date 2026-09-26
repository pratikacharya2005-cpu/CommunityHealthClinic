package test;

import java.io.File;
import java.io.IOException;
import model.Appointment;
import model.Clinic;
import model.Doctor;
import model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for the file-based persistence layer. */
public class ClinicPersistenceTest {

    private Clinic clinic;
    private File tempFolder;

    @Before public void setUp() {
        tempFolder = new File(System.getProperty("java.io.tmpdir"),
                "clinic_test_" + System.nanoTime());
        clinic = new Clinic();
        clinic.setDataFolder(tempFolder.getPath());

        clinic.addPatient(new Patient("P001", "Alice Adams", "0412345678",
                "alice@example.com", "Female", "1990-03-15", "1 Queen St", "None"));
        clinic.addDoctor(new Doctor("D001", "Gregory House", "0398765432",
                "house@clinic.com", "Male", "Diagnostics", 120.0, "Mon,Tue"));
        clinic.addAppointment(new Appointment("A0001", "P001", "D001",
                "2026-09-27", "09:30", Appointment.STATUS_SCHEDULED, "Initial consult"));
    }

    @After public void tearDown() {
        File[] files = tempFolder.listFiles();
        if (files != null) for (File f : files) f.delete();
        tempFolder.delete();
    }

    @Test public void testSaveCreatesDataFiles() throws IOException {
        clinic.saveAll();
        assertTrue(new File(tempFolder, "patients.txt").exists());
        assertTrue(new File(tempFolder, "doctors.txt").exists());
        assertTrue(new File(tempFolder, "appointments.txt").exists());
        assertTrue(new File(tempFolder, "treatments.txt").exists());
    }

    @Test public void testSaveAndLoadRoundTrip() throws IOException {
        clinic.saveAll();

        Clinic reloaded = new Clinic();
        reloaded.setDataFolder(tempFolder.getPath());
        reloaded.loadAll();

        assertEquals(1, reloaded.getPatients().size());
        assertEquals(1, reloaded.getDoctors().size());
        assertEquals(1, reloaded.getAppointments().size());
        assertNotNull(reloaded.findPatientById("P001"));
        assertEquals("Alice Adams", reloaded.findPatientById("P001").getName());
        assertEquals(120.0, reloaded.findDoctorById("D001").getConsultationFee(), 0.001);
        assertEquals("2026-09-27", reloaded.findAppointmentById("A0001").getDate());
    }

    @Test public void testDeletePatientCascadesToAppointments() {
        assertEquals(1, clinic.getAppointments().size());
        clinic.deletePatient("P001");
        assertEquals(0, clinic.getPatients().size());
        assertEquals(0, clinic.getAppointments().size());
    }

    @Test public void testNextAppointmentIdIncrements() {
        assertEquals("A0002", clinic.nextAppointmentId());
        clinic.addAppointment(new Appointment("A0002", "P001", "D001",
                "2026-09-28", "10:00", Appointment.STATUS_SCHEDULED, ""));
        assertEquals("A0003", clinic.nextAppointmentId());
    }

    @Test public void testDoctorDoubleBookingDetection() {
        assertTrue(clinic.isDoctorBooked("D001", "2026-09-27", "09:30", null));
        assertFalse(clinic.isDoctorBooked("D001", "2026-09-27", "11:00", null));
        assertFalse(clinic.isDoctorBooked("D001", "2026-09-27", "09:30", "A0001"));
    }
}