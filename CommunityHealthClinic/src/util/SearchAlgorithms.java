package util;

import java.util.ArrayList;
import java.util.List;
import model.Appointment;
import model.Patient;

/** Hand-written searching algorithms (linear + binary). */
public final class SearchAlgorithms {

    private SearchAlgorithms() { }

    public static int linearSearchPatientIndex(List<Patient> patients, String id) {
        if (patients == null || id == null) return -1;
        String key = id.trim();
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equalsIgnoreCase(key)) return i;
        }
        return -1;
    }

    public static Patient linearSearchPatient(List<Patient> patients, String id) {
        int index = linearSearchPatientIndex(patients, id);
        return index < 0 ? null : patients.get(index);
    }

    public static Patient binarySearchPatientById(List<Patient> sortedPatients, String id) {
        if (sortedPatients == null || id == null) return null;
        String key = id.trim();
        int low = 0;
        int high = sortedPatients.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = sortedPatients.get(mid).getId().compareToIgnoreCase(key);
            if (comparison == 0) return sortedPatients.get(mid);
            if (comparison < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    public static List<Appointment> searchAppointmentsByPatientId(
            List<Appointment> appointments, String patientId) {
        List<Appointment> results = new ArrayList<>();
        if (appointments == null || patientId == null) return results;
        String key = patientId.trim();
        for (Appointment a : appointments) {
            if (a.getPatientId().equalsIgnoreCase(key)) results.add(a);
        }
        return results;
    }
}