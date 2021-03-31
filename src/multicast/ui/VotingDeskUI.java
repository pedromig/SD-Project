package multicast.ui;
import multicast.VotingDesk;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class VotingDeskUI extends Terminal {
    private final VotingDesk app;

    public VotingDeskUI(VotingDesk app) {
        super(app.getName());
        this.app = app;

    }

    @Override
    public void execute(String command) {
        System.out.println("Command: " + command);
        if (command.equals("clear")) {
            terminal.setText("");
        } else if (command.equals("exit")) {
            synchronized (this.app) {
                this.app.notify();
            }
            this.dispose();
        } else {
            this.app.enqueueVoter(command);
            showNewLine();
        }
    }

    public void addWindowCloseListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg) {
                synchronized (app) {
                    setVisible(false);
                    dispose();
                    app.notify();
                }
            }
        });
    }

    @Override
    public void startText() {


    }
}
