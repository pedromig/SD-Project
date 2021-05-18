package core.actions;

import core.Configuration;

public class EndedElectionsLogAction extends Action implements Configuration {

    @Override
    public String execute() throws Exception {
        try {
            super.setEndedElectionsLog(super.getRmiConnector().getEndedElections());
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }
}
