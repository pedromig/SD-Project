package utils;

import java.io.Serializable;

public class Vote implements Serializable {
    private int personID;
    private String electionName, votedListName;

    public Vote(int personID, String electionName, String votedListName){
        this.personID = personID;
        this.electionName = electionName;
        this.votedListName = votedListName;
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getVotedListName() {
        return votedListName;
    }

    public void setVotedListName(String votedListName) {
        this.votedListName = votedListName;
    }
}
