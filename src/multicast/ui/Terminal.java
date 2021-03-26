package multicast.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public abstract class Terminal extends JFrame {

    private static final long serialVersionUID = 170291899252626982L;

    protected final JTextArea terminal = new JTextArea();
    protected final String prompt;

    public Terminal(String title) {

        // General Setup
        this.prompt = title + ":~$";

        // Terminal Window Setup
        this.setTitle(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Scroll Pane Setup
        JScrollPane scrollPane = new JScrollPane();
        this.getContentPane().add(scrollPane);

        // Text Terminal Setup
        this.terminal.setBackground(new Color(35, 35, 36));

        this.terminal.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        this.terminal.addKeyListener(new KeyListener(prompt.length() + 1));
        this.terminal.setForeground(Color.WHITE);
        this.terminal.setCaretColor(Color.WHITE);
        this.disableArrowKeys(terminal.getInputMap());
        scrollPane.setViewportView(this.terminal);
    }

    public abstract void execute(String command);

    private void disableArrowKeys(InputMap inputMap) {
        String[] keyNames = {"UP", "DOWN", "LEFT", "RIGHT", "HOME"};
        for (String key : keyNames)
            inputMap.put(KeyStroke.getKeyStroke(key), "none");
    }

    public void open() {
        this.setSize(700, 700);
        this.setVisible(true);
        showPrompt();
    }

    protected void showPrompt() {
        terminal.setText(terminal.getText() + prompt + " ");
    }

    protected void showNewLine() {
        terminal.setText(terminal.getText() + System.lineSeparator());
    }

    private class KeyListener extends KeyAdapter {
        private boolean isKeyboardDisabled;
        private int initCursorPosition;

        public KeyListener(int init) {
            this.initCursorPosition = init;
        }

        private String backspace(InputMap inputMap) {
            return (String) inputMap.get(KeyStroke.getKeyStroke("BACK_SPACE"));
        }

        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();

            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                int cursorPosition = terminal.getCaretPosition();
                System.out.println(cursorPosition + " " + initCursorPosition) ;
                if (cursorPosition == this.initCursorPosition && !isKeyboardDisabled) {
                    isKeyboardDisabled = true;
                    terminal.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
                } else if (cursorPosition > this.initCursorPosition && isKeyboardDisabled) {
                    isKeyboardDisabled = false;
                    terminal.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), backspace(terminal.getInputMap()));
                }
            }

            if (keyCode == KeyEvent.VK_ENTER) {
                terminal.setEnabled(false);

                String command = getCommand();
                System.err.println("Command: " + command);
                execute(command);

                terminal.setEnabled(true);
            }
        }

        public void keyReleased(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {
                terminal.setCaretPosition(terminal.getCaretPosition() - 1);
                this.initCursorPosition = terminal.getCaretPosition();
            }
        }

        private String getCommand() {
            String terminalText = terminal.getText().trim().strip();
            int index = terminalText.lastIndexOf(prompt);

            boolean empty = index < 0 || index >= terminalText.length();

            terminal.setText(terminalText);
            return empty ? "" : terminalText.substring(index + prompt.length()).strip().trim();
        }
    }
}