package core.actions;

import core.Configuration;
import utils.elections.Election;

import java.util.List;

public class RemoveDepartmentAction extends Action implements Configuration {
    private Integer selectedElectionJsp;
    private String selectedDepartmentName;
    private List<String> departments;

    @Override
    public String execute() throws Exception {
        System.out.println("teste");
        try {
            this.selectedElectionJsp = super.getSelectedElection(SELECTED_ELECTION_EDIT);
            if (this.selectedElectionJsp != null) {
                Election<?> election = super.getSelectableElections(SELECTABLE_ELECTIONS_EDIT).get(this.selectedElectionJsp);
                election = super.getRmiConnector().getElection(election.getName());
                if (selectedDepartmentName != null) {
                    super.getRmiConnector().removeDepartment(election.getName(), selectedDepartmentName);
                    return ADMIN;
                }
                this.departments = election.getDepartments();
                System.out.println("correu1");
                return SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("correu2");
        return ERROR;
    }

    public Integer getSelectedElectionJsp() {
        return selectedElectionJsp;
    }

    public void setSelectedElectionJsp(Integer selectedElectionJsp) {
        this.selectedElectionJsp = selectedElectionJsp;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public String getSelectedDepartmentName() {
        return selectedDepartmentName;
    }

    public void setSelectedDepartmentName(String selectedDepartmentName) {
        this.selectedDepartmentName = selectedDepartmentName;
    }

}
