package utils;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class Vote implements Serializable {
    public static final String NULL_VOTE = "NULL";
    public static final String WHITE_VOTE = "WHITE";

    private final int personID;
    private final String votingDeskID;
    private final String electionName;
    private final String votedListName;
    private final GregorianCalendar moment;

    public Vote(int personID, String electionName, String votedListName, String votingDeskID){
        this.personID = personID;
        this.electionName = electionName;
        this.votedListName = votedListName;
        this.votingDeskID = votingDeskID;
        this.moment = new GregorianCalendar();
    }

	public String getElectionName() {
		return electionName;
	}

	public int getPersonID() {
        return personID;
    }

    public String getVotedListName() {
        return votedListName;
    }

    public GregorianCalendar getMoment() {
        return moment;
    }

    public String getVotingDeskID() {
        return votingDeskID;
    }
}
