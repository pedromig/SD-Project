package core.actions;

import core.Configuration;
import utils.people.Person;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PersonAuditAction extends Action implements Configuration {
    private Integer selectedPerson;
    private Map<Integer, String> peopleAuditNames;

    @Override
    public String execute() throws Exception {
        try {
            this.setPeopleAuditNames(super.getRmiConnector().getPeople());
            if (this.selectedPerson != null){
                this.setPeopleAudit(super.getRmiConnector().getElectorVotesInfo(selectedPerson));
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public Integer getSelectedPerson() {
        return this.selectedPerson;
    }

    public void setSelectedPerson(Integer selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    public void setPeopleAuditNames(CopyOnWriteArrayList<Person> people) {
        this.peopleAuditNames = super.makeSelectablePeople(people);
    }

    public Map<Integer, String> getPeopleAuditNames() {
        return this.peopleAuditNames;
    }

}
