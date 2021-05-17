package core.actions;

public class RmiConnectAction extends Action {

    @Override
    public String execute() {
        if (this.getRmiConnector().getServer() != null)
            return SUCCESS;
        return ERROR;
    }

}
