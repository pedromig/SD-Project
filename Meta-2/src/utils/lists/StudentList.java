package utils.lists;

import utils.people.Student;

/**
 * A class for a Student List
 */
public class StudentList extends List<Student> {

    /**
     * Builder
     * @param name The name of the list
     */
    public StudentList(String name) {
        super(name);
    }

    /**
     * The getType implementation of the super Class
     * @return Student class type
     */
    @Override
    public Class<Student> getType() {
        return Student.class;
    }

    /**
     * To String method
     * @return A string with this class name + the super class toString method
     */
    @Override
    public String toString() {
        return " - Student List" + super.toString();
    }
}
