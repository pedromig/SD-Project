package utils.people;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * An abstract Class for a Person (Voter)
 * @Implements Serializable for Remote Method Invocation Usage
 */
public abstract class Person implements Serializable {
    private int phoneNumber, identityCardNumber;
    private String list;
    private String name, password, address, faculty, department;
    private GregorianCalendar identityCardExpiryDate;

    /**
     * Builder
     * @param name The name of a person
     * @param password The access code of a person
     * @param address The address of a person
     * @param faculty The Faculty of a Person
     * @param department The Department of a Person
     * @param phoneNumber The Phone Number of a Person
     * @param identityCardNumber the ID card number of a Person
     * @param identityCardExpiryDate the expiry date of the ID card of a Person
     */
    public Person(String name, String password, String address, String faculty, String department, int phoneNumber, int identityCardNumber, GregorianCalendar identityCardExpiryDate) {
        this.phoneNumber = phoneNumber;
        this.identityCardNumber = identityCardNumber;
        this.name = name;
        this.password = password;
        this.address = address;
        this.faculty = faculty;
        this.department = department;
        this.identityCardExpiryDate = identityCardExpiryDate;
        this.list = null;
    }

    /**
     * Function to implement in the subclasses to get the corresponding type
     * @return a class type
     */
    public abstract Class<?> getType();

    /**
     * Setter for the list attribute
     * @param list The new list value
     */
    public void setList(String list) {
        this.list = list;
    }

    /**
     * Getter for the list attribute
     * @return this list
     */
    public String getList() {
        return this.list;
    }

    /**
     * Getter for the name attribute
     * @return this name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the name attribute
     * @return this password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter for the identityCardNumber attribute
     * @return this identityCardNumber
     */
    public int getIdentityCardNumber() {
        return this.identityCardNumber;
    }

    /**
     * To String method
     * @return String with this name, identityCardNumber and list attributes
     */
    @Override
    public String toString() {
        return  "    Name: "    + this.name                 +
                "\tID: "        + this.identityCardNumber   +
                "\tList: "      + this.list;
    }
}
