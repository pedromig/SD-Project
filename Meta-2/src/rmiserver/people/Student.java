package rmiserver.people;

import java.util.GregorianCalendar;

/**
 * A class for a Student
 */
public class Student extends Person {

    /**
     * Builder
     * @param name The name of a person
     * @param password The access code of a person
     * @param address The address of a person
     * @param faculty The Faculty of a Person
     * @param department The Department of a Person
     * @param phoneNumber The Phone Number of a Person
     * @param identityCardNumber the ID card number of a Person
     * @param identityCardExpiryDate the expiry date of the ID card of a Person
     */
    public Student(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        super(name, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
    }

    /**
     * The getType implementation of the super Class
     * @return Student class type
     */
    @Override
    public Class<Student> getType() {
        return Student.class;
    }

    /**
     * To String method
     * @return String with this class type + the super toString method
     */
    @Override
    public String toString() {
        return " - Student " + super.toString();
    }
}
