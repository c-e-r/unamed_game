package unamedGame.ui;

import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import unamedGame.input.InputEvent;
import unamedGame.input.InputObserver;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSplitPane;

public class Window {

	private boolean mapState = true;

	private JFrame frame;
	private JTextField textField;
	private JPanel panel;
	private JMenuBar menuBar;
	private JMenu mnSystem;
	private JMenuItem mntmHelp;

	private String lastCommand;

	private static Window instance = null;
	private JSplitPane splitPane;
	private JPanel panel2;
	private JScrollPane textScrollPane;
	private JTextPane textPane;
	private JSplitPane splitPane_1;
	private JScrollPane mapScrollPane;
	private JScrollPane sideScrollPane;
	private JTextPane sidePane;
	private MapPanel mapPane;
	private JTextPane playerPane;
	private JSplitPane splitPane2;

	public static Window getInstance() {
		if (instance == null) {
			instance = new Window();

		}
		return instance;
	}

	/**
	 * Initialize the window.
	 */
	private Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 831, 791);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		textField = new JTextField();
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);
		textField.addActionListener(action);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		panel2 = new JPanel();
		panel.add(panel2, BorderLayout.NORTH);

		textScrollPane = new JScrollPane();
		panel.add(textScrollPane, BorderLayout.SOUTH);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel2, textScrollPane);
		panel.add(splitPane);

		splitPane.setResizeWeight(0.0);

		panel2.setLayout(new BorderLayout(0, 0));

		mapScrollPane = new JScrollPane();
		sideScrollPane = new JScrollPane();
		splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapScrollPane, sideScrollPane);

		splitPane2.setResizeWeight(0.0);

		mapPane = new MapPanel();
		mapScrollPane.setViewportView(mapPane);
		mapPane.setFont(new Font("Courier New", Font.PLAIN, 12));

		sidePane = new JTextPane();
		sidePane.setEditable(false);
		sideScrollPane.setViewportView(sidePane);
		panel2.add(splitPane2);
		sidePane.setFont(new Font("Consolas", Font.PLAIN, 12));

		textPane = new JTextPane();
		textPane.setEditable(false);
		textScrollPane.setViewportView(textPane);
		panel.add(splitPane, BorderLayout.CENTER);

		/*
		 * A mouse listener to put focus on textField when textPane is clicked
		 */
		textPane.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textField.requestFocusInWindow();

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		/*
		 * Make textPane scroll to the bottom when new text is added
		 */
		DefaultCaret caret = (DefaultCaret) textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		mnSystem = new JMenu("System");
		menuBar.add(mnSystem);

		mntmHelp = new JMenuItem("Help");
		mnSystem.add(mntmHelp);

		playerPane = new JTextPane();
		playerPane.setEditable(false);

	}

	/**
	 * Swap the mapPane and playerPane
	 */
	public void swapMapPane() {
		if (mapState) {
			mapScrollPane.setViewportView(playerPane);
			mapState = false;
		} else {
			mapScrollPane.setViewportView(mapPane);
			mapState = true;
		}
	}

	/**
	 * Clears the given field
	 * @param field
	 */
	public static void clearField(JTextField field) {
		field.setText("");
	}

	/**
	 * Adds the given string to the given pane with the given color
	 * @param pane the pane to add the string to
	 * @param msg the string to add
	 * @param c the color of the string text
	 */
	public static void addToPane(JTextPane pane, String msg, Color c) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		try {
			doc.insertString(doc.getLength(), msg, aset);

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Adds the given string to the given pane
	 * @param pane the pane to add the string to
	 * @param msg the string to add
	 */
	public static void addToPane(JTextPane pane, String msg) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
		try {
			doc.insertString(doc.getLength(), msg, aset);

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Adds the given string to the given pane with the given color and add a newline character
	 * @param pane the pane to add the string to
	 * @param msg the string to add
	 * @param c the color of the string text
	 */
	public static void appendToPane(JTextPane pane, String msg, Color c) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		try {
			doc.insertString(doc.getLength(), msg + "\n", aset);

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Adds the given string to the given pane with a background of the given color with a newline character
	 * @param pane the pane to add the string to
	 * @param msg the string to add
	 * @param c the color of the string background
	 */
	public static void appendToPaneBackground(JTextPane pane, String msg, Color c) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, c);
		try {
			doc.insertString(doc.getLength(), msg + "\n", aset);

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Adds the given string to the given pane with a newline character
	 * @param pane the pane to add the string to
	 * @param msg the string to add
	 */
	public static void appendToPane(JTextPane pane, String msg) {
		StyledDocument doc = pane.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
		try {
			doc.insertString(doc.getLength(), msg + "\n", aset);

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Clear the the given pane orf all text.
	 * @param pane the pane to clear
	 */
	public static void clearPane(JTextPane pane) {
		StyledDocument doc = pane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the mapPane
	 * @return the mapPane
	 */
	public JPanel getMapPane() {
		return mapPane;
	}

	/**
	 * Returns the sidePane
	 * @return the sidePane
	 */
	public JTextPane getSidePane() {
		return sidePane;
	}

	/**
	 * Returns the textPane
	 * @return the textPane
	 */
	public JTextPane getTextPane() {
		return textPane;
	}

	/**
	 * Returns the playerPane
	 * @return the playerPane
	 */
	public JTextPane getPlayerPane() {
		return playerPane;
	}

	/**
	 * Return the frame
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Return the textField
	 * @return the textField
	 */
	public JTextField getTextField() {
		return textField;
	}

	private EventListenerList listenerList = new EventListenerList();

	Action action = new AbstractAction() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			fireInputChanged(textField.getText());
			textField.selectAll();

		}
	};

	public void addInputObsever(InputObserver observer) {
		listenerList.add(InputObserver.class, observer);
	}

	public void removeInputObsever(InputObserver observer) {
		listenerList.remove(InputObserver.class, observer);
	}

	protected void fireInputChanged(String text) {

		InputObserver[] listeners = listenerList.getListeners(InputObserver.class);
		if (listeners.length > 0) {

			InputEvent evt = new InputEvent(this, text);
			for (InputObserver observer : listeners) {
				observer.inputChanged(evt);
			}

		}

	}

}
