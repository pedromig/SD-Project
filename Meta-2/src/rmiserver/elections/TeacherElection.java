package rmiserver.elections;

import rmiserver.people.Teacher;

import java.util.GregorianCalendar;

/**
 * A class for a Teacher Election
 */
public class TeacherElection extends Election<Teacher> {

    /**
     * Builder
     * @param name The name of an Election
     * @param description The Description of an Election
     * @param startDate The Start Date/Time of an Election
     * @param endDate The End Date/Time of an Election
     */
    public TeacherElection(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        super(name, description, startDate, endDate);
    }

    /**
     * Function to implement in the subclasses to get the corresponding Template Type
     * @return Teacher class type
     */
    @Override
    public Class<Teacher> getType() {
        return Teacher.class;
    }

    /**
     * To String method
     * @return this class name + the super class toString method
     */
    @Override
    public String toString() {
        return " - Teacher Election" + super.toString();
    }
}
