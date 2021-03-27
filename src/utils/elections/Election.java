package utils.elections;

import utils.people.Person;

import java.io.Serializable;
import java.util.GregorianCalendar;

public abstract class Election<T extends Person> implements Serializable {
    private String name, description;
    private GregorianCalendar startDate, endDate;

    public Election(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public abstract void vote(T person);

    public abstract Class<T> getType();

    @Override
    public String toString() {
        return "[" + this.name + "]";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public GregorianCalendar getStartDate() {
        return this.startDate;
    }

    public GregorianCalendar getEndDate() {
        return this.endDate;
    }
}
