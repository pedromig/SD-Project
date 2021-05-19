package core.actions;

import core.Configuration;
import utils.people.Person;

import java.util.HashMap;
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
        HashMap<Integer, String> names = new HashMap<>();
        for (Person p : people) {
            names.put(p.getIdentityCardNumber(), p.getName() + " - " + p.getIdentityCardNumber());
        }
        this.peopleAuditNames = names;
    }

    public Map<Integer, String> getPeopleAuditNames() {
        return this.peopleAuditNames;
    }

}
