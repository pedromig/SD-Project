package utils.people;

import java.util.GregorianCalendar;

public class Student extends Person {

    public Student(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        super(name, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
    }

    @Override
    public String toString() {
        return " - Student " + super.toString();
    }
}
