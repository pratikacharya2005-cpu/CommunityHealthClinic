package model;

import java.io.Serializable;

/**
 * Abstract superclass for every person in the clinic system.
 * Demonstrates Inheritance, Abstraction and Encapsulation.
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String phone;
    private String email;
    private String gender;

    public Person() {
        this("", "", "", "", "");
    }

    public Person(String id, String name, String phone, String email, String gender) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    /** @return the role label shown in reports and lists. */
    public abstract String getRole();

    /** @return pipe-delimited record for the flat-file data store. */
    public abstract String toRecord();

    /** Strips characters that would corrupt the pipe-delimited file. */
    protected static String clean(String value) {
        if (value == null) return "";
        return value.replace("|", "/").replace("\r", " ").replace("\n", " ").trim();
    }

    @Override
    public String toString() { return id + " - " + name; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Person)) return false;
        Person p = (Person) other;
        return id != null && id.equals(p.id);
    }

    @Override
    public int hashCode() { return id == null ? 0 : id.hashCode(); }
}