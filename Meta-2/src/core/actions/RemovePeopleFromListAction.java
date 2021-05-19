package core.actions;

import core.Configuration;
import utils.lists.List;

import java.util.Map;


public class RemovePeopleFromListAction extends Action implements Configuration {
    private Integer selectedListJsp;
    private Integer selectedPersonJsp;
    private Map<Integer, String> peopleOpts, listOpts;

    @Override
    public String execute() throws Exception {
        try {
            if (selectedPersonJsp != null) {
                List<?> list = super.getSelectableLists(SELECTABLE_LISTS_KEY_REM_PL).get(super.getSelectedList(SELECTED_LIST_KEY_REM_PL));
                super.setSelectedPerson(SELECTED_PERSON_KEY_REM_PL, selectedPersonJsp); // Not Needed
                super.getRmiConnector().associatePersonToList(null, selectedPersonJsp);
                return ADMIN;
            } else if (this.selectedListJsp == null) {
                this.listOpts = super.makeSelectableLists(SELECTABLE_LISTS_KEY_REM_PL, super.getRmiConnector().getEditableLists());
            } else {
                List<?> list = super.getSelectableLists(SELECTABLE_LISTS_KEY_REM_PL).get(selectedListJsp);
                super.setSelectedList(SELECTED_LIST_KEY_REM_PL, selectedListJsp);
                this.peopleOpts = super.makeSelectablePeople(SELECTABLE_PEOPLE_KEY_REM_PL, super.getRmiConnector().getPeopleAssignedOfType(list.getType(), list.getName()));
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public Integer getSelectedListJsp() {
        return selectedListJsp;
    }

    public void setSelectedListJsp(Integer selectedListJsp) {
        this.selectedListJsp = selectedListJsp;
    }

    public Integer getSelectedPersonJsp() {
        return selectedPersonJsp;
    }

    public void setSelectedPersonJsp(Integer selectedPersonJsp) {
        this.selectedPersonJsp = selectedPersonJsp;
    }

    public Map<Integer, String> getPeopleOpts() {
        return this.peopleOpts;
    }

    public Map<Integer, String> getListOpts() {
        return this.listOpts;
    }
}
