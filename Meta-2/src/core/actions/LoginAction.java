package core.actions;

public class LoginAction extends Action {
    private String username, password;

    @Override
    public String execute() {
        try {
            int idCardNumber = Integer.parseInt(this.username); // Note: The form input Username is the String(idcardNumber)

            /* Admin Login*/
            if (this.username.equals(ADMIN_USERNAME) && this.password.equals(ADMIN_PASSWORD))
                return ADMIN;

            /* User Login*/
            if (this.getRmiConnector().checkLogin(idCardNumber, this.password))
                return LOGIN;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
