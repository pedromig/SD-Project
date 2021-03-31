package multicast.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public abstract class Terminal extends JFrame {

    protected final JTextArea terminal;
    protected final String prompt;

    public Terminal(String title) {

        // General Setup
        this.prompt = title + ":~$ ";

        // Terminal Window Setup
        this.setTitle(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(700, 700);
        this.setVisible(true);

        // Scroll Pane Setup
        JScrollPane scrollPane = new JScrollPane();
        this.getContentPane().add(scrollPane);

        // Text Terminal Setup
        this.terminal = new JTextArea();
        this.terminal.setBackground(new Color(35, 35, 36));

        this.terminal.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        this.terminal.addKeyListener(new KeyListener());

        this.terminal.setForeground(Color.WHITE);
        this.terminal.setCaretColor(Color.WHITE);

        this.disableArrowKeys(terminal.getInputMap());
        scrollPane.setViewportView(this.terminal);
    }

    public abstract void execute(String command);
    public abstract void startText();

    private void disableArrowKeys(InputMap inputMap) {
        String[] keys = {"UP", "DOWN", "LEFT", "RIGHT", "HOME"};
        for (String key : keys)
            inputMap.put(KeyStroke.getKeyStroke(key), "none");
    }

    public void open() {
        showPrompt();
    }

    public void showPrompt() {
        terminal.setText(terminal.getText() + prompt);
    }

    public void showNewLine() {
        terminal.setText(terminal.getText() + System.lineSeparator());
    }

    private class KeyListener extends KeyAdapter {
        private final KeyStroke BACK_SPACE_KEY_BIND = KeyStroke.getKeyStroke("BACK_SPACE");
        private final String DEFAULT_BACKSPACE_KEY_BIND = (String) terminal.getInputMap().get(BACK_SPACE_KEY_BIND);

        private boolean isBackspaceDisabled;
        private int initPosition = prompt.length();

        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();

            if (keyCode ==  KeyEvent.VK_ENTER) {
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
                    terminal.getInputMap().put(BACK_SPACE_KEY_BIND,"none");
                } else if (cursorPosition > initPosition && isBackspaceDisabled) {
                    isBackspaceDisabled = false;
                    terminal.getInputMap().put(BACK_SPACE_KEY_BIND, DEFAULT_BACKSPACE_KEY_BIND);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (keyCode ==  KeyEvent.VK_ENTER) {
                terminal.setCaretPosition(terminal.getCaretPosition() - 1);
                initPosition = terminal.getCaretPosition();
            }
        }

        private String getCommand() {
            String terminalText = terminal.getText();
            terminal.setText(terminalText.trim());

            int index = terminalText.lastIndexOf(prompt);
            boolean empty = index < 0 || index >= terminalText.length();
            return empty ? "" : terminalText.substring(index + prompt.length()).strip().trim();
        }
    }
}