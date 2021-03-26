package utils.elections;

import utils.people.Person;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public abstract class Election<T extends Person> implements Serializable {
    String name, description;
    SimpleDateFormat startDate, endDate;

    public Election(String name, String description, SimpleDateFormat startDate, SimpleDateFormat endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public abstract void vote(T p);

}
