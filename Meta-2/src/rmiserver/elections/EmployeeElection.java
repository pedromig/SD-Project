package rmiserver.elections;

import rmiserver.people.Employee;

import java.util.GregorianCalendar;

/**
 * A class for an Employee Election
 */
public class EmployeeElection extends Election<Employee> {

    /**
     * Builder
     * @param name The name of an Election
     * @param description The Description of an Election
     * @param startDate The Start Date/Time of an Election
     * @param endDate The End Date/Time of an Election
     */
    public EmployeeElection(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        super(name, description, startDate, endDate);
    }

    /**
     * Function to implement in the subclasses to get the corresponding Template Type
     * @return Employee class type
     */
    @Override
    public Class<Employee> getType() {
        return Employee.class;
    }

    /**
     * To String method
     * @return this class name + the super class toString method
     */
    @Override
    public String toString() {
        return " - Employee Election" + super.toString();
    }
}
