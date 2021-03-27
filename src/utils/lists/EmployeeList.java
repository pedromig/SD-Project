package utils.lists;

import utils.people.Employee;

public class EmployeeList extends List<Employee> {

    public EmployeeList(String name) {
        super(name);
    }

    @Override
    public Class<Employee> getType() {
        return Employee.class;
    }

    @Override
    public String toString() {
        return " - Employee List" + super.toString();
    }
}
