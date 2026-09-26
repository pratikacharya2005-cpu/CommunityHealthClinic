package test;

import model.Patient;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for Patient. Test objective: verify state + record round-trip. */
public class PatientTest {

    private Patient patient;

    @Before
    public void setUp() {
        patient = new Patient("P001", "John Smith", "0412345678", "john@example.com",
                "Male", "1985-04-12", "12 Main Street", "Asthma");
    }

    @Test public void testConstructorPopulatesAllFields() {
        assertEquals("P001", patient.getId());
        assertEquals("John Smith", patient.getName());
        assertEquals("0412345678", patient.getPhone());
        assertEquals("john@example.com", patient.getEmail());
        assertEquals("Male", patient.getGender());
        assertEquals("1985-04-12", patient.getDateOfBirth());
        assertEquals("12 Main Street", patient.getAddress());
        assertEquals("Asthma", patient.getMedicalHistory());
    }

    @Test public void testGetRoleReturnsPatient() { assertEquals("Patient", patient.getRole()); }

    @Test public void testSettersUpdateState() {
        patient.setName("Jane Doe");
        patient.setPhone("0498765432");
        assertEquals("Jane Doe", patient.getName());
        assertEquals("0498765432", patient.getPhone());
    }

    @Test public void testRecordRoundTrip() {
        Patient restored = Patient.fromRecord(patient.toRecord());
        assertEquals(patient.getId(), restored.getId());
        assertEquals(patient.getName(), restored.getName());
        assertEquals(patient.getEmail(), restored.getEmail());
        assertEquals(patient.getDateOfBirth(), restored.getDateOfBirth());
        assertEquals(patient.getMedicalHistory(), restored.getMedicalHistory());
    }

    @Test public void testRecordSanitisesDelimiter() {
        patient.setAddress("12 Main | Street");
        String record = patient.toRecord();
        assertEquals(7, record.chars().filter(c -> c == '|').count());
        assertTrue(record.contains("12 Main / Street"));
    }

    @Test public void testEqualsUsesId() {
        Patient same = new Patient("P001", "Different Name", "0400000000",
                "x@y.com", "Female", "1990-01-01", "-", "-");
        Patient other = new Patient("P002", "John Smith", "0412345678",
                "john@example.com", "Male", "1985-04-12", "12 Main Street", "Asthma");
        assertEquals(patient, same);
        assertNotEquals(patient, other);
        assertFalse(patient.equals(null));
    }

    @Test public void testFromRecordRejectsCorruptData() {
        try {
            Patient.fromRecord("P001|Only Two Fields");
            fail("Expected IllegalArgumentException for a corrupt record.");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Corrupt patient record"));
        }
    }
}