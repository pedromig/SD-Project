package utils.people;

import java.io.Serializable;
import java.util.GregorianCalendar;

public abstract class Person implements Serializable {
    private int phoneNumber, identityCardNumber;
    private boolean inList;
    private String name, password, address, faculty, department;
    private GregorianCalendar identityCardExpiryDate;

    public Person(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        this.phoneNumber = phoneNumber;
        this.identityCardNumber = identityCardNumber;
        this.name = name;
        this.password = password;
        this.address = address;
        this.faculty = faculty;
        this.department = department;
        this.identityCardExpiryDate = identityCardExpiryDate;
        this.inList = false;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return  "    Name: "    + this.name        +
                "\tFaculty: "   + this.faculty     +
                "\tDept: "      + this.department;
    }

}
