package core.actions;

import core.Configuration;
import utils.people.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PersonAuditAction extends Action implements Configuration {
    private String selectedPerson;
    private List<String> peopleAuditNames;

    @Override
    public String execute() throws Exception {
        try {
            this.setPeopleAuditNames(super.getRmiConnector().getPeople());
            if (this.selectedPerson != null){
                System.out.println("print: " + selectedPerson);
//                super.getRmiConnector().getElectorVotesInfo(selectedPerson);
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public String getSelectedPerson() {
        return this.selectedPerson;
    }

    public void setSelectedPerson(String selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    public void setPeopleAuditNames(CopyOnWriteArrayList<Person> people) {
        ArrayList<String> names = new ArrayList<>();
        for (Person p : people) {
            names.add(p.getName());
        }
        this.peopleAuditNames = names;
    }

    public List<String> getPeopleAuditNames() {
        return this.peopleAuditNames;
    }

}
