package rmiserver.lists;

import rmiserver.people.Employee;

/**
 * A class for an Employee List
 */
public class EmployeeList extends List<Employee> {

    /**
     * Builder
     * @param name The name of the list
     */
    public EmployeeList(String name) {
        super(name);
    }

    /**
     * The getType implementation of the super Class
     * @return Employee class type
     */
    @Override
    public Class<Employee> getType() {
        return Employee.class;
    }

    /**
     * To String method
     * @return A string with this class name + the super class toString method
     */
    @Override
    public String toString() {
        return " - Employee List" + super.toString();
    }
}
