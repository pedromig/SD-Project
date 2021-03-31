package utils.people;

import java.util.GregorianCalendar;

public class Employee extends Person {

    public Employee(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        super(name, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
    }

    @Override
    public Class<Employee> getType() {
        return Employee.class;
    }

    @Override
    public String toString() {
        return " - Employee" + super.toString();
    }
}
