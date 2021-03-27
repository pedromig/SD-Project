package utils.elections;

import utils.people.Employee;

import java.util.GregorianCalendar;

public class EmployeeElection extends Election<Employee> {

    public EmployeeElection(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        super(name, description, startDate, endDate);
    }

    @Override
    public void vote(Employee p) {

    }

    @Override
    public String toString() {
        return " - Employee Election " + super.toString();
    }
}
