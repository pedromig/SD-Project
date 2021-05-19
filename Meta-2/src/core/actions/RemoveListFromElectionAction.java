package core.actions;

import core.Configuration;
import utils.elections.Election;
import utils.lists.List;

import java.util.Map;

public class RemoveListFromElectionAction extends Action implements Configuration {
    private Integer selectedListJsp, selectedElectionJsp;
    private Map<Integer, String> listOpts, electionsOpts;

    @Override
    public String execute() throws Exception {
        try {
            if (selectedListJsp != null) {
                List<?> list = super.getSelectableLists(SELECTABLE_LISTS_KEY_REM_LE).get(selectedListJsp);
                super.setSelectedList(SELECTED_LIST_KEY_REM_LE, selectedListJsp); // Not Needed
                super.getRmiConnector().associateListToElection(null, list.getName());
                return ADMIN;
            } else if (this.selectedElectionJsp == null) {
                this.electionsOpts = super.makeSelectableElections(SELECTABLE_ELECTIONS_KEY_REM_LE, super.getRmiConnector().getFutureElections());
            } else {
                Election<?> election = super.getSelectableElections(SELECTABLE_ELECTIONS_KEY_REM_LE).get(selectedElectionJsp);
                super.setSelectedElection(SELECTED_ELECTION_KEY_REM_LE, selectedElectionJsp);
                this.listOpts = super.makeSelectableLists(SELECTABLE_LISTS_KEY_REM_LE, super.getRmiConnector().getListsAssignedOfType(election.getType(), election.getName()));
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
