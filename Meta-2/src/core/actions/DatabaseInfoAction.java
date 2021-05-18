package core.actions;

import core.Configuration;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Person;

import java.util.ArrayList;

import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseInfoAction extends Action implements Configuration {

    @Override
    public String execute() {
        try {
            this.setElections(super.getRmiConnector().getAllElections());
            this.setLists(super.getRmiConnector().getLists());
            this.setPeople(super.getRmiConnector().getPeople());
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

}
