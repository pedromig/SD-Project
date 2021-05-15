package multicast.ui;

import multicast.VotingDesk;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * A simple implementation of a GUI that is used to display a console like environment in the multicast
 * server. This console allows members of the table to enqueue new users so they can go vote.
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 */
public class VotingDeskUI extends Terminal {
	private final VotingDesk app;

	/**
	 * The default constructor for this GUI
	 * @param app The backend for the {@code multicast.ui.VotingDeskUI} app
	 */
	public VotingDeskUI(VotingDesk app) {
		super("multicast.VotingDesk@" + app.getName());
		this.app = app;
	}

	/**
	 * A implementation of the command parser used by the {@code multicast.ui.VotingDeskUI}
	 *
	 * @param command The command that is to be executed by this class
	 */
	@Override
	public void execute(String command) {

		if (command.equals("clear")) {
			terminal.setText("");
		} else if (command.equals("exit")) {
			synchronized (this.app) {
				this.app.notify();
			}
			this.dispose();
		} else {
			try {
				int value = Integer.parseInt(command);
				if (value >= 0)
					this.app.enqueueVoter(command);
			} catch (NumberFormatException ignore) {
			}
			showNewLine();
		}
	}

	/**
	 * A implementation of the action that occurs when the console window is closed by the user
	 */
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
}
