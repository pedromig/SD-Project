package utils;

import utils.people.Person;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElectionList<T extends Person> implements Serializable {
    private String name;
    private CopyOnWriteArrayList<T> people;

    public ElectionList(String name) {
        this.name = name;
        this.people = new CopyOnWriteArrayList<>();
    }

    public void addPerson(T person){
        this.people.add(person);
        person.setInList(true);
    }

    public void removePerson(T person){
        this.people.remove(person);
        person.setInList(false);
    }

    public String getName() {
        return name;
    }

}
