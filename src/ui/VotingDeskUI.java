package ui;
import multicast.VotingDesk;
import ui.Terminal;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;


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

/*        String title = "Main Menu";
        String options[] = {"A", "B" ,"C"};

        int counter = 0, maxLen = title.length();
        for (String s : options) if (s.length() > maxLen)  maxLen = s.length();
        String repeat = "─".repeat(2 * maxLen + title.length());
        this.terminal.append("┌" + repeat + "┐\n");
        this.terminal.append("│" + " ".repeat(maxLen) + title + " ".repeat(maxLen) + "│\n");
        if (options.length != 0){
            this.terminal.append("├" + repeat + "┤\n");
            for (String opt : options){
                this.terminal.append("│  " + (++counter) + "- " + opt + " ".repeat(2*maxLen + title.length() - opt.length() - 4 - ((int) Math.log10(counter) + 1) ) + "│\n");
            }
            this.terminal.append("├" + repeat + "┤\n");
            this.terminal.append("|  0- Back" + " ".repeat(2*maxLen + title.length() - "Back".length() - 5) + "│\n");

        }
        this.terminal.append("└" + repeat + "┘\n");
    }*/
}
