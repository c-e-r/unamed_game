package unamedgame.ui;

import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedgame.Game;
import unamedgame.entities.Player;
import unamedgame.input.InputEvent;
import unamedgame.input.InputObserver;

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

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private boolean mapState = true;

    private JFrame frame;
    private JTextField textField;
    private JPanel panel;
    private JMenuBar menuBar;
    private JMenu mnSystem;
    private JMenuItem mntmHelp;

    private static Window instance = null;
    private JSplitPane splitPane;
    private JPanel panel2;
    private JScrollPane textScrollPane;
    private JTextPane textPane;
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
        frame.setBounds(200, 50, 1000, 950);
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

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel2,
                textScrollPane);
        panel.add(splitPane);

        splitPane.setResizeWeight(0.0);

        panel2.setLayout(new BorderLayout(0, 0));

        mapScrollPane = new JScrollPane();
        sideScrollPane = new JScrollPane();
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapScrollPane,
                sideScrollPane);

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
        textPane.setFont(new Font("Courier New", Font.PLAIN, 12));

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

        setUpKeyBindings();
    }

    /**
     * Swap the mapPane and playerPane
     */
    @Deprecated
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
     * Swaps to the player pane if not already on the player pane
     */
    public void swapToPlayerPane() {
        if (mapState) {
            mapScrollPane.setViewportView(playerPane);
            mapState = false;
        }
    }

    /**
     * Swaps to the map pane if not already on the map pane
     */
    public void swapToMapPane() {
        if (!mapState) {
            mapScrollPane.setViewportView(mapPane);
            mapState = true;
        }
    }

    /**
     * Clears the text field
     * 
     */
    public static void clearTextField() {
        instance.textField.setText("");
    }

    public static void appendText(String msg) {
        appendText(msg, Color.BLACK);
    }

    public static void appendText(String msg, Color c) {
        StyledDocument doc = instance.textPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);

    }

    public static void appendTextBackground(String msg, Color c) {
        StyledDocument doc = instance.textPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Background, c);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendSide(String msg) {
        appendSide(msg, Color.BLACK);
    }

    public static void appendSide(String msg, Color c) {
        StyledDocument doc = instance.sidePane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendSideBackground(String msg, Color c) {
        StyledDocument doc = instance.sidePane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Background, c);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendPlayer(String msg) {
        appendPlayer(msg, Color.BLACK);
    }

    public static void appendPlayer(String msg, Color c) {
        StyledDocument doc = instance.playerPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendPlayerBackground(String msg, Color c) {
        StyledDocument doc = instance.textPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Background, c);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void clearText() {
        StyledDocument doc = instance.textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void clearSide() {
        StyledDocument doc = instance.sidePane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void clearPlayer() {
        StyledDocument doc = instance.playerPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns the mapPane
     * 
     * @return the mapPane
     */
    public JPanel getMapPane() {
        return mapPane;
    }

    /**
     * Returns the sidePane
     * 
     * @return the sidePane
     */
    public JTextPane getSidePane() {
        return sidePane;
    }

    /**
     * Returns the textPane
     * 
     * @return the textPane
     */
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     * Returns the playerPane
     * 
     * @return the playerPane
     */
    public JTextPane getPlayerPane() {
        return playerPane;
    }

    /**
     * Return the frame
     * 
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Return the textField
     * 
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

        InputObserver[] listeners = listenerList
                .getListeners(InputObserver.class);
        if (listeners.length > 0) {

            InputEvent evt = new InputEvent(this, text);
            for (InputObserver observer : listeners) {
                observer.inputChanged(evt);
            }

        }

    }

    private void setUpKeyBindings() {

        Action moveNorthWest = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1913467203987288379L;

            public void actionPerformed(ActionEvent e) {
                Player.getInstance().move(0);
                Window.getInstance().getMapPane().repaint();

            }
        };
        textField.getInputMap().put(KeyStroke.getKeyStroke("Q"),
                "moveNorthWest");
        textField.getActionMap().put("moveNorthWest", moveNorthWest);

        Action moveNorth = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -7823599887108172961L;

            public void actionPerformed(ActionEvent e) {
                Player.getInstance().move(1);
                Window.getInstance().getMapPane().repaint();

            }
        };
        textField.getInputMap().put(KeyStroke.getKeyStroke("W"), "moveNorth");
        textField.getActionMap().put("moveNorth", moveNorth);

        Action moveNorthEast = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 3971483173919348630L;

            public void actionPerformed(ActionEvent e) {
                Player.getInstance().move(2);
                Window.getInstance().getMapPane().repaint();

            }
        };
        textField.getInputMap().put(KeyStroke.getKeyStroke("E"),
                "moveNorthEast");
        textField.getActionMap().put("moveNorthEast", moveNorthEast);

        Action moveSouthWest = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -8986802417301697297L;

            public void actionPerformed(ActionEvent e) {
                Player.getInstance().move(3);
                Window.getInstance().getMapPane().repaint();

            }
        };
        textField.getInputMap().put(KeyStroke.getKeyStroke("D"),
                "moveSouthWest");
        textField.getActionMap().put("moveSouthWest", moveSouthWest);

        Action moveSouth = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -6960339214481443539L;

            public void actionPerformed(ActionEvent e) {
                Player.getInstance().move(4);
                Window.getInstance().getMapPane().repaint();

            }
        };
        textField.getInputMap().put(KeyStroke.getKeyStroke("S"), "moveSouth");
        textField.getActionMap().put("moveSouth", moveSouth);

        Action moveSouthEast = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -5341066901232125649L;

            public void actionPerformed(ActionEvent e) {
                Player.getInstance().move(5);
                Window.getInstance().getMapPane().repaint();

            }
        };
        textField.getInputMap().put(KeyStroke.getKeyStroke("A"),
                "moveSouthEast");
        textField.getActionMap().put("moveSouthEast", moveSouthEast);
    }

}
