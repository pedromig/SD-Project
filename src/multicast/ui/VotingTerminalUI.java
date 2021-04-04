package multicast.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VotingTerminalUI extends JFrame {

	private JPanel topPanel;
	private JPanel electionSelectionPanel;

	private JLabel terminalTitle;
	private JLabel formTitle;

	private JComboBox<String> electionBox;
	private String[] elections;

	private JLabel chooseElection;
	private JList<ElectionList> electionsList;


	private JButton voteButton;

	public VotingTerminalUI(String name) {

		this.setTitle(name);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(700, 700);
		this.setLayout(new BorderLayout());

		setDefaultLookAndFeelDecorated(true);

		initFrameTitle(name);

		this.elections = new String[]{};
		initElectionSelectionBox(elections);

		this.electionsList = new JList<>(new ElectionList[]{});
		initElectionListOptions(electionsList);

		this.voteButton = new JButton("Vote");

		this.topPanel.add(terminalTitle);
		this.topPanel.add(formTitle);
		this.topPanel.add(electionSelectionPanel);
		this.getContentPane().add(this.topPanel, BorderLayout.NORTH);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(left.getWidth() + 50, left.getHeight()));
		this.getContentPane().add(left, BorderLayout.LINE_START);

		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(right.getWidth() + 50, right.getHeight()));
		this.getContentPane().add(right, BorderLayout.LINE_END);

		JPanel bottom = new JPanel();
		bottom.add(voteButton);
		bottom.setPreferredSize(new Dimension(bottom.getWidth(), bottom.getHeight() + 100));
		this.getContentPane().add(bottom, BorderLayout.PAGE_END);

		this.getContentPane().add(new JScrollPane(electionsList), BorderLayout.CENTER);
		this.setVisible(true);
	}

	private void initFrameTitle(String name) {

		this.topPanel = new JPanel();
		BoxLayout layout = new BoxLayout(topPanel, BoxLayout.Y_AXIS);
		topPanel.setLayout(layout);

		this.terminalTitle = new JLabel("Welcome to eVoting Terminal " + name);
		terminalTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		terminalTitle.setBorder(new EmptyBorder(2, 2, 6, 2));
		terminalTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));

		this.formTitle = new JLabel("Voting Form");
		formTitle.setBorder(new EmptyBorder(2, 2, 6, 2));
		formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		formTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));

		this.electionSelectionPanel = new JPanel(new FlowLayout());
		electionSelectionPanel.setBorder(new EmptyBorder(2, 2, 10, 2));

		this.chooseElection = new JLabel("Choose Election: ");
		chooseElection.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		electionSelectionPanel.add(chooseElection);
	}


	private void initElectionSelectionBox(String[] elections) {
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);

		this.electionBox = new JComboBox<>(elections);
		this.electionBox.setRenderer(renderer);
		this.electionBox.setPrototypeDisplayValue("-".repeat(40));
		electionSelectionPanel.add(electionBox);
	}


	private void initElectionListOptions(JList<ElectionList> list) {

		list.setCellRenderer((l, value, index, isSelected, cellHasFocus) -> {
			JCheckBox box = new JCheckBox();
			box.setSelected(value.isSelected());
			box.setText(value.toString());
			box.setFont(l.getFont());
			box.setBackground(l.getBackground());
			box.setForeground(l.getForeground());
			box.setEnabled(l.isEnabled());
			return box;
		});

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = list.locationToIndex(event.getPoint());
				if (index >= 0) {
					ElectionList item = list.getModel().getElementAt(index);
					item.setSelected(!item.isSelected());
					list.repaint(list.getCellBounds(index, index));
				}
			}
		});
	}

	private static class ElectionList {
		private final String name;
		private boolean isSelected = false;

		public ElectionList(String name) {
			this.name = name;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		@Override
		public String toString() {
			return name;
		}
	}


	public static void main(String[] args) {
		VotingTerminalUI ui = new VotingTerminalUI("VT-1");
	}
}



