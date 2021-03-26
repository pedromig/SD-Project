package utils.elections;

import utils.people.Teacher;

import java.text.SimpleDateFormat;

public class TeacherElection extends Election<Teacher>{
    public TeacherElection(String name, String description, SimpleDateFormat startDate, SimpleDateFormat endDate) {
        super(name, description, startDate, endDate);
    }

    @Override
    public void vote(Teacher p) {

    }
}
