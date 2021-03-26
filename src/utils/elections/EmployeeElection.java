package utils.elections;

import utils.people.Employee;

import java.text.SimpleDateFormat;

public class EmployeeElection extends Election<Employee> {

    public EmployeeElection(String name, String description, SimpleDateFormat startDate, SimpleDateFormat endDate) {
        super(name, description, startDate, endDate);
    }

    @Override
    public void vote(Employee p) {

    }
}
