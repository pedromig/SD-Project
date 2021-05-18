package core.actions;

import core.Configuration;
import utils.lists.EmployeeList;
import utils.lists.List;
import utils.lists.StudentList;
import utils.lists.TeacherList;

public class CreateListAction extends Action implements Configuration {
    private List<?> list;
    private String listType, name;
    @Override
    public String execute() {
        if (!(name.equals("") || name.contains(":") || name.contains("|"))){
            try {
                switch (listType) {
                    case STUDENT:
                        list = new StudentList(name);
                        break;
                    case TEACHER:
                        list = new TeacherList(name);
                        break;
                    case EMPLOYEE:
                        list = new EmployeeList(name);
                        break;
                }
                super.getRmiConnector().createList(list);
                return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ERROR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }
}
