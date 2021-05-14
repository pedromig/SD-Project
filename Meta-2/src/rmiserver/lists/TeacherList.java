package rmiserver.lists;

import rmiserver.people.Teacher;

/**
 * A class for a Teacher List
 */
public class TeacherList extends List<Teacher> {

    /**
     * Builder
     * @param name The name of the list
     */
    public TeacherList(String name) {
        super(name);
    }

    /**
     * The getType implementation of the super Class
     * @return Teacher class type
     */
    @Override
    public Class<Teacher> getType() {
        return Teacher.class;
    }

    /**
     * To String method
     * @return A string with this class name + the super class toString method
     */
    @Override
    public String toString() {
        return " - Teacher List" + super.toString();
    }
}
