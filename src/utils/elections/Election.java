package utils.elections;

import utils.people.Person;

import java.io.Serializable;
import java.util.GregorianCalendar;

public abstract class Election<T extends Person> implements Serializable {
    protected String name, description;
    protected GregorianCalendar startDate, endDate;

    public Election(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public abstract Class<T> getType();


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


    public GregorianCalendar getStartDate() {
        return this.startDate;
    }

    @Override
    public String toString() {
        return " | [" + this.name + "]";
    }

}
