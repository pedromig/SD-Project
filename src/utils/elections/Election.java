package utils.elections;

import utils.people.Person;

import java.io.Serializable;
import java.util.GregorianCalendar;

public abstract class Election<T extends Person> implements Serializable {
    String name, description;
    GregorianCalendar startDate, endDate;

    public Election(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public abstract void vote(T person);

    @Override
    public String toString() {
        return "[" + this.name + "]";
    }
}
