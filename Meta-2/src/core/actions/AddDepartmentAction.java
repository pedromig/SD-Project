package core.actions;

import core.Configuration;
import utils.elections.Election;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AddDepartmentAction extends Action implements Configuration {
    private Integer selectedElectionJsp;
    private String selectedDepartmentName;
    private List<String> departments;

    @Override
    public String execute() throws Exception {
        try {
            this.selectedElectionJsp = super.getSelectedElection(SELECTED_ELECTION_EDIT);
            if (this.selectedElectionJsp != null) {
                Election<?> election = super.getSelectableElections(SELECTABLE_ELECTIONS_EDIT).get(this.selectedElectionJsp);
                election = super.getRmiConnector().getElection(election.getName());
                if (selectedDepartmentName != null) {
                    super.getRmiConnector().addDepartment(election.getName(), selectedDepartmentName);
                    return ADMIN;
                }
                String[] depts = super.getRmiConnector().getDepartments();
                CopyOnWriteArrayList<String> selectableDepartments = new CopyOnWriteArrayList<>();
                for (String deptName : depts) {
                    if (!election.getDepartments().contains(deptName)) {
                        selectableDepartments.add(deptName);
                    }
                }
                this.departments = selectableDepartments;
                return SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
