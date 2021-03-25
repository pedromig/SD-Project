package utils.people;

import java.util.GregorianCalendar;

public abstract class Person {
    int phoneNumber, identityCardNumber;
    String name, password, address, faculty, department;
    GregorianCalendar identityCardExpiryDate;

    public Person(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        this.phoneNumber = phoneNumber;
        this.identityCardNumber = identityCardNumber;
        this.name = name;
        this.password = password;
        this.address = address;
        this.faculty = faculty;
        this.department = department;
        this.identityCardExpiryDate = identityCardExpiryDate;
    }
}
