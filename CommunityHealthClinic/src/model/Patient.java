package model;

/** Represents a patient registered at the clinic. */
public class Patient extends Person {

    private static final long serialVersionUID = 1L;

    private String dateOfBirth;      // ISO: yyyy-MM-dd
    private String address;
    private String medicalHistory;

    public Patient() { super(); }

    public Patient(String id, String name, String phone, String email, String gender,
                   String dateOfBirth, String address, String medicalHistory) {
        super(id, name, phone, email, gender);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    @Override public String getRole() { return "Patient"; }

    @Override
    public String toRecord() {
        return clean(getId()) + "|" + clean(getName()) + "|" + clean(getPhone()) + "|"
                + clean(getEmail()) + "|" + clean(getGender()) + "|" + clean(dateOfBirth) + "|"
                + clean(address) + "|" + clean(medicalHistory);
    }

    public static Patient fromRecord(String record) {
        String[] f = record.split("\\|", -1);
        if (f.length < 8) throw new IllegalArgumentException("Corrupt patient record: " + record);
        return new Patient(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
    }
}