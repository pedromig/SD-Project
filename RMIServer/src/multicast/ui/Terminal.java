package multicast.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * A simple abstract GUI utility class that is used to display a console like environment in the multicast server. This
 * console allows members of the table to enqueue new users so they can go vote.
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 */
public abstract class Terminal extends JFrame {

	/**
	 * @implNote The basic graphical components of this UI class
	 */
	protected final JTextArea terminal;
	protected final String prompt;


	/**
	 * The default constructor for an instance of a class that extends this one since this one being abstract cannont
	 * be instantiated directly,
	 *
	 * @param title The title of the terminal window and prompt of the terminal
	 */
	public Terminal(String title) {

		// General Setup
		this.prompt = title + ":~$ ";

		// multicast.ui.Terminal Window Setup
		this.setTitle(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(700, 700);
		this.setVisible(true);

		// Scroll Pane Setup
		JScrollPane scrollPane = new JScrollPane();
		this.getContentPane().add(scrollPane);

		// Text multicast.ui.Terminal Setup
		this.terminal = new JTextArea();
		this.terminal.setBackground(new Color(35, 35, 36));

		this.terminal.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		this.terminal.addKeyListener(new KeyListener());

		this.terminal.setForeground(Color.WHITE);
		this.terminal.setCaretColor(Color.WHITE);

		this.disableArrowKeys(terminal.getInputMap());
		scrollPane.setViewportView(this.terminal);
	}

	/**
	 * The method that can be implemented by the class that extends this one and executes the behaviour associated
	 * with a command that was passed to this terminal
	 *
	 * @param command The command that is to be executed by this class
	 */
	public abstract void execute(String command);

	/**
	 * This method is used to disable the arrow keys default behavior in our console like environment since it can
	 * visual glitches
	 *
	 * @param inputMap The default input map used by this GUI.
	 */
	private void disableArrowKeys(InputMap inputMap) {
		String[] keys = {"UP", "DOWN", "LEFT", "RIGHT", "HOME"};
		for (String key : keys)
			inputMap.put(KeyStroke.getKeyStroke(key), "none");
	}

	/**
	 * This method is ran once the terminal instance has been started opening the GUI and setting it visible.
	 */
	public void open() {
		showPrompt();
		this.terminal.setCaretPosition(terminal.getCaretPosition() + this.prompt.length());
	}

	/**
	 * This method is used to display the prompt characters
	 */
	public void showPrompt() {
		terminal.setText(terminal.getText() + prompt);
	}

	/**
	 * This method is used to display a new line after a give command was inserted
	 */
	public void showNewLine() {
		terminal.setText(terminal.getText() + System.lineSeparator());
	}

	private class KeyListener extends KeyAdapter {
		private final KeyStroke BACK_SPACE_KEY_BIND = KeyStroke.getKeyStroke("BACK_SPACE");
		private final String DEFAULT_BACKSPACE_KEY_BIND = (String) terminal.getInputMap().get(BACK_SPACE_KEY_BIND);

		private boolean isBackspaceDisabled;
		private int initPosition = prompt.length();

		/**
		 * Method that implements the action listener to be call when a given key is pressed.
		 * @param event The event associated with a key press
		 */
		@Override
		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();

			if (keyCode == KeyEvent.VK_ENTER) {
				terminal.setEnabled(false);
				String command = getCommand();
				execute(command);
				showPrompt();
				terminal.setEnabled(true);
			}

			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				int cursorPosition = terminal.getCaretPosition();

				if (cursorPosition == initPosition && !isBackspaceDisabled) {
					isBackspaceDisabled = true;
					terminal.getInputMap().put(BACK_SPACE_KEY_BIND, "none");
				} else if (cursorPosition > initPosition && isBackspaceDisabled) {
					isBackspaceDisabled = false;
					terminal.getInputMap().put(BACK_SPACE_KEY_BIND, DEFAULT_BACKSPACE_KEY_BIND);
				}
			}
		}

		/**
		 * Method that implements the action listener to be call when a given key is released.
		 * @param event The event associated with a key released
		 */
		@Override
		public void keyReleased(KeyEvent event) {
			int keyCode = event.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER) {
				terminal.setCaretPosition(terminal.getCaretPosition() - 1);
				initPosition = terminal.getCaretPosition();
			}
		}

		/**
		 * Method that attempts to read a command inserted by a user in the console
		 * @return The string representative of the command that was read.
		 */
		private String getCommand() {
			String terminalText = terminal.getText();
			terminal.setText(terminalText.trim());

			int index = terminalText.lastIndexOf(prompt);
			boolean empty = index < 0 || index >= terminalText.length();
			return empty ? "" : terminalText.substring(index + prompt.length()).strip().trim();
		}
	}
}