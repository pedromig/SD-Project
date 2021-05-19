package core.actions;

import core.Configuration;
import utils.elections.Election;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class EditElectionAttributeAction extends Action implements Configuration {
    private String name, description, startDate, endDate;

    @Override
    public String execute() throws Exception {
        try {
            Election<?> election = super.getSelectableElections(SELECTABLE_ELECTIONS_EDIT).get(super.getSelectedElection(SELECTED_ELECTION_EDIT));
            SimpleDateFormat sdf = new SimpleDateFormat(STRUTS_DATE_FORMAT + " " + STRUTS_TIME_FORMAT);
            sdf.setLenient(false);

            if ((name != null) && !(name.equals("") || name.contains(":") || name.contains("|"))) {
                super.getRmiConnector().getServer().editElectionName(election.getName(), name);
            } else if ((description != null) && !(description.equals("") || description.contains(":") || description.contains("|"))) {
                super.getRmiConnector().getServer().editElectionDescription(election.getName(), description);
            } else if (startDate != null) {
                sdf.parse(startDate);
                GregorianCalendar date = (GregorianCalendar) sdf.getCalendar();
                super.getRmiConnector().getServer().editElectionStartDate(election.getName(), date);
            } else if (endDate != null) {
                sdf.parse(endDate);
                GregorianCalendar date = (GregorianCalendar) sdf.getCalendar();
                super.getRmiConnector().getServer().editElectionEndDate(election.getName(), date);
            } else {
                return ERROR;
            }
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate.replace("T"," ");
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate.replace("T", " ");
    }
}
