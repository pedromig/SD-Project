package core.actions;

import core.Configuration;
import utils.elections.Election;
import utils.elections.EmployeeElection;
import utils.elections.StudentElection;
import utils.elections.TeacherElection;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class CreateElectionAction extends Action implements Configuration {
    private Election<?> election;
    private String electionType, name, description, startDate, endDate;
    @Override
    public String execute() {
        if (!(  name.equals("")        || name.contains(":")        || name.contains("|")        ||
                description.equals("") || description.contains(":") || description.contains("|") ||
                startDate.equals("")   || endDate.equals(""))
        ) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(STRUTS_DATE_FORMAT + " " + STRUTS_TIME_FORMAT);
                sdf.setLenient(false);
                sdf.parse(startDate);
                GregorianCalendar startGreg = (GregorianCalendar) sdf.getCalendar();

                sdf = new SimpleDateFormat(STRUTS_DATE_FORMAT + " " + STRUTS_TIME_FORMAT);
                sdf.setLenient(false);
                sdf.parse(endDate);
                GregorianCalendar endGreg = (GregorianCalendar) sdf.getCalendar();
                /* Dates Check */
                if (startGreg.getTimeInMillis() < new GregorianCalendar().getTimeInMillis() || startGreg.getTimeInMillis() >= endGreg.getTimeInMillis()) {
                    return ERROR;
                }
                switch (electionType) {
                    case STUDENT:
                        election = new StudentElection(name, description, startGreg, endGreg);
                        break;
                    case TEACHER:
                        election = new TeacherElection(name, description, startGreg, endGreg);
                        break;
                    case EMPLOYEE:
                        election = new EmployeeElection(name, description, startGreg, endGreg);
                        break;
                }
                super.getRmiConnector().createElection(election);
                return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ERROR;
    }

    public void setElectionType(String electionType) {
        this.electionType = electionType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate.replace("T", " ");
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate.replace("T", " ");
    }
}
