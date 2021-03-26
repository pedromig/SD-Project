package multicast.ui;


import multicast.VotingDesk;


public class VotingDeskConsole extends Terminal {
    private final VotingDesk app;

    public VotingDeskConsole(VotingDesk app) {
        super(app.getName());
        this.app = app;
    }

    @Override
    public void execute(String command) {
        if (command.equals("clear")) {
            this.terminal.setText("");
        } else if (command.equals("exit")) {
            this.setVisible(false);
            synchronized (app.lock) {
                app.lock.notify();
            }
            this.dispose();
        } else {
            showNewLine();
        }
        showPrompt();

    }
}
