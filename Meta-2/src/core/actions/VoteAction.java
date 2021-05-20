package core.actions;

import core.Configuration;
import utils.Vote;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Person;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoteAction extends Action implements Configuration {
    private Integer selectedListJsp, selectedElectionJsp;
    private Map<Integer, String> listOpts, electionsOpts;

    @Override
    public String execute() throws Exception {
        try {
            if (selectedListJsp != null) {
                Election<?> election = super.getSelectableElections(SELECTABLE_VOTING_ELECTIONS_KEY).get(selectedElectionJsp);
                List<?> list = super.getSelectableLists(SELECTRABLE_VOTING_LISTS_KEY).get(selectedListJsp);
                Vote vote = new Vote(Integer.parseInt(super.getUsername()),election.getName(),list.getName(),"online");
                super.getRmiConnector().vote(vote);
                return USER;
            } else if (this.selectedElectionJsp == null) {
                Person user = super.getRmiConnector().getPerson(Integer.parseInt(super.getUsername()));
                CopyOnWriteArrayList<Election<?>> elections = super.getRmiConnector().getRunningElectionsByDepartment("online");
                elections.removeIf(election -> !election.getType().equals(user.getType()));
                elections.removeIf(election -> election.getRestrictions().size() != 0 &&
                        (!election.getRestrictions().contains(user.getDepartment()) ||
                                !election.getRestrictions().contains("online")));
                for (Election<?> e : elections) {
                    if (super.getRmiConnector().hasVoted(e.getName(), Integer.parseInt(super.getUsername()))){
                        elections.remove(e);
                    }
                }
                this.electionsOpts = super.makeSelectableElections(SELECTABLE_VOTING_ELECTIONS_KEY, elections) ;
            } else {
                Person user = super.getRmiConnector().getPerson(Integer.parseInt(super.getUsername()));
                Election<?> election = super.getSelectableElections(SELECTABLE_VOTING_ELECTIONS_KEY).get(selectedElectionJsp);
                super.setSelectedElection(SELECTED_VOTING_ELECTION_KEY, selectedElectionJsp);
                this.listOpts = super.makeSelectableLists(SELECTRABLE_VOTING_LISTS_KEY,super.getRmiConnector().getListsAssignedOfType(user.getType(), election.getName()));
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public Integer getSelectedListJsp() {
        return this.selectedListJsp;
    }

    public void setSelectedListJsp(Integer selectedListJsp) {
        this.selectedListJsp = selectedListJsp;
    }

    public Integer getSelectedElectionJsp() {
        return this.selectedElectionJsp;
    }

    public void setSelectedElectionJsp(Integer selectedElectionJsp) {
        this.selectedElectionJsp = selectedElectionJsp;
    }

    public Map<Integer, String> getListOpts() {
        return this.listOpts;
    }

    public void setListOpts(Map<Integer, String> listOpts) {
        this.listOpts = listOpts;
    }

    public Map<Integer, String> getElectionsOpts() {
        return this.electionsOpts;
    }

    public void setElectionsOpts(Map<Integer, String> electionsOpts) {
        this.electionsOpts = electionsOpts;
    }
}
