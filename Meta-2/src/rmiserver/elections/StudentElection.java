package rmiserver.elections;

import rmiserver.people.Student;

import java.util.GregorianCalendar;

/**
 * A class for a Student Election
 */
public class StudentElection extends Election<Student> {

    /**
     * Builder
     * @param name The name of an Election
     * @param description The Description of an Election
     * @param startDate The Start Date/Time of an Election
     * @param endDate The End Date/Time of an Election
     */
    public StudentElection(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        super(name, description, startDate, endDate);
    }

    /**
     * Function to implement in the subclasses to get the corresponding Template Type
     * @return Student class type
     */
    @Override
    public Class<Student> getType() {
        return Student.class;
    }

    /**
     * To String method
     * @return this class name + the super class toString method
     */
    @Override
    public String toString() {
        return " - Student Election" + super.toString();
    }
}
