package unamedgame.ui;

import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

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
import unamedgame.items.Item;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JSplitPane;

public class Window {

    private static final Logger LOG = LogManager.getLogger(Game.class);
    private static final String DEFAULT_WINDOW_Y = "50";
    private static final String DEFAULT_WINDOW_X = "200";
    private static final String DEFAULT_WINDOW_WIDTH = "1000";
    private static final String DEFAULT_WINDOW_HEIGHT = "950";
    private static final String DEFAULT_MAP_WIDTH = "300";
    private static final String DEFAULT_MAP_HEIGHT = "300";

    private boolean mapState = true;

    private JFrame frame;
    private JTextField textField;
    private JPanel panel;

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
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                File propFile = new File("saves/window.properties");
                String yOff = Integer.toString(frame.getLocationOnScreen().y);
                String xOff = Integer.toString(frame.getLocationOnScreen().x);
                String width = Integer
                        .toString((int) frame.getBounds().getWidth());
                String height = Integer
                        .toString((int) frame.getBounds().getHeight());
                String mapWidth = Integer
                        .toString((int) splitPane2.getDividerLocation());
                String mapHeight = Integer
                        .toString((int) splitPane.getDividerLocation());
                String state = Integer.toString((int) frame.getExtendedState());
                Properties windowProp = new Properties();
                windowProp.setProperty("windowY", yOff);
                windowProp.setProperty("windowX", xOff);
                windowProp.setProperty("windowWidth", width);
                windowProp.setProperty("windowHeight", height);
                windowProp.setProperty("mapWidth", mapWidth);
                windowProp.setProperty("mapHeight", mapHeight);
                windowProp.setProperty("state", state);
                try {
                    windowProp.store(new FileOutputStream(propFile), "");
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        int yOff = 0;
        int xOff = 0;
        int width = 0;
        int height = 0;
        int mapWidth = 0;
        int mapHeight = 0;
        int state = JFrame.NORMAL;
        File propFile = new File("saves/window.properties");
        if (propFile.exists()) {
            Properties windowProp = new Properties();
            try {
                windowProp.load(new FileReader(propFile));
                yOff = Integer.parseInt(
                        windowProp.getProperty("windowY", DEFAULT_WINDOW_Y));
                xOff = Integer.parseInt(
                        windowProp.getProperty("windowX", DEFAULT_WINDOW_X));
                width = Integer.parseInt(windowProp.getProperty("windowWidth",
                        DEFAULT_WINDOW_WIDTH));
                height = Integer.parseInt(windowProp.getProperty("windowHeight",
                        DEFAULT_WINDOW_HEIGHT));
                mapWidth = Integer.parseInt(
                        windowProp.getProperty("mapWidth", DEFAULT_MAP_WIDTH));
                mapHeight = Integer.parseInt(windowProp.getProperty("mapHeight",
                        DEFAULT_MAP_HEIGHT));
                state = Integer.parseInt(windowProp.getProperty("mapWidth",
                        Integer.toString(JFrame.NORMAL)));

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {
            Properties windowProp = new Properties();
            windowProp.setProperty("windowY", (DEFAULT_WINDOW_Y));
            windowProp.setProperty("windowX", (DEFAULT_WINDOW_X));
            windowProp.setProperty("windowWidth", (DEFAULT_WINDOW_WIDTH));
            windowProp.setProperty("windowHeight", (DEFAULT_WINDOW_HEIGHT));
            windowProp.setProperty("mapWidth", (DEFAULT_MAP_WIDTH));
            windowProp.setProperty("mapHeight", (DEFAULT_MAP_HEIGHT));
            windowProp.setProperty("state", Integer.toString(JFrame.NORMAL));
            yOff = Integer.parseInt(
                    windowProp.getProperty("windowY", DEFAULT_WINDOW_Y));
            xOff = Integer.parseInt(
                    windowProp.getProperty("windowX", DEFAULT_WINDOW_X));
            width = Integer.parseInt(windowProp.getProperty("windowWidth",
                    DEFAULT_WINDOW_WIDTH));
            height = Integer.parseInt(
                    windowProp.getProperty("windowHeight", DEFAULT_MAP_HEIGHT));
            mapWidth = Integer.parseInt(
                    windowProp.getProperty("mapWidth", DEFAULT_MAP_WIDTH));
            mapHeight = Integer.parseInt(
                    windowProp.getProperty("mapHeight", DEFAULT_MAP_HEIGHT));
            state = Integer.parseInt(windowProp.getProperty("state",
                    Integer.toString(JFrame.NORMAL)));
            try {
                windowProp.store(new FileOutputStream(propFile), "");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        frame.setBounds(xOff, yOff, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setExtendedState(state);
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

        splitPane2.setDividerLocation(mapWidth);
        splitPane.setDividerLocation(mapHeight);

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
        appendText(msg, c, Color.WHITE);
    }

    public static void appendText(String msg, Color text, Color background) {
        StyledDocument doc = instance.textPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, text);
        aset = sc.addAttribute(aset, StyleConstants.Background, background);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendTextBackground(String msg, Color c) {
        appendText(msg, Color.BLACK, c);
    }

    public static void appendSide(String msg) {
        appendSide(msg, Color.BLACK);
    }

    public static void appendSide(String msg, Color c) {
        appendSide(msg, Color.BLACK, Color.WHITE);
    }

    public static void appendSide(String msg, Color text, Color background) {
        StyledDocument doc = instance.sidePane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();

        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, text);
        aset = sc.addAttribute(aset, StyleConstants.Background, background);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendSideBackground(String msg, Color c) {
        appendSide(msg, Color.BLACK, c);
    }

    public static void appendPlayer(String msg) {
        appendPlayer(msg, Color.BLACK, Color.WHITE);
    }

    public static void appendPlayer(String msg, Color c) {
        appendPlayer(msg, c, Color.WHITE);
    }

    public static void appendPlayer(String msg, Color text, Color background) {
        StyledDocument doc = instance.playerPane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, text);
        aset = sc.addAttribute(aset, StyleConstants.Background, background);
        try {
            doc.insertString(doc.getLength(), msg, aset);

        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        LOG.debug(msg);
    }

    public static void appendPlayerBackground(String msg, Color c) {
        appendPlayer(msg, Color.BLACK, c);
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
