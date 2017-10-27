/**
 * 
 */
package unamedgame.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import unamedgame.Dice;

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
    public static void startRandomEventFromFile(String fileName)
            throws FileNotFoundException {
        EventReader.startEvent(
                chooseEventFromList(getEventListFromFile(fileName)));

    }

    /**
     * Starts a random event from a list of event file names.
     * 
     * @param fileNames
     *            the event filesnames list
     * @throws FileNotFoundException
     *             if the event files cannot be found
     */
    public static void startRandomEventFromFileList(List<String> fileNames)
            throws FileNotFoundException {
        EventReader.startEvent(
                chooseEventFromList(getEventListFromFiles(fileNames)));
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
    private static List<String> getEventListFromFiles(List<String> fileNames)
            throws FileNotFoundException {
        List<String> newList = new ArrayList<String>();
        List<String> temp;
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
     * @param list the list to choose from
     * @return the chosen event
     */
    private static String chooseEventFromList(List<String> list) {
        return list.get(Dice.roll(list.size()) - 1);

    }

    /**
     * Gets an event list from file.
     * @param fileName the event list file to get events from
     * @return the event list
     * @throws FileNotFoundException if the file cannot be found
     */
    private static List<String> getEventListFromFile(String fileName)
            throws FileNotFoundException {
        List<String> events = new ArrayList<String>();
        Scanner scanner = new Scanner(
                new File("data/events/eventLists/" + fileName + ".txt"));
        while (scanner.hasNextLine()) {
            events.add(scanner.nextLine());
        }
        scanner.close();
        return events;

    }
}
