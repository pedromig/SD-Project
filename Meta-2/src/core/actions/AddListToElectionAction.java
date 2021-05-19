package core.actions;

import core.Configuration;
import utils.elections.Election;
import utils.lists.List;

import java.util.Map;

public class AddListToElectionAction extends Action implements Configuration {
    private Integer selectedList, selectedElection;
    private Map<Integer, String> listOpts, electionsOpts;

    @Override
    public String execute() throws Exception {
        try {
            if (selectedList != null) {
                Election<?> election = super.getSelectableElections().get(super.getSelectedElection());
                List<?> list = super.getSelectableLists().get(selectedList);
                super.setSelectedList(selectedList); // Not Needed
                super.getRmiConnector().associateListToElection(election.getName(), list.getName());
                return ADMIN;
            } else if (this.selectedElection == null) {
                this.electionsOpts = super.makeSelectableElections(super.getRmiConnector().getFutureElections());
            } else {
                Election<?> election = super.getSelectableElections().get(selectedElection);
                super.setSelectedElection(selectedElection);
                this.listOpts = super.makeSelectableLists(super.getRmiConnector().getListsUnassignedOfType(election.getType()));
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    @Override
    public Integer getSelectedList() {
        return this.selectedList;
    }

    @Override
    public void setSelectedList(Integer selectedList) {
        this.selectedList = selectedList;
    }

    @Override
    public Integer getSelectedElection() {
        return this.selectedElection;
    }

    @Override
    public void setSelectedElection(Integer selectedElection) {
        this.selectedElection = selectedElection;
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
