package model;

/** System administrator who operates the desktop application. */
public class Administrator extends Person {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String jobTitle;

    public Administrator() { super(); }

    public Administrator(String id, String name, String phone, String email, String gender,
                         String username, String password, String jobTitle) {
        super(id, name, phone, email, gender);
        this.username = username;
        this.password = password;
        this.jobTitle = jobTitle;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    @Override public String getRole() { return "Administrator"; }

    @Override
    public String toRecord() {
        return clean(getId()) + "|" + clean(getName()) + "|" + clean(getPhone()) + "|"
                + clean(getEmail()) + "|" + clean(getGender()) + "|" + clean(username) + "|"
                + clean(password) + "|" + clean(jobTitle);
    }

    public static Administrator fromRecord(String record) {
        String[] f = record.split("\\|", -1);
        if (f.length < 8)
            throw new IllegalArgumentException("Corrupt administrator record: " + record);
        return new Administrator(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
    }
}