package core.actions;

import core.Configuration;
import utils.elections.Election;
import utils.lists.List;

import java.util.Map;

public class AddListToElectionAction extends Action implements Configuration {
    private Integer selectedListJsp, selectedElectionJsp;
    private Map<Integer, String> listOpts, electionsOpts;

    @Override
    public String execute() throws Exception {
        try {
            if (selectedListJsp != null) {
                Election<?> election = super.getSelectableElections(SELECTABLE_ELECTIONS_KEY_ADD_LE).get(super.getSelectedElection(SELECTED_ELECTION_KEY_ADD_LE));
                List<?> list = super.getSelectableLists(SELECTABLE_LISTS_KEY_ADD_LE).get(selectedListJsp);
                super.setSelectedList(SELECTED_LIST_KEY_ADD_LE, selectedListJsp); // Not Needed
                super.getRmiConnector().associateListToElection(election.getName(), list.getName());
                return ADMIN;
            } else if (this.selectedElectionJsp == null) {
                this.electionsOpts = super.makeSelectableElections(SELECTABLE_ELECTIONS_KEY_ADD_LE, super.getRmiConnector().getFutureElections());
            } else {
                Election<?> election = super.getSelectableElections(SELECTABLE_ELECTIONS_KEY_ADD_LE).get(selectedElectionJsp);
                super.setSelectedElection(SELECTED_ELECTION_KEY_ADD_LE, selectedElectionJsp);
                this.listOpts = super.makeSelectableLists(SELECTABLE_LISTS_KEY_ADD_LE, super.getRmiConnector().getListsUnassignedOfType(election.getType()));
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
