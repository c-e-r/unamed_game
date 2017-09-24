/**
 * 
 */
package unamedGame.events;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import unamedGame.Combat;
import unamedGame.Game;
import unamedGame.entities.Enemy;
import unamedGame.entities.Player;
import unamedGame.input.InputEvent;
import unamedGame.input.InputObserver;
import unamedGame.items.Item;
import unamedGame.ui.Window;

/**
 * Reads and runs events from xml files
 * 
 * @author c-e-r
 *
 */
public class EventReader {

	private static Iterator<Element> iterator;
	private static Element currentElement;
	private static Element root;

	/**
	 * Starts an event from the given string. The string must be the file name of an
	 * event in the events folder.
	 * 
	 * @param event a string of the filename of the event
	 */
	public static void startEvent(String event) {
		Window.clearPane(Window.getInstance().getSidePane());

		Window.clearPane(Window.getInstance().getTextPane());
		SAXReader reader = new SAXReader();
		try {
			File inputFile = new File("data/events/" + event + ".xml");
			Document document = reader.read(inputFile);
			root = (Element) document.selectSingleNode("//branch[@number='0']");
			currentElement = root;

			parseEventXML(root, 0);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Resumes the paused event
	 */
	public static void resumeEvent() {
		Window.clearPane(Window.getInstance().getTextPane());
		resumeParseEventXML(currentElement);
	}
	
	/*
	 * Begins parsing the resumed event
	 */
	private static void resumeParseEventXML(Element element) {
		nextElement();
		interpretElement(currentElement);
		if (currentElement != null) {
			Window.getInstance().addInputObsever(new InputObserver() {
				@Override
				public void inputChanged(InputEvent evt) {
					nextElement();
					if (!interpretElement(currentElement)) {
						Window.getInstance().removeInputObsever(this);

					}

				}
			});
		}
	}

	/*
	 * Waits for the player to hit enter then loads the next element
	 */
	private static void parseEventXML(Element root, int branch) {
		currentElement = (Element) root.selectSingleNode("//branch[@number='" + branch + "']");
		interpretElement(currentElement);
		if (currentElement != null) {
			Window.getInstance().addInputObsever(new InputObserver() {
				@Override
				public void inputChanged(InputEvent evt) {
					nextElement();
					if (!interpretElement(currentElement)) {
						Window.getInstance().removeInputObsever(this);
					}

				}
			});
		}
	}

	/*
	 * sets current element to the next element
	 */
	private static void nextElement() {
		currentElement = getNextElement(currentElement);
	}

	/*
	 * interprets the element based on the element name and does something depending what that is
	 */
	private static boolean interpretElement(Element element) {
		switch (element.getName()) {
		case "text":
			Window.appendToPane(Window.getInstance().getTextPane(), currentElement.getTextTrim());
			return true;
		case "end":
			Game.openExplorationMenu();
			return false;
		case "choice":
			Window.clearPane(Window.getInstance().getSidePane());
			String choiceDescription = ((Element) currentElement.selectSingleNode("choiceDescription")).getTextTrim();
			List<Node> choices = currentElement.selectNodes("option");
			Window.appendToPane(Window.getInstance().getTextPane(), choiceDescription);
			int i = 1;
			for (Node node : choices) {
				Window.appendToPane(Window.getInstance().getSidePane(), i++ + ": " + node.getText());
			}
			waitForChoice(choices);
			return false;
		case "branch":
			nextElement();
			interpretElement(currentElement);
			return true;
		case "combat":
			new Combat(new Enemy(element.getText()));
			return false;
		case "addItem":
			Item item = Player.getInstance().addItemToInventory(new Item(element.getText()));
			Window.appendToPane(Window.getInstance().getTextPane(),
					Game.capitalizeFirstLetter(item.getName()) + " added to inventory");
			return true;
		default:
			System.out.println("Error unrecognized element name: " + currentElement.getName());
			return false;
		}
	}

	/*
	 * Sets an observer to wait for the player to choose a choice
	 */
	private static void waitForChoice(List<Node> choices) {
		int max = choices.size();
		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {
				int choice = -1;
				System.out.println(max);
				if (Game.isNumeric(evt.getText()) && Integer.parseInt(evt.getText()) <= max) {
					choice = Integer.parseInt(
							((Element) choices.get(Integer.parseInt(evt.getText()) - 1)).attributeValue("branch"));
					Window.getInstance().removeInputObsever(this);
					Window.clearPane(Window.getInstance().getSidePane());
					parseEventXML(root, choice);
				} else {
					Window.appendToPane(Window.getInstance().getTextPane(), "Invalid choice");

				}

			}
		});

	}

	/*
	 * Gets the next XML element. Priority is first child, next sibling, then parents next sibling.
	 */
	private static Element getNextElement(Element element) {
		if (element != null) {
			Iterator<Element> childElements = element.elementIterator();
			if (childElements.hasNext()) {

				return childElements.next();
			}

			Iterator<Element> elements = element.getParent().elementIterator();
			while (elements.hasNext() && elements.next() != element) {

			}
			if (elements.hasNext()) {
				return elements.next();
			}

			if (element.getParent().getParent() != null) {
				Iterator<Element> parentElements = element.getParent().getParent().elementIterator();
				while (parentElements.hasNext() && parentElements.next() != element.getParent()) {

				}
				if (parentElements.hasNext()) {
					return parentElements.next();
				}

			}
		}
		return null;
	}

}
