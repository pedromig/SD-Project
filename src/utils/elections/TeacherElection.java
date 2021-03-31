package utils.elections;

import utils.people.Teacher;

import java.util.GregorianCalendar;


public class TeacherElection extends Election<Teacher> {
    public TeacherElection(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        super(name, description, startDate, endDate);
    }

    @Override
    public Class<Teacher> getType() {
        return Teacher.class;
    }

    @Override
    public String toString() {
        return " - Teacher Election" + super.toString();
    }
}
