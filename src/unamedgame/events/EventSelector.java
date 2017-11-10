/**
 * 
 */
package unamedgame.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import unamedgame.Dice;
import unamedgame.entities.Player;

/**
 * A class to randomly select events.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class EventSelector {

    /**
     * Starts a random event from an event list file.
     * 
     * @param fileName
     *            the filename of the list file
     * @throws FileNotFoundException
     *             if the file can not be found
     */
    public static void startRandomEventFromFile(String fileName, Runnable back)
            throws FileNotFoundException {
        EventReader.startEvent(
                chooseEventFromList(getEventListFromFile(fileName)), back);

    }

    /**
     * Starts a random event from a list of event file names.
     * 
     * @param fileNames
     *            the event filesnames list
     * @throws FileNotFoundException
     *             if the event files cannot be found
     */
    public static void startRandomEventFromFileList(List<String> fileNames, Runnable back)
            throws FileNotFoundException {
        EventReader.startEvent(
                chooseEventFromList(getEventListFromFiles(fileNames)), back);
    }

    /**
     * Gets a list of events from an event list file.
     * 
     * @param fileNames
     *            an array of event list files
     * @return a list of event files
     * @throws FileNotFoundException
     *             if file cannot be found
     */
    private static ArrayList<String> getEventListFromFiles(List<String> fileNames)
            throws FileNotFoundException {
        ArrayList<String> newList = new ArrayList<String>();
        ArrayList<String> temp;
        for (String fileName : fileNames) {
            temp = getEventListFromFile(fileName);
            for (String string : temp) {
                if (!newList.contains(string)) {
                    newList.add(string);
                }
            }
        }
        return newList;
    }

    /**
     * Chooses an event from an event filename list.
     * 
     * @param list
     *            the list to choose from
     * @return the chosen event
     */
    private static String chooseEventFromList(ArrayList<String> list) {
        if (list.size() == 0) {
            return "defaultEvent";
        }
        list = pruneEventList(list);
        String event = list.get(Dice.roll(list.size()) - 1);
        return event;

    }
    
    public static ArrayList<String> prunedEventListFromFile(String fileName) {
            try {
                
                return pruneEventList(getEventListFromFile(fileName));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
    }
    
    public static ArrayList<String> pruneEventList(ArrayList<String> list) {
        System.out.println(list);
        Iterator<String> itr = list.iterator();
        String[] tmp;
        while(itr.hasNext()) {
            tmp = itr.next().split("\\|");
            if(!checkEventConditions(tmp)) {
                itr.remove();
            }
        }
        System.out.println(list);
        return list;
    }

    public static boolean checkEventConditions(String[] eventStrings) {
        for (int i = 1; i < eventStrings.length; i++) {
            int roll;
            String operator;
            String[] seperatedIf = eventStrings[i].split(":");

            switch (seperatedIf[0]) {
            case "ifStat":
                String stat = seperatedIf[1];
                roll = Integer.parseInt(seperatedIf[2]);
                operator = seperatedIf[3];
                double value = Double.parseDouble(seperatedIf[4]);
                if (!Player.getInstance().checkStat(stat, operator, value,
                        roll)) {
                    return false;
                }
                break;
            case "ifRoll":
                roll = Integer.parseInt(seperatedIf[1]);
                operator = seperatedIf[2];
                int rollValue = Integer.parseInt(seperatedIf[3]);
                if (EventReader.checkRoll(operator, rollValue, roll)) {
                    return false;
                }
                break;
            case "ifFlag":
                String flag = seperatedIf[1];
                roll = Integer.parseInt(seperatedIf[2]);
                operator = seperatedIf[3];
                int flagValue = Integer.parseInt(seperatedIf[4]);
                if (!Player.getInstance().checkFlag(flag, operator, flagValue,
                        roll)) {
                    return false;
                }
                break;
            case "ifEquipped":
                String itemEquippedName = seperatedIf[1];
                Boolean notEquipped = false;
                if (seperatedIf.length == 3 && seperatedIf[2].equals("not")) {
                    notEquipped = true;
                }
                if (notEquipped ^ !Player.getInstance()
                        .checkIfEquipped(itemEquippedName)) {
                    return false;
                }
                break;
            case "ifItem":
                String itemName = seperatedIf[1];
                Boolean notInInventory = false;
                if (seperatedIf.length == 3 && seperatedIf[2].equals("not")) {
                    notInInventory = true;
                }
                if (notInInventory
                        ^ !Player.getInstance().checkIfInInventory(itemName)) {
                    return false;
                }
                break;
            case "ifItemUses":
                String itemUsesName = seperatedIf[1];
                operator = seperatedIf[3];
                int usesValue = Integer.parseInt(seperatedIf[4]);
                if (!Player.getInstance().checkIfItemUses(itemUsesName,
                        operator, usesValue)) {
                    return false;
                }
                break;
            case "ifSkill":
                String skillName = seperatedIf[1];
                Boolean notSkill = false;
                if (seperatedIf.length == 3 && seperatedIf[2].equals("not")) {
                    notInInventory = true;
                }
                if (notSkill ^ !Player.getInstance().checkIfSkill(skillName)) {
                    return false;
                }
                break;
            case "ifSpell":
                String spellName = seperatedIf[1];
                Boolean notSpell = false;
                if (seperatedIf.length == 3 && seperatedIf[2].equals("not")) {
                    notInInventory = true;
                }
                if (notSpell ^ !Player.getInstance().checkIfSpell(spellName)) {
                    return false;
                }
                break;
            }
        }
        return true;
    }

    /**
     * Gets an event list from file.
     * 
     * @param fileName
     *            the event list file to get events from
     * @return the event list
     * @throws FileNotFoundException
     *             if the file cannot be found
     */
    private static ArrayList<String> getEventListFromFile(String fileName)
            throws FileNotFoundException {
        ArrayList<String> events = new ArrayList<String>();
        Scanner scanner = new Scanner(
                new File("data/events/eventLists/" + fileName + ".txt"));
        while (scanner.hasNextLine()) {
            String tmp = scanner.nextLine();
            if(!tmp.substring(0, 1).equals("#")) {
                events.add(tmp);
            }
        }
        scanner.close();
        return events;

    }
}
