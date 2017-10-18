/**
 * 
 */
package unamedGame.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import unamedGame.Dice;

/**
 * @author c-e-r
 *
 */
public class EventSelector {

	public static void startRandomEventFromFile(String fileName) {
		EventReader.startEvent(
				chooseEventFromList(getEventListFromFile(fileName)));

	}

	public static void startRandomEventFromFileList(List<String> fileNames) {
		EventReader.startEvent(
				chooseEventFromList(getEventListFromFiles(fileNames)));
	}

	private static List<String> getEventListFromFiles(List<String> fileNames) {
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

	private static String chooseEventFromList(List<String> list) {
		return list.get(Dice.roll(list.size()) - 1);

	}

	private static List<String> getEventListFromFile(String fileName) {
		List<String> events = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(
					new File("data/events/eventLists/" + fileName + ".txt"));
			while (scanner.hasNextLine()) {
				events.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return events;

	}
}
