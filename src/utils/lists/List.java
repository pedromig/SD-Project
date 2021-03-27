package utils.lists;

import utils.people.Person;

import java.io.Serializable;

public abstract class List<T extends Person> implements Serializable {
    protected String name, electionName;

    public List(String name) {
        this.name = name;
        this.electionName = null;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getElectionName() {
        return this.electionName;
    }

    public String getName() {
        return this.name;
    }

    public abstract Class<T> getType();

    @Override
    public String toString() {
        return " | Name: " + this.name + "\tElection: " + this.electionName;
    }
}
