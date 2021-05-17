package core.actions;

public class RmiConnectAction extends Action {

    @Override
    public String execute() {
        if (super.getRmiConnector().getServer() != null)
            return SUCCESS;
        return ERROR;
    }

}
