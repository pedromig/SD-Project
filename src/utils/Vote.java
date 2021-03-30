package utils;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class Vote implements Serializable {
    private int personID, votingDeskID;
    private String electionName, votedListName;
    private GregorianCalendar moment;

    public Vote(int personID, String electionName, String votedListName, int votingDeskID){
        this.personID = personID;
        this.electionName = electionName;
        this.votedListName = votedListName;
        this.votingDeskID = votingDeskID;
        this.moment = new GregorianCalendar();
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

    public GregorianCalendar getMoment() {
        return moment;
    }

    public int getVotingDeskID() {
        return votingDeskID;
    }
}
