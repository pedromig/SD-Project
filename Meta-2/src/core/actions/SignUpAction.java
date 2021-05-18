package core.actions;

import core.Configuration;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class SignUpAction extends Action implements Configuration {
    private Person person;
    private int phoneNumber, identityCardNumber;
    private String name, password, address, faculty, department, identityCardExpiryDate, personType;

    @Override
    public String execute() {
        //FIXME: nao esta nada clean
        if (!(  name.equals("")       || name.contains(":")       || name.contains("|")        ||
                password.equals("")   || password.contains(":")   || password.contains("|")    ||
                address.equals("")    || address.contains(":")    || address.contains("|")     ||
                faculty.equals("")    || faculty.contains(":")    || faculty.contains("|")     ||
                department.equals("") || department.contains(":") || department.contains("|")  ||
                (phoneNumber == 0) || (identityCardNumber == 0) || identityCardExpiryDate.equals(""))
        ) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(STRUTS_DATE_FORMAT);
                sdf.parse(identityCardExpiryDate);
                GregorianCalendar gregDate = (GregorianCalendar) sdf.getCalendar();
                switch (personType) {
                    case STUDENT:
                        person = new Student(name, password, address,faculty, department, phoneNumber, identityCardNumber, gregDate);
                        break;
                    case TEACHER:
                        person = new Teacher(name, password, address, faculty, department, phoneNumber, identityCardNumber, gregDate);
                        break;
                    case EMPLOYEE:
                        person = new Employee(name, password, address, faculty, department, phoneNumber, identityCardNumber, gregDate);
                        break;
                }
                super.getRmiConnector().signUp(person);
                return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ERROR;
    }

    public void setIdentityCardExpiryDate(String identityCardExpiryDate) {
        this.identityCardExpiryDate = identityCardExpiryDate;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIdentityCardNumber(int identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

}
