package core.actions;

import core.Configuration;

import java.util.Map;

public class EditElectionMenuAction extends Action implements Configuration {
    private Map<Integer, String> electionsOpts;
    private Integer selectedElectionJsp;


    @Override
    public String execute() throws Exception {
        try {
            if (selectedElectionJsp == null) {
                this.electionsOpts = super.makeSelectableElections(SELECTABLE_ELECTIONS_EDIT, super.getRmiConnector().getFutureElections());
            } else {
                super.setSelectedList(SELECTED_ELECTION_EDIT, selectedElectionJsp);
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public Map<Integer, String> getElectionsOpts() {
        return electionsOpts;
    }

    public void setElectionsOpts(Map<Integer, String> electionsOpts) {
        this.electionsOpts = electionsOpts;
    }

    public Integer getSelectedElectionJsp() {
        return selectedElectionJsp;
    }

    public void setSelectedElectionJsp(Integer selectedElectionJsp) {
        this.selectedElectionJsp = selectedElectionJsp;
    }
}
