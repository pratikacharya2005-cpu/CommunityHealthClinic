package test;

import model.Appointment;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for Appointment. Test objective: verify chronological ordering. */
public class AppointmentTest {

    private Appointment morning;
    private Appointment afternoon;

    @Before public void setUp() {
        morning = new Appointment("A0001", "P001", "D001",
                "2026-09-27", "09:30", Appointment.STATUS_SCHEDULED, "First visit");
        afternoon = new Appointment("A0002", "P002", "D001",
                "2026-09-27", "14:00", Appointment.STATUS_SCHEDULED, "Follow up");
    }

    @Test public void testConstructorPopulatesFields() {
        assertEquals("A0001", morning.getAppointmentId());
        assertEquals("P001", morning.getPatientId());
        assertEquals("D001", morning.getDoctorId());
        assertEquals("2026-09-27", morning.getDate());
        assertEquals("09:30", morning.getTime());
        assertEquals(Appointment.STATUS_SCHEDULED, morning.getStatus());
    }

    @Test public void testCompareToOrdersByTime() {
        assertTrue(morning.compareTo(afternoon) < 0);
        assertTrue(afternoon.compareTo(morning) > 0);
    }

    @Test public void testCompareToOrdersByDateFirst() {
        Appointment laterDay = new Appointment("A0003", "P003", "D002",
                "2026-10-01", "08:00", Appointment.STATUS_SCHEDULED, "");
        assertTrue(morning.compareTo(laterDay) < 0);
    }

    @Test public void testDateTimeKey() {
        assertEquals("2026-09-27 09:30", morning.getDateTimeKey());
    }

    @Test public void testStatusUpdate() {
        morning.setStatus(Appointment.STATUS_COMPLETED);
        assertEquals(Appointment.STATUS_COMPLETED, morning.getStatus());
    }

    @Test public void testRecordRoundTrip() {
        Appointment restored = Appointment.fromRecord(morning.toRecord());
        assertEquals(morning.getAppointmentId(), restored.getAppointmentId());
        assertEquals(morning.getDate(), restored.getDate());
        assertEquals(morning.getTime(), restored.getTime());
        assertEquals(morning.getStatus(), restored.getStatus());
    }
}