package test;

import java.util.ArrayList;
import java.util.List;
import model.Appointment;
import model.Patient;
import org.junit.Before;
import org.junit.Test;
import util.Comparators;
import util.SearchAlgorithms;
import util.SortAlgorithms;
import static org.junit.Assert.*;

/** Unit tests for hand-written search + sort algorithms. */
public class SearchSortAlgorithmsTest {

    private List<Patient> patients;
    private List<Appointment> appointments;

    @Before public void setUp() {
        patients = new ArrayList<>();
        patients.add(new Patient("P003", "Charlie Brown", "0400000003", "c@x.com",
                "Male", "1990-01-01", "-", "-"));
        patients.add(new Patient("P001", "Alice Adams", "0400000001", "a@x.com",
                "Female", "1985-05-05", "-", "-"));
        patients.add(new Patient("P002", "Bob Carter", "0400000002", "b@x.com",
                "Male", "1979-11-11", "-", "-"));

        appointments = new ArrayList<>();
        appointments.add(new Appointment("A0003", "P001", "D001",
                "2026-10-05", "11:00", Appointment.STATUS_SCHEDULED, ""));
        appointments.add(new Appointment("A0001", "P002", "D001",
                "2026-09-27", "09:30", Appointment.STATUS_SCHEDULED, ""));
        appointments.add(new Appointment("A0002", "P001", "D002",
                "2026-10-01", "15:45", Appointment.STATUS_SCHEDULED, ""));
    }

    @Test public void testLinearSearchFindsPatient() {
        Patient found = SearchAlgorithms.linearSearchPatient(patients, "P002");
        assertNotNull(found);
        assertEquals("Bob Carter", found.getName());
    }

    @Test public void testLinearSearchReturnsNullWhenAbsent() {
        assertNull(SearchAlgorithms.linearSearchPatient(patients, "P999"));
    }

    @Test public void testBinarySearchAfterSorting() {
        SortAlgorithms.quickSort(patients, Comparators.PATIENT_BY_ID);
        assertEquals("P001", patients.get(0).getId());
        assertEquals("P003", patients.get(2).getId());
        Patient found = SearchAlgorithms.binarySearchPatientById(patients, "P003");
        assertNotNull(found);
        assertEquals("Charlie Brown", found.getName());
    }

    @Test public void testBinarySearchReturnsNullWhenAbsent() {
        SortAlgorithms.quickSort(patients, Comparators.PATIENT_BY_ID);
        assertNull(SearchAlgorithms.binarySearchPatientById(patients, "P500"));
    }

    @Test public void testQuickSortByPatientName() {
        SortAlgorithms.quickSort(patients, Comparators.PATIENT_BY_NAME);
        assertEquals("Alice Adams", patients.get(0).getName());
        assertEquals("Bob Carter", patients.get(1).getName());
        assertEquals("Charlie Brown", patients.get(2).getName());
    }

    @Test public void testBubbleSortAppointmentsByDate() {
        SortAlgorithms.bubbleSort(appointments, Comparators.APPOINTMENT_BY_DATE_TIME);
        assertEquals("A0001", appointments.get(0).getAppointmentId());
        assertEquals("A0002", appointments.get(1).getAppointmentId());
        assertEquals("A0003", appointments.get(2).getAppointmentId());
    }

    @Test public void testInsertionSortAppointmentsByDate() {
        SortAlgorithms.insertionSort(appointments, Comparators.APPOINTMENT_BY_DATE_TIME);
        assertEquals("A0001", appointments.get(0).getAppointmentId());
        assertEquals("A0003", appointments.get(2).getAppointmentId());
    }

    @Test public void testSearchAppointmentsByPatient() {
        List<Appointment> result =
                SearchAlgorithms.searchAppointmentsByPatientId(appointments, "P001");
        assertEquals(2, result.size());
    }

    @Test public void testAlgorithmsHandleEmptyList() {
        List<Patient> empty = new ArrayList<>();
        SortAlgorithms.quickSort(empty, Comparators.PATIENT_BY_ID);
        SortAlgorithms.bubbleSort(empty, Comparators.PATIENT_BY_ID);
        SortAlgorithms.insertionSort(empty, Comparators.PATIENT_BY_ID);
        assertEquals(0, empty.size());
        assertNull(SearchAlgorithms.linearSearchPatient(empty, "P001"));
    }
}