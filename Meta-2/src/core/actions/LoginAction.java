package core.actions;

public class LoginAction extends Action {
    private String username, password;

    @Override
    public String execute() {

        /* New Login */
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
                super.getRmiConnector().getServer().login(idCardNumber);
                return LOGIN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Already Logged User (this code block stays here in case the web user wants to login with other account. The new will have priority) */
//        if (this.getUsername() != null && this.getPassword() != null) {
//            if (this.getUsername().equals(ADMIN_USERNAME) && this.getPassword().equals(ADMIN_PASSWORD))
//                return ADMIN;
//            return LOGIN;
//        }
        try {
            int id = Integer.parseInt(this.username);
            super.getRmiConnector().getServer().logout(id);
        } catch (Exception ignore) {}

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
