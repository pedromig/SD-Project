package utils.lists;

import utils.people.Teacher;

public class TeacherList extends List<Teacher> {
    public TeacherList(String name) {
        super(name);
    }

    @Override
    public Class<Teacher> getType() {
        return Teacher.class;
    }

    @Override
    public String toString() {
        return " - Teacher List" + super.toString();
    }
}
