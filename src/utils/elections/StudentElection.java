package utils.elections;

import utils.people.Student;

import java.text.SimpleDateFormat;

public class StudentElection extends Election<Student> {


    public StudentElection(String name, String description, SimpleDateFormat startDate, SimpleDateFormat endDate) {
        super(name, description, startDate, endDate);
    }


    public void vote(Student p) {

    }
}
