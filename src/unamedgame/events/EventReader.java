/**
 * 
 */
package unamedgame.events;

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

import unamedgame.Combat;
import unamedgame.Dice;
import unamedgame.Game;
import unamedgame.effects.Effect;
import unamedgame.entities.Enemy;
import unamedgame.entities.Player;
import unamedgame.input.InputEvent;
import unamedgame.input.InputObserver;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.ui.Window;

/**
 * Reads and runs events from xml files.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class EventReader {

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static Element currentElement;
    private static Element root;
    private static boolean skipChildren;
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

        Window.clearSide();

        Window.clearText();
        SAXReader reader = new SAXReader();
        try {
            File inputFile = new File("data/events/" + event + ".xml");
            Document document = reader.read(inputFile);
            root = (Element) document.selectSingleNode("//branch[@number='0']");
            currentElement = root;

            parseEventXML(root, 0);

        } catch (DocumentException e) {
            Window.appendText("ERROR: " + e.getMessage() + "\n");
            Game.openExplorationMenu();
            LOG.error("Error loading event xml file", e);
            e.printStackTrace();
        }

    }

    /**
     * Resumes the paused event.
     */
    public static void resumeEvent() {
        Window.clearText();
        Window.clearSide();

        resumeParseEventXML();
    }

    /**
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

    /**
     * Waits for the player to hit enter then loads the next element.
     * 
     * @param root
     *            the root element
     * @param branch
     *            the branch to start from
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

    /**
     * sets current element to the next element.
     */
    private static void nextElement() {
        currentElement = getNextElement(currentElement);
    }

    /**
     * interprets the element based on the element name and does something.
     * depending what that is
     * 
     * @param element
     *            the element to interpret
     */
    private static void interpretElement(Element element) {
        boolean not;
        int roll = 0;

        switch (element.getName()) {
        case "text":
            Window.appendText(currentElement.getTextTrim() + "\n");
            break;
        case "end":
            Window.clearSide();
            Window.clearText();
            stop = true;
            Game.openExplorationMenu();
            break;
        case "choice":
            Window.clearSide();
            String choiceDescription = ((Element) currentElement
                    .selectSingleNode("choiceDescription")).getTextTrim();
            List<Node> choices = currentElement.selectNodes("option");
            choices.addAll(getAllIfOptions(currentElement));

            Window.appendText(choiceDescription + "\n");
            int i = 1;
            for (Node node : choices) {
                Window.appendSide(i++ + ": " + node.getText() + "\n");
            }
            waitForChoice(choices);
            stop = true;
            break;
        case "branch":
            nextElement();
            interpretElement(currentElement);
            break;
        case "combat":
            Enemy newEnemy = Enemy.buildEnemy(element.getText());
            if (newEnemy != null) {
                new Combat(newEnemy, () -> Combat.backToEvent());
            } else {
                Window.appendText(
                        "ERROR: Somthing went wrong while creating an enemy. See game.log for more info.\n");
            }

            stop = true;
            break;
        case "addItem":
            Item newItem = Item.buildItem(element.getText());
            if (newItem != null) {
                Player.getInstance().addItemToInventory(newItem);
                Window.appendText(Game.capitalizeFirstLetter(newItem.getName())
                        + " added to inventory\n");
            } else {
                Window.appendText(
                        "ERROR: Something went wrong adding an item to your inventory. See game.log for more information.\n");
            }
            break;
        case "removeItem":
            if (Player.getInstance()
                    .removeItemFromInventory(element.getText())) {
                Window.appendText(Game.capitalizeFirstLetter(
                        element.getText() + " removed from inventory.\n"));
            }
            break;
        case "removeItemUse":
            Player.getInstance().removeItemUse(element.getText());
            nextElement();
            interpretElement(currentElement);
            break;
        case "useItem":
            Item newUseItem = Item.buildItem(element.getText());
            if(element.attributeValue("user") != null) {
                Player.getInstance().applyItemEffects(newUseItem, Enemy.buildEnemy(element.attributeValue("user")));
            } else {
                Player.getInstance().applyItemEffects(newUseItem, Player.getInstance());
            }
            break;
        case "addSkill":
            Skill newSkill = Skill.buildSkill(element.getText());
            if (newSkill != null) {
                Player.getInstance().addInnateSkill(newSkill);
                Window.appendText(Game.capitalizeFirstLetter(
                        "Gained Skill: " + newSkill.getName()) + "\n");
            } else {
                Window.appendText(
                        "ERROR: Something went wrong adding a skill to your innate skills. See game.log for more information.\n");
            }
            break;
        case "removeSkill":
            if (Player.getInstance()
                    .removeSkillFromInnateSkills(element.getText())) {
                Window.appendText(Game.capitalizeFirstLetter(element.getText()
                        + " has been removed from innate skills."));
            }
            break;
        case "addSpell":
            Spell newSpell = Spell.buildSpell(element.getText());
            if (newSpell != null) {
                Player.getInstance().addKnownSpell(newSpell);
                Window.appendText(Game.capitalizeFirstLetter(
                        "Learned Spell: " + newSpell.getName()) + "\n");
            } else {
                Window.appendText(
                        "ERROR: Something went wrong adding a spell to your known spells. See game.log for more information.\n");
            }
            break;
        case "removeSpell":
            if (Player.getInstance()
                    .removeSpellFromKnownSpells(element.getText())) {
                Window.appendText(Game.capitalizeFirstLetter(element.getText()
                        + " has been removed from known spells."));
            }
            break;
        case "addEffect":
            Effect newEffect = Effect.buildEffect(element);
            if (newEffect != null) {
                Player.getInstance().addEffect(newEffect, null);
                skipChildren = true;
            } else {
                Window.appendText(
                        "ERROR: Something went wrong adding an effect to the player. See game.log for more information.\n");
            }
            break;
        case "ifRoll":
            if (element.attributeValue("roll") != null) {
                roll = Integer.parseInt(element.attributeValue("roll"));
            }
            int rollValue = Integer.parseInt(element.attributeValue("value"));
            String rollOperator = element.attributeValue("operator");

            if (!checkRoll(rollOperator, rollValue, roll)) {
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifStat":
            if (element.attributeValue("roll") != null) {
                roll = Integer.parseInt(element.attributeValue("roll"));
            }
            double value = Double.parseDouble(element.attributeValue("value"));
            String operator = element.attributeValue("operator");
            String stat = element.attributeValue("stat");
            System.out.println(roll);
            if (!Player.getInstance().checkStat(stat, operator, value, roll)) {
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifFlag":
            if (element.attributeValue("roll") != null) {
                roll = Integer.parseInt(element.attributeValue("roll"));
            }
            int flagValue = Integer.parseInt(element.attributeValue("value"));
            String flagOperator = element.attributeValue("operator");
            String flag = element.attributeValue("flag");
            if (!Player.getInstance().checkFlag(flag, flagOperator, flagValue,
                    roll)) {
                skipChildren = true;
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
            if (element.attributeValue("roll") != null) {
                roll = Integer.parseInt(element.attributeValue("roll"));
            }
            int tempFlagValue = Integer
                    .parseInt(element.attributeValue("value"));
            String tempFlagOperator = element.attributeValue("operator");
            String tempFlag = element.attributeValue("flag");

            if (!checkTempFlag(tempFlag, tempFlagOperator, tempFlagValue,
                    roll)) {
                skipChildren = true;
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
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifEffect":
            not = Boolean.parseBoolean(element.attributeValue("not"));
            String effectName = element.attributeValue("effectName");
            if (not ^ !Player.getInstance().checkIfEffected(effectName)) {
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifItem":
            not = Boolean.parseBoolean(element.attributeValue("not"));
            String itemName = element.attributeValue("itemName");
            if (not ^ !Player.getInstance().checkIfInInventory(itemName)) {
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifItemUses":
            String itemUsesName = element.attributeValue("itemName");
            int itemUsesValue = Integer
                    .parseInt(element.attributeValue("value"));
            String itemUsesOperator = element.attributeValue("operator");
            if (!Player.getInstance().checkIfItemUses(itemUsesName,
                    itemUsesOperator, itemUsesValue)) {
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifSkill":
            not = Boolean.parseBoolean(element.attributeValue("not"));
            String skillName = element.attributeValue("skillName");
            if (not ^ !Player.getInstance().checkIfSkill(skillName)) {
                skipChildren = true;
            }
            nextElement();
            interpretElement(currentElement);
            break;
        case "ifSpell":
            not = Boolean.parseBoolean(element.attributeValue("not"));
            String spellName = element.attributeValue("spellName");
            if (not ^ !Player.getInstance().checkIfSpell(spellName)) {
                skipChildren = true;
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

    /**
     * Sets an observer to wait for the player to choose a choice.
     * 
     * @param choices
     *            the list of choices for the player to choose from
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
                    Window.clearSide();
                    parseEventXML(root, choice);
                } else {
                    Window.appendText("Invalid choice\n");

                }

            }
        });

    }

    /**
     * Gets the next XML element. Priority is first child, next sibling, then
     * parents next sibling.
     * 
     * @param element
     *            the element to get the next from
     */
    private static Element getNextElement(Element element) {
        if (element != null) {
            Iterator<Element> childElements = element.elementIterator();
            if (!skipChildren && childElements.hasNext()) {
                return childElements.next();
            }
            skipChildren = false;
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
        int roll = 0;

        List<Node> choices = new ArrayList<>();
        List<Node> ifRollChoices = node.selectNodes("ifRoll");
        for (Node n : ifRollChoices) {
            if (n != null) {
                if (((Element) n).attributeValue("roll") != null) {
                    roll = Integer
                            .parseInt(((Element) n).attributeValue("roll"));
                } else {
                    roll = 0;
                }
                int rollValue = Integer
                        .parseInt(((Element) n).attributeValue("value"));
                String rollOperator = ((Element) n).attributeValue("operator");
                if (checkRoll(rollOperator, rollValue, roll)) {
                    choices.addAll(n.selectNodes("option"));

                    List<Node> innerIfChoices = n
                            .selectNodes("./*[starts-with(name(), 'if')]");
                    if (innerIfChoices.size() != 0) {
                        choices.addAll(getAllIfOptions(n));
                    }
                }

            }

        }
        List<Node> ifFlagChoices = node.selectNodes("ifFlag");
        for (Node n : ifFlagChoices) {
            if (n != null) {
                if (((Element) n).attributeValue("roll") != null) {
                    roll = Integer
                            .parseInt(((Element) n).attributeValue("roll"));
                } else {
                    roll = 0;
                }
                int flagValue = Integer
                        .parseInt(((Element) n).attributeValue("value"));
                String flagOperator = ((Element) n).attributeValue("operator");
                String flag = ((Element) n).attributeValue("flag");
                if (Player.getInstance().checkFlag(flag, flagOperator,
                        flagValue, roll)) {
                    choices.addAll(n.selectNodes("option"));

                    List<Node> innerIfChoices = n
                            .selectNodes("./*[starts-with(name(), 'if')]");
                    if (innerIfChoices.size() != 0) {
                        choices.addAll(getAllIfOptions(n));
                    }
                }

            }

        }
        List<Node> ifTempFlagChoices = node.selectNodes("ifTempFlag");
        for (Node n : ifTempFlagChoices) {
            if (n != null) {
                if (((Element) n).attributeValue("roll") != null) {
                    roll = Integer
                            .parseInt(((Element) n).attributeValue("roll"));
                } else {
                    roll = 0;
                }
                int tempFlagValue = Integer
                        .parseInt(((Element) n).attributeValue("value"));
                String tempFlagOperator = ((Element) n)
                        .attributeValue("operator");
                String tempFlag = ((Element) n).attributeValue("flag");
                if (checkTempFlag(tempFlag, tempFlagOperator, tempFlagValue,
                        roll)) {
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
                if (((Element) n).attributeValue("roll") != null) {
                    roll = Integer
                            .parseInt(((Element) n).attributeValue("roll"));
                } else {
                    roll = 0;
                }
                int value = Integer
                        .parseInt(((Element) n).attributeValue("value"));
                String operator = ((Element) n).attributeValue("operator");
                String stat = ((Element) n).attributeValue("stat");
                if (Player.getInstance().checkStat(stat, operator, value,
                        roll)) {
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
        List<Node> ifItemUsesChoices = node.selectNodes("ifItemUses");
        for (Node n : ifItemUsesChoices) {
            if (n != null) {
                if (((Element) n).attributeValue("roll") != null) {
                    roll = Integer
                            .parseInt(((Element) n).attributeValue("roll"));
                } else {
                    roll = 0;
                }
                int value = Integer
                        .parseInt(((Element) n).attributeValue("value"));
                String operator = ((Element) n).attributeValue("operator");
                String itemName = ((Element) n).attributeValue("itemName");
                if (Player.getInstance().checkIfItemUses(itemName, operator,
                        value)) {
                    choices.addAll(n.selectNodes("option"));

                    List<Node> innerIfChoices = n
                            .selectNodes("./*[starts-with(name(), 'if')]");
                    if (innerIfChoices.size() != 0) {
                        choices.addAll(getAllIfOptions(n));
                    }
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

    /**
     * Checks the temporary flag against the value with the operator.
     * 
     * @param flagName
     *            the flag to check
     * @param operator
     *            the operator to use
     * @param value
     *            the value to compare to
     * @param roll
     *            the roll
     * @return the result
     */
    public static boolean checkTempFlag(String flagName, String operator,
            int value, int roll) {
        int flagValue = getTempFlagValue(flagName);
        switch (operator) {
        case "=":
            return flagValue + Dice.roll(roll) == value;
        case "!=":
            return flagValue + Dice.roll(roll) != value;
        case "<":
            return flagValue + Dice.roll(roll) < value;
        case ">":
            return flagValue + Dice.roll(roll) > value;
        case "<=":
            return flagValue + Dice.roll(roll) <= value;
        case ">=":
            return flagValue + Dice.roll(roll) >= value;
        default:
            return false;
        }
    }

    /**
     * Check a random roll against the value.
     * 
     * @param operator
     *            the operator to use
     * @param value
     *            the value to compare to
     * @param roll
     *            the roll
     * @return the result
     */
    public static boolean checkRoll(String operator, int value, int roll) {
        switch (operator) {
        case "=":
            return Dice.roll(roll) == value;
        case "!=":
            return Dice.roll(roll) != value;
        case "<":
            return Dice.roll(roll) < value;
        case ">":
            return Dice.roll(roll) > value;
        case "<=":
            return Dice.roll(roll) <= value;
        case ">=":
            return Dice.roll(roll) >= value;
        default:
            return false;
        }
    }

    /**
     * Sets a temporary flag with the given value.
     * 
     * @param flag
     *            the flag to set
     * @param operator
     *            the operator to use
     * @param value
     *            the value to set
     */
    public static void setTempFlag(String flag, String operator, int value) {
        switch (operator) {
        case "=":
            tempFlags.put(flag, value);
            break;
        case "+":
            tempFlags.put(flag, tempFlags.get(flag) - value);
            break;
        case "-":
            tempFlags.put(flag, tempFlags.get(flag) + value);
            break;
        case "*":
            tempFlags.put(flag, tempFlags.get(flag) * value);
            break;
        case "/":
            tempFlags.put(flag, tempFlags.get(flag) / value);
            break;
        default:
        }
    }

    /**
     * Returns the flag value if the flag exists or 0 it it does not.
     * 
     * @param flag
     *            the flag to get
     * @return the flag value
     */
    public static int getTempFlagValue(String flag) {
        if (tempFlags.get(flag) != null) {
            return tempFlags.get(flag);
        }
        return 0;
    }

    /**
     * Recursively looks for a parent with a sibling of the given element.
     * 
     * @param element
     *            the element to start from
     * @return the first parents sibling it finds
     */
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
