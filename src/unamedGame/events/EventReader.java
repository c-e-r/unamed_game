/**
 * 
 */
package unamedGame.events;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static final Logger LOG = LogManager.getLogger(Game.class);

	private static Element currentElement;
	private static Element root;
	private static boolean skipNext;
	private static boolean stop;
	private static HashMap<String, Integer> tempFlags;

	/**
	 * Starts an event from the given string. The string must be the file name
	 * of an event in the events folder.
	 * 
	 * @param event
	 *            a string of the filename of the event
	 */
	public static void startEvent(String event) {
		tempFlags = new HashMap<String, Integer>();

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
			Window.appendToPane(Window.getInstance().getTextPane(),
					"ERROR: " + e.getMessage());
			Game.openExplorationMenu();
			LOG.error("Error loading event xml file", e);
			e.printStackTrace();
		}

	}

	/**
	 * Resumes the paused event
	 */
	public static void resumeEvent() {
		Window.clearPane(Window.getInstance().getTextPane());
		resumeParseEventXML();
	}

	/*
	 * Begins parsing the resumed event
	 */
	private static void resumeParseEventXML() {
		nextElement();
		interpretElement(currentElement);
		if (currentElement != null) {
			Window.getInstance().addInputObsever(new InputObserver() {
				@Override
				public void inputChanged(InputEvent evt) {
					nextElement();
					interpretElement(currentElement);
					if (stop) {
						stop = false;
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
		currentElement = (Element) root
				.selectSingleNode("//branch[@number='" + branch + "']");
		nextElement();
		interpretElement(currentElement);
		if (currentElement != null) {
			Window.getInstance().addInputObsever(new InputObserver() {
				@Override
				public void inputChanged(InputEvent evt) {
					nextElement();
					interpretElement(currentElement);
					if (stop) {
						stop = false;
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
	 * interprets the element based on the element name and does something
	 * depending what that is
	 */
	private static void interpretElement(Element element) {
		boolean not;

		switch (element.getName()) {
		case "text":
			Window.appendToPane(Window.getInstance().getTextPane(),
					currentElement.getTextTrim());
			break;
		case "end":
			Game.openExplorationMenu();
			stop = true;
			break;
		case "choice":
			Window.clearPane(Window.getInstance().getSidePane());
			String choiceDescription = ((Element) currentElement
					.selectSingleNode("choiceDescription")).getTextTrim();
			List<Node> choices = currentElement.selectNodes("option");
			choices.addAll(getAllIfOptions(currentElement));

			Window.appendToPane(Window.getInstance().getTextPane(),
					choiceDescription);
			int i = 1;
			for (Node node : choices) {
				Window.appendToPane(Window.getInstance().getSidePane(),
						i++ + ": " + node.getText());
			}
			waitForChoice(choices);
			stop = true;
			break;
		case "branch":
			nextElement();
			interpretElement(currentElement);
		case "combat":
			Enemy newEnemy = Enemy.buildEnemy(element.getText());
			if (newEnemy != null) {
				new Combat(newEnemy, Combat.FROM_EVENT);
			} else {
				Window.appendToPane(Window.getInstance().getTextPane(),
						"ERROR: Somthing went wrong while creating an enemy. See game.log for more info.");
			}

			stop = true;
			break;
		case "addItem":
			Item newItem = Item.buildItem(element.getText());
			if (newItem != null) {
				Item item = Player.getInstance().addItemToInventory(newItem);
				Window.appendToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(item.getName())
								+ " added to inventory");
			} else {
				Window.appendToPane(Window.getInstance().getTextPane(),
						"ERROR: Somthing went wrong adding an item to your inventry. See game.log for more information.");
			}
			break;
		case "ifStat":
			double value = Double.parseDouble(element.attributeValue("value"));
			String operator = element.attributeValue("operator");
			String stat = element.attributeValue("stat");

			if (!Player.getInstance().checkStat(stat, operator, value)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifFlag":
			int flagValue = Integer.parseInt(element.attributeValue("value"));
			String flagOperator = element.attributeValue("operator");
			String flag = element.attributeValue("flag");
			if (!Player.getInstance().checkFlag(flag, flagOperator,
					flagValue)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "setFlag":
			int setFlagValue = Integer
					.parseInt(element.attributeValue("value"));
			String setFlagOperator = element.attributeValue("operator");
			String setFlagName = element.attributeValue("flag");
			Player.getInstance().setFlag(setFlagName, setFlagOperator,
					setFlagValue);
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifTempFlag":
			int tempFlagValue = Integer
					.parseInt(element.attributeValue("value"));
			String tempFlagOperator = element.attributeValue("operator");
			String tempFlag = element.attributeValue("flag");

			if (!checkTempFlag(tempFlag, tempFlagOperator, tempFlagValue)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "setTempFlag":
			int setTempFlagValue = Integer
					.parseInt(element.attributeValue("value"));
			String setTempFlagOperator = element.attributeValue("operator");
			String setTempFlagName = element.attributeValue("flag");
			setTempFlag(setTempFlagName, setTempFlagOperator, setTempFlagValue);
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifEquipped":
			not = Boolean.parseBoolean(element.attributeValue("not"));
			String equippedItemName = element.attributeValue("itemName");
			if (not ^ !Player.getInstance().checkIfEquipped(equippedItemName)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifEffect":
			not = Boolean.parseBoolean(element.attributeValue("not"));
			String effectName = element.attributeValue("effectName");
			if (not ^ !Player.getInstance().checkIfEffected(effectName)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifItem":
			not = Boolean.parseBoolean(element.attributeValue("not"));
			String itemName = element.attributeValue("itemName");
			if (not ^ !Player.getInstance().checkIfInInventory(itemName)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifSkill":
			not = Boolean.parseBoolean(element.attributeValue("not"));
			String skillName = element.attributeValue("skillName");
			if (not ^ !Player.getInstance().checkIfSkill(skillName)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "ifSpell":
			not = Boolean.parseBoolean(element.attributeValue("not"));
			String spellName = element.attributeValue("spellName");
			if (not ^ !Player.getInstance().checkIfSpell(spellName)) {
				skipNext = true;
			}
			nextElement();
			interpretElement(currentElement);
			break;
		case "goto":
			System.out.println("goto");
			int branch = Integer.parseInt(element.attributeValue("branch"));
			currentElement = (Element) root
					.selectSingleNode("//branch[@number='" + branch + "']");
			nextElement();
			interpretElement(currentElement);
			break;
		default:
			LOG.error("Error unrecognized element name: "
					+ currentElement.getName());
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
				if (Game.isNumeric(evt.getText())
						&& Integer.parseInt(evt.getText()) <= max
						&& Integer.parseInt(evt.getText()) > 0) {
					choice = Integer.parseInt(((Element) choices
							.get(Integer.parseInt(evt.getText()) - 1))
									.attributeValue("branch"));
					Window.getInstance().removeInputObsever(this);
					Window.clearPane(Window.getInstance().getSidePane());
					parseEventXML(root, choice);
				} else {
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid choice");

				}

			}
		});

	}

	/*
	 * Gets the next XML element. Priority is first child, next sibling, then
	 * parents next sibling.
	 */
	private static Element getNextElement(Element element) {
		if (element != null) {
			Iterator<Element> childElements = element.elementIterator();
			if (!skipNext && childElements.hasNext()) {
				return childElements.next();
			}
			skipNext = false;
			Iterator<Element> elements = element.getParent().elementIterator();
			while (elements.hasNext() && elements.next() != element) {

			}
			if (elements.hasNext()) {
				return elements.next();
			}

			if (element.getParent().getParent() != null) {
				Iterator<Element> parentElements = element.getParent()
						.getParent().elementIterator();
				while (parentElements.hasNext()
						&& parentElements.next() != element.getParent()) {

				}
				if (parentElements.hasNext()) {
					return parentElements.next();
				}

			}
			if (element.getParent().getParent() != null) {
			}
			return getNextParentWithSibling(element);
		}
		return null;
	}

	private static List<Node> getAllIfOptions(Node node) {
		boolean not;
		List<Node> choices = new ArrayList<>();
		List<Node> ifFlagChoices = node.selectNodes("ifFlag");
		for (Node n : ifFlagChoices) {
			if (n != null) {
				int flagValue = Integer
						.parseInt(((Element) n).attributeValue("value"));
				String flagOperator = ((Element) n).attributeValue("operator");
				String flag = ((Element) n).attributeValue("flag");
				if (Player.getInstance().checkFlag(flag, flagOperator,
						flagValue)) {
					choices.addAll(n.selectNodes("option"));

					List<Node> innerIfChoices = n
							.selectNodes("./*[starts-with(name(), 'if')]");
					if (innerIfChoices.size() != 0) {
						choices.addAll(getAllIfOptions(n));
					}
				}

			}

		}
		List<Node> ifStatChoices = node.selectNodes("ifStat");
		for (Node n : ifStatChoices) {
			if (n != null) {
				int value = Integer
						.parseInt(((Element) n).attributeValue("value"));
				String operator = ((Element) n).attributeValue("operator");
				String stat = ((Element) n).attributeValue("stat");
				if (Player.getInstance().checkStat(stat, operator, value)) {
					choices.addAll(n.selectNodes("option"));

					List<Node> innerIfChoices = n
							.selectNodes("./*[starts-with(name(), 'if')]");
					if (innerIfChoices.size() != 0) {
						choices.addAll(getAllIfOptions(n));
					}
				}

			}

		}
		List<Node> ifEquippedChoices = node.selectNodes("ifEquipped");
		for (Node n : ifEquippedChoices) {
			not = Boolean.parseBoolean(((Element) n).attributeValue("not"));
			if (not ^ Player.getInstance().checkIfEquipped(
					((Element) n).attributeValue("itemName"))) {
				choices.addAll(n.selectNodes("option"));

				List<Node> innerIfChoices = n
						.selectNodes("./*[starts-with(name(), 'if')]");
				if (innerIfChoices.size() != 0) {
					choices.addAll(getAllIfOptions(n));
				}
			}

		}
		List<Node> ifItemChoices = node.selectNodes("ifItem");
		for (Node n : ifItemChoices) {
			not = Boolean.parseBoolean(((Element) n).attributeValue("not"));

			if (not ^ Player.getInstance().checkIfInInventory(
					((Element) n).attributeValue("itemName"))) {
				choices.addAll(n.selectNodes("option"));

				List<Node> innerIfChoices = n
						.selectNodes("./*[starts-with(name(), 'if')]");
				if (innerIfChoices.size() != 0) {
					choices.addAll(getAllIfOptions(n));
				}
			}
		}
		List<Node> ifSkillChoices = node.selectNodes("ifSkill");
		for (Node n : ifSkillChoices) {
			not = Boolean.parseBoolean(((Element) n).attributeValue("not"));

			if (not ^ Player.getInstance()
					.checkIfSkill(((Element) n).attributeValue("skillName"))) {
				choices.addAll(n.selectNodes("option"));

				List<Node> innerIfChoices = n
						.selectNodes("./*[starts-with(name(), 'if')]");
				if (innerIfChoices.size() != 0) {
					choices.addAll(getAllIfOptions(n));
				}
			}
		}
		List<Node> ifSpellChoices = node.selectNodes("ifSpell");
		for (Node n : ifSpellChoices) {
			not = Boolean.parseBoolean(((Element) n).attributeValue("not"));

			if (not ^ Player.getInstance()
					.checkIfSpell(((Element) n).attributeValue("spellName"))) {
				choices.addAll(n.selectNodes("option"));

				List<Node> innerIfChoices = n
						.selectNodes("./*[starts-with(name(), 'if')]");
				if (innerIfChoices.size() != 0) {
					choices.addAll(getAllIfOptions(n));
				}
			}
		}
		List<Node> ifEffectChoices = node.selectNodes("ifEffect");
		for (Node n : ifEffectChoices) {
			not = Boolean.parseBoolean(((Element) n).attributeValue("not"));

			if (not ^ Player.getInstance().checkIfEffected(
					((Element) n).attributeValue("effectName"))) {
				choices.addAll(n.selectNodes("option"));

				List<Node> innerIfChoices = n
						.selectNodes("./*[starts-with(name(), 'if')]");
				if (innerIfChoices.size() != 0) {
					choices.addAll(getAllIfOptions(n));
				}
			}
		}
		return choices;
	}

	public static boolean checkTempFlag(String flagName, String operator,
			int value) {
		int flagValue = getTempFlagValue(flagName);
		switch (operator) {
		case "=":
			return flagValue == value;
		case "!=":
			return flagValue != value;
		case "<":
			return flagValue < value;
		case ">":
			return flagValue > value;
		case "<=":
			return flagValue <= value;
		case ">=":
			return flagValue >= value;
		default:
			return false;
		}
	}

	public static void setTempFlag(String flag, String operator, int value) {
		switch (operator) {
		case "=":
			tempFlags.put(flag, value);
		case "+":
			tempFlags.put(flag, tempFlags.get(flag) - value);
		case "-":
			tempFlags.put(flag, tempFlags.get(flag) + value);
		case "*":
			tempFlags.put(flag, tempFlags.get(flag) * value);
		case "/":
			tempFlags.put(flag, tempFlags.get(flag) / value);
		default:
		}
	}

	public static int getTempFlagValue(String flag) {
		if (tempFlags.get(flag) != null) {
			return tempFlags.get(flag);
		}
		return 0;
	}

	private static Element getNextParentWithSibling(Element element) {
		Iterator<Element> parentElements = element.getParent().getParent()
				.elementIterator();
		while (parentElements.hasNext()
				&& parentElements.next() != element.getParent()) {
		}
		if (parentElements.hasNext()) {
			return parentElements.next();
		}
		return getNextParentWithSibling(element.getParent());
	}

}
