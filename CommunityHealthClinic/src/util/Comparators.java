package util;

import java.util.Comparator;
import model.Appointment;
import model.Doctor;
import model.Patient;

/** Reusable ordering rules shared by sorts and GUI tables. */
public final class Comparators {

    private Comparators() { }

    public static final Comparator<Patient> PATIENT_BY_ID =
            (a, b) -> a.getId().compareToIgnoreCase(b.getId());

    public static final Comparator<Patient> PATIENT_BY_NAME =
            (a, b) -> a.getName().compareToIgnoreCase(b.getName());

    public static final Comparator<Doctor> DOCTOR_BY_NAME =
            (a, b) -> a.getName().compareToIgnoreCase(b.getName());

    public static final Comparator<Appointment> APPOINTMENT_BY_DATE_TIME =
            (a, b) -> a.getDateTimeKey().compareTo(b.getDateTimeKey());

    public static final Comparator<Appointment> APPOINTMENT_BY_ID =
            (a, b) -> a.getAppointmentId().compareToIgnoreCase(b.getAppointmentId());
}