package utils.elections;

import utils.Vote;
import utils.people.Person;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An abstract template Class for an Election
 * @param <T> Template for a subclass of Person
 * @Implements Serializable for Remote Method Invocation Usage
 */
public abstract class Election<T extends Person> implements Serializable {
    protected String name, description;
    protected GregorianCalendar startDate, endDate;
    protected CopyOnWriteArrayList<Vote> votes;
    protected CopyOnWriteArrayList<String> departments;
    protected CopyOnWriteArrayList<String> restrictions;

    /**
     * Builder
     * @param name The name of an Election
     * @param description The Description of an Election
     * @param startDate The Start Date/Time of an Election
     * @param endDate The End Date/Time of an Election
     */
    public Election(String name, String description, GregorianCalendar startDate, GregorianCalendar endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.votes = new CopyOnWriteArrayList<>();
        this.departments = new CopyOnWriteArrayList<>();
        this.restrictions = new CopyOnWriteArrayList<>();
    }

    /**
     * Function to implement in the subclasses to get the corresponding Template Type
     * @return a class type
     */
    public abstract Class<T> getType();

    /**
     * Getter for the restrictions attribute
     * @return this restrictions
     */
    public CopyOnWriteArrayList<String> getRestrictions() {
        return this.restrictions;
    }

    /**
     * Method to add a Restriction
     * @param deptName new department restriction
     */
    public void addRestriction(String deptName) {
        this.restrictions.add(deptName);
    }

    /**
     * Method to remove a restriction
     * @param deptName restriction to remove
     */
    public void removeRestrictions(String deptName) {
        this.restrictions.remove(deptName);
    }

    /**
     * Getter for the departments attribute
     * @return this departments
     */
    public CopyOnWriteArrayList<String> getDepartments() {
        return this.departments;
    }

    /**
     * Getter fot the votes attribute
     * @return this votes
     */
    public CopyOnWriteArrayList<Vote> getVotes() {
        return this.votes;
    }

    /**
     * Setter for the description attribute
     * @param description The new description value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the startDate attribute
     * @param startDate the new startDate value
     */
    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    /**
     * Setter for the endDate attribute
     * @param endDate the new endDate value
     */
    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    /**
     * Setter for the name attribute
     * @param name the new name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the name attribute
     * @return this name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the startDate attribute
     * @return this startDate
     */
    public GregorianCalendar getStartDate() {
        return this.startDate;
    }

    /**
     * Getter for the endDate attribute
     * @return this endDate
     */
    public GregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Method to add a vote to the elections votes
     * @param v vote to be added
     */
    public void addVote(Vote v) {
        this.votes.add(v);
    }

    /**
     * To String method
     * @return A String with this name, this startDate and this endDate
     */
    @Override
    public String toString() {
        return " | [" + this.name + "] Start Date: " + this.startDate.getTime() + " End: " + this.endDate.getTime() ;
    }

}
