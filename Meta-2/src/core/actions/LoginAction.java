package core.actions;

public class LoginAction extends Action {
    private String username, password;

    @Override
    public String execute() {
        try {
            /* Admin Login*/
            if (this.username.equals(ADMIN_USERNAME) && this.password.equals(ADMIN_PASSWORD)) {
                super.getRmiConnector().getServer().ping(); // If server is null || is not reachable it will throw an exception;
                super.setLogin(this.username, this.password, true);
                return ADMIN;
            }

            /* User Login*/
            int idCardNumber = Integer.parseInt(this.username); // Note: The form input Username is the String(idCardNumber)
            if (super.getRmiConnector().checkLogin(idCardNumber, this.password)) {
                super.setLogin(this.username, this.password, false);
                return LOGIN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.clearLogin();
        return ERROR;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
