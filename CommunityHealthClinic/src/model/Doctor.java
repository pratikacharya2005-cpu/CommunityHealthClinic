package model;

/** Represents a doctor who can be allocated to appointments. */
public class Doctor extends Person {

    private static final long serialVersionUID = 1L;

    private String specialisation;
    private double consultationFee;
    private String availableDays;   // e.g. "Mon,Wed,Fri"

    public Doctor() { super(); }

    public Doctor(String id, String name, String phone, String email, String gender,
                  String specialisation, double consultationFee, String availableDays) {
        super(id, name, phone, email, gender);
        this.specialisation = specialisation;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
    }

    public String getSpecialisation() { return specialisation; }
    public void setSpecialisation(String specialisation) { this.specialisation = specialisation; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    @Override public String getRole() { return "Doctor"; }

    @Override
    public String toRecord() {
        return clean(getId()) + "|" + clean(getName()) + "|" + clean(getPhone()) + "|"
                + clean(getEmail()) + "|" + clean(getGender()) + "|" + clean(specialisation) + "|"
                + consultationFee + "|" + clean(availableDays);
    }

    public static Doctor fromRecord(String record) {
        String[] f = record.split("\\|", -1);
        if (f.length < 8) throw new IllegalArgumentException("Corrupt doctor record: " + record);
        double fee;
        try {
            fee = Double.parseDouble(f[6]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid consultation fee: " + record, ex);
        }
        return new Doctor(f[0], f[1], f[2], f[3], f[4], f[5], fee, f[7]);
    }
}