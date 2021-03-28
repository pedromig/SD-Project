package utils.elections;

import utils.Vote;
import utils.people.Person;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Election<T extends Person> implements Serializable {
    public static final String NO_RESTRAINT = "ANY";
    protected String name, description, faculty, department;
    protected GregorianCalendar startDate, endDate;
    protected CopyOnWriteArrayList<Vote> votes;

    public Election(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.faculty = NO_RESTRAINT;
        this.department = NO_RESTRAINT;
        votes = new CopyOnWriteArrayList<>();
    }

    public abstract Class<T> getType();


    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return this.name;
    }

    public GregorianCalendar getStartDate() {
        return this.startDate;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void addVote(Vote v) {
        this.votes.add(v);
    }

    @Override
    public String toString() {
        return " | [" + this.name + "] Start Date: " + this.startDate.getTime() + " End: " + this.endDate.getTime() ;
    }

}
