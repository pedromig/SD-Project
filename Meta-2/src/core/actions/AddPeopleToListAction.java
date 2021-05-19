package core.actions;

import core.Configuration;
import utils.lists.List;
import utils.people.Person;

import java.util.Map;


public class AddPeopleToListAction extends Action implements Configuration {
    private Integer selectedList;
    private Integer selectedPerson;
    private Map<Integer, String> peopleOpts, listOpts;


    @Override
    public String execute() throws Exception {
        try {
            if (selectedPerson != null) {
                List<?> list = super.getSelectableLists().get(super.getSelectedList());
                super.setSelectedPerson(selectedPerson); // Not Needed
                super.getRmiConnector().associatePersonToList(list.getName(), selectedPerson);
                return ADMIN;
            } else if (this.selectedList == null) {
                this.listOpts = super.makeSelectableLists(super.getRmiConnector().getEditableLists());
            } else {
                List<?> list = super.getSelectableLists().get(selectedList);
                super.setSelectedList(selectedList);
                this.peopleOpts = super.makeSelectablePeople(super.getRmiConnector().getPeopleUnassignedOfType(list.getType()));
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public Integer getSelectedList() {
        return this.selectedList;
    }

    public void setSelectedList(Integer selectedList) {
        this.selectedList = selectedList;
    }

    public Integer getSelectedPerson() {
        return this.selectedPerson;
    }

    public void setSelectedPerson(Integer selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    public Map<Integer, String> getPeopleOpts() {
        return this.peopleOpts;
    }

    public Map<Integer, String> getListOpts() {
        return this.listOpts;
    }
}
