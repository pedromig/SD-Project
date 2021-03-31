package utils.lists;

import utils.people.Student;

public class StudentList extends List<Student> {
    public StudentList(String name) {
        super(name);
    }

    @Override
    public Class<Student> getType() {
        return Student.class;
    }

    @Override
    public String toString() {
        return " - Student List" + super.toString();
    }
}
