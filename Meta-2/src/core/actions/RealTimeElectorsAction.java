package core.actions;

import core.Configuration;
import utils.elections.Election;

import java.util.ArrayList;
import java.util.List;

public class RealTimeElectorsAction extends Action implements Configuration {
    List<String> watchableElections;

    @Override
    public String execute() throws Exception {
        try {
            ArrayList<String> map = new ArrayList<>();
            for (Election<?> e : super.getRmiConnector().getRunningElections()) {
                map.add(e.getName());
            }
            this.watchableElections = map;
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public List<String> getWatchableElections() {
        return watchableElections;
    }

    public void setWatchableElections(List<String> watchableElections) {
        this.watchableElections = watchableElections;
    }
}
