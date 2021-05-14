package rmiserver;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * A class for a Election Vote
 */
public class Vote implements Serializable {
    public static final String NULL_VOTE = "NULL";
    public static final String WHITE_VOTE = "WHITE";

    private final int personID;
    private final String votingDeskID;
    private final String electionName;
    private final String votedListName;
    private final GregorianCalendar moment;

    /**
     * Builder
     * @param personID ID of the voter
     * @param electionName name of the voted election
     * @param votedListName name of the voted list
     * @param votingDeskID name of the voting desk (department)
     */
    public Vote(int personID, String electionName, String votedListName, String votingDeskID){
        this.personID = personID;
        this.electionName = electionName;
        this.votedListName = votedListName;
        this.votingDeskID = votingDeskID;
        this.moment = new GregorianCalendar();
    }

    /**
     * Getter of election name
     * @return this electionName
     */
	public String getElectionName() {
		return electionName;
	}

    /**
     * Getter of person ID
     * @return this personID
     */
	public int getPersonID() {
        return personID;
    }

    /**
     * Getter of voted list name
     * @return this votedListName
     */
    public String getVotedListName() {
        return votedListName;
    }

    /**
     * Getter of moment
     * @return this moment
     */
    public GregorianCalendar getMoment() {
        return moment;
    }

    /**
     * Getter of voting desk ID
     * @return this votingDeskID
     */
    public String getVotingDeskID() {
        return votingDeskID;
    }
}
