package utils.people;

import java.util.GregorianCalendar;

public class Teacher extends Person {
    public Teacher(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        super(name, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
    }
}
