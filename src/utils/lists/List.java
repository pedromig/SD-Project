package utils.lists;

import utils.people.Person;

import java.io.Serializable;

/**
 * An abstract template Class for a List
 * @param <T> Template for a subclass of Person
 * @Implements Serializable for Remote Method Invocation Usage
 */
public abstract class List<T extends Person> implements Serializable {
    protected String name, electionName;

    /**
     * Builder
     * @param name The name of the list
     */
    public List(String name) {
        this.name = name;
        this.electionName = null;
    }

    /**
     * Setter for the electionName attribute
     * @param electionName the new electionName value
     */
    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    /**
     * Getter for the electionName attribute
     * @return this electionName
     */
    public String getElectionName() {
        return this.electionName;
    }

    /**
     * Getter for the name attribute
     * @return this name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Function to implement in the subclasses to get the corresponding Template Type
     * @return a class type
     */
    public abstract Class<T> getType();

    /**
     * To String method
     * @return A string with the Name of the list + this list associated electionName
     */
    @Override
    public String toString() {
        return " | Name: " + this.name + "\tElection: " + this.electionName;
    }
}
