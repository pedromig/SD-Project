package utils.elections;

import utils.people.Student;

import java.util.GregorianCalendar;

public class StudentElection extends Election<Student> {


    public StudentElection(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        super(name, description, startDate, endDate);
    }

    @Override
    public void vote(Student p) {

    }

    @Override
    public String toString() {
        return " - Student Election " + super.toString();
    }
}
