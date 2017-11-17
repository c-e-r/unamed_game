/**
 * 
 */
package unamedgame;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import unamedgame.entities.Enemy;
import unamedgame.entities.Entity;
import unamedgame.entities.Player;
import unamedgame.entities.Entity.EquipmentIndex;
import unamedgame.events.EventReader;
import unamedgame.events.EventSelector;
import unamedgame.input.InputEvent;
import unamedgame.input.InputObserver;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.time.Time;
import unamedgame.ui.Window;
import unamedgame.util.Colors;
import unamedgame.world.World;
import unamedgame.world.WorldTile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class Game {

    /**
     * True if the move menu is open.
     */
    private static boolean onMoveMenu;

    private static Window window = Window.getInstance();

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static final int MAX_SAVES = 9;

    /**
     * Starts the game.
     * 
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        


        Window.getInstance().getFrame().setVisible(true);
        openMainMenu();

    }

    /**
     * Opens the games main menu.
     */
    public static void openMainMenu() {
        Window.clearPlayer();
        Window.clearText();
        Window.clearSide();
        Window.getInstance().swapToMapPane();
        Window.appendSide("1: NEW GAME\n2: LOAD GAME\n");
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    if (command >= 1 && command <= 2) {
                        switch (command) {
                        case 1: // New Game
                            Window.getInstance().removeInputObsever(this);
                            Player.setInstance(null);
                            Time.setInstance(null);
                            openExplorationMenu();
                            break;
                        case 2: // Load Game
                            Window.getInstance().removeInputObsever(this);
                            openLoadMenu(() -> openMainMenu());
                            break;
                        default:
                            break;
                        }
                    }

                }
            }

        });
    }

    /**
     * Opens the games loading menu.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */
    public static void openLoadMenu(Runnable back) {
        Window.clearSide();
        Window.appendSide("LOAD GAME\n");
        Window.appendSide("0: Back\n");
        for (int i = 1; i < MAX_SAVES + 1; i++) {
            boolean[] validChoices = new boolean[MAX_SAVES + 1];
            if (new File("saves/save" + i + ".sav").exists()) {
                validChoices[i] = true;
                try {
                    ObjectInputStream in;
                    in = new ObjectInputStream(new FileInputStream(
                            new File("saves/save" + i + ".sav")));
                    Save save = ((Save) in.readObject());
                    Window.appendSide(i + ": " + save.getSaveText() + "\n");
                    Window.appendSide("    " + save.getSaveNote() + "\n");
                    in.close();
                } catch (IOException | ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Window.appendSide(i + ": --Slot Empty--\n");
                Window.appendSide("\n");
            }
        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    if (command == 0) {
                        window.removeInputObsever(this);
                        back.run();
                    }
                    if (command >= 1 && command <= MAX_SAVES) {
                        File file = new File("saves/save" + command + ".sav");
                        if (file.exists()) {
                            try {
                                ObjectInputStream in = new ObjectInputStream(
                                        new FileInputStream(file));
                                Save save = ((Save) in.readObject());
                                Player.setInstance(save.getPlayer());
                                Time.setInstance(save.getTime());

                                in.close();
                            } catch (IOException | ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Window.getInstance().removeInputObsever(this);
                            Window.getInstance().getMapPane().repaint();
                            openExplorationMenu();
                        } else {
                            Window.appendText("That slot is empty.\n");
                        }

                    }

                }
            }

        });
    }

    /**
     * Opens the save menu.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */

    public static void openSaveMenu(Runnable back) {

        Window.clearSide();
        Window.appendSide("SAVE GAME\n");
        Window.appendSide("0: Back\n");
        for (int i = 1; i < MAX_SAVES + 1; i++) {
            boolean[] validChoices = new boolean[MAX_SAVES + 1];

            if (new File("saves/save" + i + ".sav").exists()) {
                validChoices[i] = true;
                try {
                    ObjectInputStream in;
                    in = new ObjectInputStream(new FileInputStream(
                            new File("saves/save" + i + ".sav")));
                    Save save = ((Save) in.readObject());
                    Window.appendSide(i + ": " + save.getSaveText() + "\n");
                    Window.appendSide("    " + save.getSaveNote() + "\n");
                    in.close();
                } catch (IOException | ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Window.appendSide(i + ": --Slot Empty--\n");
                Window.appendSide("\n");
            }
        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();
                String[] commands = evt.getText().split(" ");
                String saveNote = "";
                for (int j = 1; j < commands.length; j++) {
                    saveNote += commands[j] + " ";
                }
                if (isNumeric(commands[0])) {
                    int command = Integer.parseInt(commands[0]);
                    if (command == 0) {
                        window.removeInputObsever(this);
                        back.run();
                    }
                    if (command >= 1 && command <= MAX_SAVES) {
                        File file = new File("saves/save" + command + ".sav");

                        if (file.exists()) {
                            openSaveOverwriteConfirmMenu(file, back, saveNote);
                            window.removeInputObsever(this);
                        } else {
                            try {
                                ObjectOutputStream out = new ObjectOutputStream(
                                        new FileOutputStream(file));
                                out.writeObject(new Save(saveNote));
                                out.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }
                            Window.getInstance().removeInputObsever(this);
                            openSaveMenu(() -> openSaveMenu(back));
                        }

                    }

                }
            }

        });
    }

    /**
     * Opens the save overwrite confirmation menu.
     * 
     * @param file
     *            the file to overwrite
     * @param back
     *            A Runnable that when run will return to the previous menu.
     * @param saveNote
     *            the save note for the file
     */
    private static void openSaveOverwriteConfirmMenu(File file, Runnable back,
            String saveNote) {
        Window.clearSide();
        Window.appendSide("OVERWRITE SAVE?\n1: Yes\n2: No\n");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    if (command >= 1 && command <= 2) {
                        if (command == 1) {
                            try {
                                ObjectOutputStream out;
                                out = new ObjectOutputStream(
                                        new FileOutputStream(file));
                                out.writeObject(new Save(saveNote));
                                out.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                        window.removeInputObsever(this);
                        back.run();
                    }
                }
            }

        });
    }

    /**
     * Opens the main exploration menu and waits for player input.
     */
    public static void openExplorationMenu() {
        Window.getInstance().swapToMapPane();
        Window.getInstance().getMapPane().repaint();
        Window.clearSide();
        Window.appendSide(
                "1: Explore \n2: Move\n3: Gather\n4: Inventory\n5: Quests \n6: Status\n7: Save\n8: Load\n9: Exit to Main Menu");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                String text = evt.getText();
                int command = -1;
                clearTextField();
                if (isNumeric(text)) {
                    command = Integer.parseInt(text);
                }

                switch (command) {
                case 0: // debug
                    window.removeInputObsever(this);
                    openDebugMenu(() -> openExplorationMenu());
                    break;
                case 1: // explore
                    window.removeInputObsever(this);
                    explore();
                    break;
                case 2: // move
                    window.removeInputObsever(this);
                    openMoveMenu(() -> openExplorationMenu());
                    break;
                case 3: // gather?
                    window.removeInputObsever(this);
                    gather();
                    break;
                case 4: // inventory
                    window.removeInputObsever(this);
                    openInventoryMenu(() -> openExplorationMenu());
                    break;
                case 5: // quest
                    window.removeInputObsever(this);
                    openQuestMenu(() -> openExplorationMenu());
                    break;
                case 6: // status
                    window.removeInputObsever(this);
                    openStatusMenu(() -> openExplorationMenu());
                    break;
                case 7: // save
                    window.removeInputObsever(this);
                    openSaveMenu(() -> openExplorationMenu());
                    break;
                case 8: // load
                    window.removeInputObsever(this);
                    openLoadMenu(() -> openExplorationMenu());
                    break;
                case 9: // main Menu
                    window.removeInputObsever(this);
                    openMainMenu();
                    break;
                default:
                    Window.appendText("Invalid Command\n");
                    break;
                }

            }
        });
    }

    /**
     * Chooses an starts a random event based on the current tiles event list.
     */
    private static void explore() {
        WorldTile playerTile = World.getInstance()
                .getTile(Player.getInstance().getLocation());

        if (playerTile.getLocation() == null) {
            try {
                EventSelector
                        .startRandomEventFromFileList(
                                World.getInstance()
                                        .getTile(Player.getInstance()
                                                .getLocation())
                                        .getEventFiles(),
                                () -> openExplorationMenu());
            } catch (FileNotFoundException e) {
                Window.appendText("ERROR: " + e.getMessage() + "\n");
                Game.openExplorationMenu();
                LOG.error("Event list file not found.", e);
                e.printStackTrace();
            }
        } else {
            openLocationMenu(playerTile, () -> openExplorationMenu());
        }

    }

    private static void openLocationMenu(WorldTile tile, Runnable back) {
        Window.clearSide();
        Window.appendSide(String.format("%-24s%10s%13s\n", "Name", "Weight",
                "Uses Left"));
        Window.appendSide("------------------------------------------------\n");
        Window.appendSide(String.format("0: Back\n"));

        ArrayList<String> events = EventSelector
                .prunedEventListFromFile(tile.getLocation().getEventFile());
        int i = 1;
        for (String event : events) {
            Window.appendSide(i++ + ": " + event + "\n");
        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int eventIndex = -2;
                if (isNumeric(evt.getText())) {
                    eventIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (eventIndex >= 0 && eventIndex < events.size()) {
                    EventReader.startEvent(events.get(eventIndex),
                            () -> openLocationMenu(tile, back));
                    Window.getInstance().removeInputObsever(this);
                } else if (eventIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendSide("Invalid Command\n");

                }
            }
        });

    }

    /**
     * Opens a menu for the player to choose which direction to move.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     * 
     */
    private static void openMoveMenu(Runnable back) {
        onMoveMenu = true;
        Window.clearSide();
        Window.appendSide(
                "1: Northwest\n2: North\n3: Northeast\n4: Southeast\n5: South\n6: Southwest\n");
        Window.appendText("Move where?\n");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    if (command >= 1 && command <= 6) {
                        Player.getInstance().move(command - 1);
                        Window.getInstance().getMapPane().repaint();
                    } else {
                        onMoveMenu = false;
                        window.removeInputObsever(this);
                        openExplorationMenu();
                    }

                }

            }

        });
    }

    /**
     * NOT YET IMPLEMENTED
     */
    private static void gather() {
    }

    /**
     * Opens the out of combat inventory and waits for the player to select an
     * item or return to the exploration menu.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     * 
     */
    public static void openInventoryMenu(Runnable back) {
        Window.clearSide();
        Window.appendSide(String.format("%-24s%10s%13s\n", "Name", "Weight",
                "Uses Left"));
        Window.appendSide("------------------------------------------------\n");
        Window.appendSide(String.format("0: Back\n"));
        int i = 1;
        for (Item item : Player.getInstance().getInventory()) {
            if (i % 2 != 0) {
                Window.appendSideBackground(
                        i++ + ": " + item.getItemInfo() + "\n",
                        new Color(244, 244, 244));

            } else {
                Window.appendSide(i++ + ": " + item.getItemInfo() + "\n");

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int itemIndex = -2;
                if (isNumeric(evt.getText())) {
                    itemIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (itemIndex >= 0 && itemIndex < Player.getInstance()
                        .getInventory().size()) {
                    itemChoiceMenu(itemIndex, () -> openInventoryMenu(back));
                    Window.getInstance().removeInputObsever(this);
                } else if (itemIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendSide("Invalid Command\n");

                }
            }
        });
    }

    /**
     * Shows the player choices for the current item and waits for player input.
     * 
     * @param itemIndex
     *            the index of the item being manipulated
     * @param back
     *            A Runnable that when run will return to the previous menu.
     * 
     */
    public static void itemChoiceMenu(int itemIndex, Runnable back) {
        Window.clearSide();
        Item item = Player.getInstance().getInventoryItem(itemIndex);
        if (item != null) {
            if (item.isEquipped()) {
                Window.appendSide(
                        "0: Back\n1: Use\n2: Inspect\n3: Drop\n4: Uneqip\n");

            } else {
                Window.appendSide(
                        "0: Back\n1: Use\n2: Inspect\n3: Drop\n4: Equip\n");

            }
            Window.getInstance().addInputObsever(new InputObserver() {
                @Override
                public void inputChanged(InputEvent evt) {
                    int command = -1;
                    if (isNumeric(evt.getText())) {
                        command = Integer.parseInt(evt.getText());
                    }

                    switch (command) {
                    case 0: // back
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                        break;
                    case 1: // use
                        if (item.isFieldUse()) {
                            if (item.getMaxUses() == 0) {
                                Window.appendText("That item is not usable.\n");
                            } else {
                                if (item.getUses() <= 0) {
                                    Window.appendText(
                                            "That item is out of uses.\n");

                                } else {
                                    Player.getInstance().applyItemEffects(
                                            Player.getInstance()
                                                    .getInventoryItem(
                                                            itemIndex),
                                            Player.getInstance());
                                    Window.getInstance()
                                            .removeInputObsever(this);
                                    back.run();
                                }

                            }

                        } else {
                            Window.appendText("You cant use that item here.\n");
                        }

                        break;
                    case 2: // inspect
                        Window.appendText(Player.getInstance()
                                .getItemDescription(itemIndex) + "\n");
                        break;
                    case 3: // drop
                        if (!item.isBound()) {
                            if (item.isEquipped()) {
                                Player.getInstance()
                                        .unequipInventoryItem(itemIndex);
                                Window.appendText("You threw away the "
                                        + Player.getInstance()
                                                .removeItemFromInventory(
                                                        itemIndex)
                                                .getName()
                                        + ".\n");
                            } else {
                                Window.appendText("You threw away the "
                                        + Player.getInstance()
                                                .removeItemFromInventory(
                                                        itemIndex)
                                                .getName()
                                        + ".\n");
                                Window.getInstance().removeInputObsever(this);
                            }
                            back.run();
                        } else {
                            Window.appendText("You cant drop that item!\n");
                        }
                        break;
                    case 4: // equip/unequip
                        if (item.isEquippable()) {
                            if (item.isEquipped()) {
                                Player.getInstance()
                                        .unequipInventoryItem(itemIndex);
                                back.run();
                            } else {
                                if (item.getEquipSlot().equals("hand")) {
                                    getHandChoice(item,
                                            () -> itemChoiceMenu(itemIndex,
                                                    back));
                                } else if (item.getEquipSlot().equals("held")) {
                                    getHeldChoice(item,
                                            () -> itemChoiceMenu(itemIndex,
                                                    back));
                                } else {
                                    Player.getInstance()
                                            .equipInventoryItem(itemIndex);
                                    back.run();
                                }

                            }
                            Window.getInstance().removeInputObsever(this);
                        } else {
                            Window.appendText("You cant equip that item!\n");
                        }

                        break;
                    default:
                        Window.appendText("Invalid Command\n");
                        break;
                    }

                }
            });
        } else {
            LOG.error("Somehow you tried to access an item that dosn't exist");
        }
    }

    /**
     * Opens a menu to select which hand to equip an item on.
     * 
     * @param item
     *            the item to equip
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */
    public static void getHandChoice(Item item, Runnable back) {
        Window.clearSide();
        Window.appendSide("0: Back\n1: Left");

        if (Player.getInstance().getEquipment()[EquipmentIndex.LEFT_HAND
                .getValue()] != null) {
            Window.appendSide(" - " + Player.getInstance()
                    .getEquipment()[EquipmentIndex.LEFT_HAND.getValue()]
                            .getName());
        }
        Window.appendSide("\n2: Right");
        if (Player.getInstance().getEquipment()[EquipmentIndex.RIGHT_HAND
                .getValue()] != null) {
            Window.appendSide(" - " + Player.getInstance()
                    .getEquipment()[EquipmentIndex.RIGHT_HAND.getValue()]
                            .getName());
        }
        Window.appendText("Equip to which hand?\n");
        Window.getInstance().addInputObsever(new InputObserver() {

            @Override
            public void inputChanged(InputEvent evt) {
                if (Game.isNumeric(evt.getText())) {
                    int hand = Integer.parseInt(evt.getText());
                    if (hand == 0) {
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                    } else if (hand >= 1 && hand <= 2) {
                        Player.getInstance().equipToHand(hand, item);
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                    } else {
                        Window.appendText("Invalid Choice\n");
                    }
                }
            }

        });
    }

    /**
     * Opens a menu for the player to select which hand to hold an item in.
     * 
     * @param item
     *            the item to equip
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */
    public static void getHeldChoice(Item item, Runnable back) {
        Window.clearSide();
        Window.appendSide("0: Back\n1: Left");

        if (Player.getInstance().getEquipment()[EquipmentIndex.LEFT_HELD
                .getValue()] != null) {
            Window.appendSide(" - " + Player.getInstance()
                    .getEquipment()[EquipmentIndex.LEFT_HELD.getValue()]
                            .getName());
        }
        Window.appendSide("\n2: Right");
        if (Player.getInstance().getEquipment()[EquipmentIndex.RIGHT_HELD
                .getValue()] != null) {
            Window.appendSide(" - " + Player.getInstance()
                    .getEquipment()[EquipmentIndex.RIGHT_HELD.getValue()]
                            .getName());
        }
        Window.appendSide("\n3: Both");
        Window.appendText("Hold in which hand?\n");
        Window.getInstance().addInputObsever(new InputObserver() {

            @Override
            public void inputChanged(InputEvent evt) {
                if (Game.isNumeric(evt.getText())) {
                    int hand = Integer.parseInt(evt.getText());
                    if (hand == 0) {
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                    } else if (hand >= 1 && hand <= 3) {
                        Player.getInstance().equipToHeld(hand, item);
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                    } else {
                        Window.appendText("Invalid Choice\n");
                    }
                }
            }

        });
    }

    public static void openQuestMenu(Runnable back) {

        Window.clearSide();
        Window.appendSide(String.format("Name"));
        Window.appendSide("------------------------------------------------\n");
        Window.appendSide(String.format("0: Back\n"));
        int i = 1;
        ArrayList<Quest> quests = new ArrayList<Quest>(
                Player.getInstance().getQuestLog().values());
        for (Quest quest : quests) {
            if (i % 2 != 0) {
                if (quest.isCompleted()) {
                    Window.appendSide(
                            i++ + ": " + quest.getQuestName()
                                    + " (Completed)\n",
                            new Color(100, 100, 100), new Color(244, 244, 244));
                } else {
                    Window.appendSide(i++ + ": " + quest.getQuestName() + "\n",
                            Color.BLACK, new Color(244, 244, 244));
                }

            } else {
                if (quest.isCompleted()) {
                    Window.appendSide(
                            i++ + ": " + quest.getQuestName()
                                    + " (Completed)\n",
                            new Color(100, 100, 100));
                } else {
                    Window.appendSide(i++ + ": " + quest.getQuestName() + "\n");
                }
            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int questIndex = -2;
                if (isNumeric(evt.getText())) {
                    questIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (questIndex >= 0 && questIndex < Player.getInstance()
                        .getQuestLog().values().size()) {
                    questChoiceMenu(quests.get(questIndex),
                            () -> openQuestMenu(back));
                    Window.getInstance().removeInputObsever(this);
                } else if (questIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendSide("Invalid Command\n");

                }
            }
        });

    }

    public static void questChoiceMenu(Quest quest, Runnable back) {
        Window.clearSide();
        Window.clearText();
        Window.appendSide("0: Back\n");
        Window.appendText(quest.getLogInfo());
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int command = -1;
                if (isNumeric(evt.getText())) {
                    command = Integer.parseInt(evt.getText());
                }

                switch (command) {
                case 0: // back
                    Window.clearText();
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                    break;
                default:
                    Window.appendText("Invalid Command\n");
                    break;
                }

            }
        });

    }

    /**
     * Opens the status menu.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */
    private static void openStatusMenu(Runnable back) {
        window.swapToPlayerPane();
        Window.clearPlayer();
        Window.appendPlayer(Player.getInstance().getStatus());

        Window.clearSide();
        Window.appendSide(
                "0: Back\n1: View Perks\n2: Spend Stat Points\n3: Spend Perk Points\n");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                clearTextField();
                String text = evt.getText();
                int command = -1;
                if (isNumeric(text)) {
                    command = Integer.parseInt(text);
                }
                switch (command) {
                case 0: // Back
                    window.removeInputObsever(this);
                    window.swapToMapPane();

                    openExplorationMenu();
                    break;
                case 1: // View Perks
                    Window.appendText("This command is not implemented yet\n");
                    break;
                case 2: // Spend Stat Points
                    window.removeInputObsever(this);
                    openStatPointMenu(() -> openStatusMenu(back));
                    break;
                case 3: // Spend Perk Points
                    Window.appendText("This command is not implemented yet\n");
                    break;
                default:
                    Window.appendText("Invalid Command\n");
                    break;
                }
            }

        });
    }

    /**
     * Opens the stat point spending menu.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */
    private static void openStatPointMenu(Runnable back) {
        Window.clearSide();
        Window.clearText();
        Window.appendSide(
                "0: Cancel\n1: Finish\n2: + Vitality\n3: + Strength\n4: + Dexterity\n5: + Intellect\n6: + Spirit\n7: + Luck\n");
        Player player = Player.getInstance();
        int curVitality = player.getVitality();
        int curStrength = player.getStrength();
        int curDexterity = player.getDexterity();
        int curIntellect = player.getIntellect();
        int curSpirit = player.getSpirit();
        int curLuck = player.getLuck();

        player.setNewVitality(curVitality);
        player.setNewStrength(curStrength);
        player.setNewDexterity(curDexterity);
        player.setNewIntellect(curIntellect);
        player.setNewSpirit(curSpirit);
        player.setNewLuck(curLuck);
        player.setNewStatPoints(player.getStatPoints());

        Window.appendText(
                "Points Remaining: " + player.getNewStatPoints() + "\n");
        Window.appendText("STAT CURRENT NEW COST\n");

        Window.appendText("VIT   " + curVitality + " -----> ");
        if (player.getNewVitality() > curVitality) {
            Window.appendText(Integer.toString(player.getNewVitality()),
                    Colors.STAT_POINT_INCREASE);
        } else {
            Window.appendText(Integer.toString(player.getNewVitality()));
        }
        if (Math.ceil((double) player.getNewVitality() / 5) > player
                .getNewStatPoints()) {
            Window.appendText(
                    "   (" + (int) Math
                            .ceil((double) player.getNewVitality() / 5) + ")\n",
                    Colors.STAT_POINT_CANT_AFFORD);
        } else {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewVitality() / 5)
                    + ")\n");
        }

        Window.appendText("STR   " + curStrength + " -----> ");
        if (player.getNewStrength() > curStrength) {
            Window.appendText(Integer.toString(player.getNewStrength()),
                    Colors.STAT_POINT_INCREASE);
        } else {
            Window.appendText(Integer.toString(player.getNewStrength()));
        }
        if (Math.ceil((double) player.getNewStrength() / 5) > player
                .getNewStatPoints()) {
            Window.appendText(
                    "   (" + (int) Math
                            .ceil((double) player.getNewStrength() / 5) + ")\n",
                    Colors.STAT_POINT_CANT_AFFORD);
        } else {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewStrength() / 5)
                    + ")\n");
        }

        Window.appendText("DEX   " + curDexterity + " -----> ");
        if (player.getNewDexterity() > curDexterity) {
            Window.appendText(Integer.toString(player.getNewDexterity()),
                    Colors.STAT_POINT_INCREASE);
        } else {
            Window.appendText(Integer.toString(player.getNewDexterity()));
        }
        if (Math.ceil((double) player.getNewDexterity() / 5) > player
                .getNewStatPoints()) {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewDexterity() / 5)
                    + ")\n", Colors.STAT_POINT_CANT_AFFORD);
        } else {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewDexterity() / 5)
                    + ")\n");
        }

        Window.appendText("INT   " + curIntellect + " -----> ");
        if (player.getNewIntellect() > curIntellect) {
            Window.appendText(Integer.toString(player.getNewIntellect()),
                    Colors.STAT_POINT_INCREASE);
        } else {
            Window.appendText(Integer.toString(player.getNewIntellect()));
        }
        if (Math.ceil((double) player.getNewIntellect() / 5) > player
                .getNewStatPoints()) {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewIntellect() / 5)
                    + ")\n", Colors.STAT_POINT_CANT_AFFORD);
        } else {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewIntellect() / 5)
                    + ")\n");
        }

        Window.appendText("SPR   " + curSpirit + " -----> ");
        if (player.getNewSpirit() > curSpirit) {
            Window.appendText(Integer.toString(player.getNewSpirit()),
                    Colors.STAT_POINT_INCREASE);
        } else {
            Window.appendText(Integer.toString(player.getNewSpirit()));
        }
        if (Math.ceil((double) player.getNewSpirit() / 5) > player
                .getNewStatPoints()) {
            Window.appendText(
                    "   (" + (int) Math.ceil((double) player.getNewSpirit() / 5)
                            + ")\n",
                    Colors.STAT_POINT_CANT_AFFORD);
        } else {
            Window.appendText(
                    "   (" + (int) Math.ceil((double) player.getNewSpirit() / 5)
                            + ")\n");
        }

        Window.appendText("LCK   " + curLuck + " -----> ");
        if (player.getNewLuck() > curLuck) {
            Window.appendText(Integer.toString(player.getNewLuck()),
                    Colors.STAT_POINT_INCREASE);
        } else {
            Window.appendText(Integer.toString(player.getNewLuck()));
        }
        if (Math.ceil((double) player.getNewLuck() / 5) > player
                .getNewStatPoints()) {
            Window.appendText("   ("
                    + (int) Math.ceil((double) player.getNewLuck() / 5) + ")\n",
                    Colors.STAT_POINT_CANT_AFFORD);
        } else {
            Window.appendText(
                    "   (" + (int) Math.ceil((double) player.getNewLuck() / 5)
                            + ")\n");
        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                int cost;
                boolean print = true;

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    switch (command) {
                    case 0:
                        window.removeInputObsever(this);
                        print = false;
                        back.run();
                        break;
                    case 1:
                        player.setVitality(player.getNewVitality());
                        player.setStrength(player.getNewStrength());
                        player.setDexterity(player.getNewDexterity());
                        player.setIntellect(player.getNewIntellect());
                        player.setSpirit(player.getNewSpirit());
                        player.setLuck(player.getNewLuck());

                        player.setStatPoints(player.getNewStatPoints());

                        player.recalculateStats();

                        window.removeInputObsever(this);
                        print = false;

                        back.run();
                        break;
                    case 2:
                        cost = (int) Math
                                .ceil((double) player.getNewVitality() / 5);
                        if (player.getNewStatPoints() >= cost) {
                            player.setNewVitality(player.getNewVitality() + 1);
                            player.setNewStatPoints(
                                    player.getNewStatPoints() - cost);
                        }
                        break;
                    case 3:
                        cost = (int) Math
                                .ceil((double) player.getNewStrength() / 5);
                        if (player.getNewStatPoints() >= cost) {
                            player.setNewStrength(player.getNewStrength() + 1);
                            player.setNewStatPoints(
                                    player.getNewStatPoints() - cost);
                        }
                        break;
                    case 4:
                        cost = (int) Math
                                .ceil((double) player.getNewDexterity() / 5);
                        if (player.getNewStatPoints() >= cost) {
                            player.setNewDexterity(
                                    player.getNewDexterity() + 1);
                            player.setNewStatPoints(
                                    player.getNewStatPoints() - cost);
                        }
                        break;
                    case 5:
                        cost = (int) Math
                                .ceil((double) player.getNewIntellect() / 5);
                        if (player.getNewStatPoints() >= cost) {
                            player.setNewIntellect(
                                    player.getNewIntellect() + 1);
                            player.setNewStatPoints(
                                    player.getNewStatPoints() - cost);
                        }
                        break;
                    case 6:
                        cost = (int) Math
                                .ceil((double) player.getNewSpirit() / 5);
                        if (player.getNewStatPoints() >= cost) {
                            player.setNewSpirit(player.getNewSpirit() + 1);
                            player.setNewStatPoints(
                                    player.getNewStatPoints() - cost);
                        }
                        break;
                    case 7:
                        cost = (int) Math
                                .ceil((double) player.getNewLuck() / 5);
                        if (player.getNewStatPoints() >= cost) {
                            player.setNewLuck(player.getNewLuck() + 1);
                            player.setNewStatPoints(
                                    player.getNewStatPoints() - cost);
                        }
                        break;

                    default:
                        break;

                    }

                }

                Window.clearText();
                if (print) {
                    Window.appendText("Points Remaining: "
                            + player.getNewStatPoints() + "\n");
                    Window.appendText("STAT CURRENT NEW COST\n");

                    Window.appendText("VIT   " + curVitality + " -----> ");
                    if (player.getNewVitality() > curVitality) {
                        Window.appendText(
                                Integer.toString(player.getNewVitality()),
                                Colors.STAT_POINT_INCREASE);
                    } else {
                        Window.appendText(
                                Integer.toString(player.getNewVitality()));
                    }
                    if (Math.ceil((double) player.getNewVitality() / 5) > player
                            .getNewStatPoints()) {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewVitality() / 5)
                                + ")\n", Colors.STAT_POINT_CANT_AFFORD);
                    } else {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewVitality() / 5)
                                + ")\n");
                    }

                    Window.appendText("STR   " + curStrength + " -----> ");
                    if (player.getNewStrength() > curStrength) {
                        Window.appendText(
                                Integer.toString(player.getNewStrength()),
                                Colors.STAT_POINT_INCREASE);
                    } else {
                        Window.appendText(
                                Integer.toString(player.getNewStrength()));
                    }
                    if (Math.ceil((double) player.getNewStrength() / 5) > player
                            .getNewStatPoints()) {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewStrength() / 5)
                                + ")\n", Colors.STAT_POINT_CANT_AFFORD);
                    } else {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewStrength() / 5)
                                + ")\n");
                    }

                    Window.appendText("DEX   " + curDexterity + " -----> ");
                    if (player.getNewDexterity() > curDexterity) {
                        Window.appendText(
                                Integer.toString(player.getNewDexterity()),
                                Colors.STAT_POINT_INCREASE);
                    } else {
                        Window.appendText(
                                Integer.toString(player.getNewDexterity()));
                    }
                    if (Math.ceil(
                            (double) player.getNewDexterity() / 5) > player
                                    .getNewStatPoints()) {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewDexterity() / 5)
                                + ")\n", Colors.STAT_POINT_CANT_AFFORD);
                    } else {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewDexterity() / 5)
                                + ")\n");
                    }

                    Window.appendText("INT   " + curIntellect + " -----> ");
                    if (player.getNewIntellect() > curIntellect) {
                        Window.appendText(
                                Integer.toString(player.getNewIntellect()),
                                Colors.STAT_POINT_INCREASE);
                    } else {
                        Window.appendText(
                                Integer.toString(player.getNewIntellect()));
                    }
                    if (Math.ceil(
                            (double) player.getNewIntellect() / 5) > player
                                    .getNewStatPoints()) {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewIntellect() / 5)
                                + ")\n", Colors.STAT_POINT_CANT_AFFORD);
                    } else {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewIntellect() / 5)
                                + ")\n");
                    }

                    Window.appendText("SPR   " + curSpirit + " -----> ");
                    if (player.getNewSpirit() > curSpirit) {
                        Window.appendText(
                                Integer.toString(player.getNewSpirit()),
                                Colors.STAT_POINT_INCREASE);
                    } else {
                        Window.appendText(
                                Integer.toString(player.getNewSpirit()));
                    }
                    if (Math.ceil((double) player.getNewSpirit() / 5) > player
                            .getNewStatPoints()) {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewSpirit() / 5)
                                + ")\n", Colors.STAT_POINT_CANT_AFFORD);
                    } else {
                        Window.appendText("   ("
                                + (int) Math.ceil(
                                        (double) player.getNewSpirit() / 5)
                                + ")\n");
                    }

                    Window.appendText("LCK   " + curLuck + " -----> ");
                    if (player.getNewLuck() > curLuck) {
                        Window.appendText(Integer.toString(player.getNewLuck()),
                                Colors.STAT_POINT_INCREASE);
                    } else {
                        Window.appendText(
                                Integer.toString(player.getNewLuck()));
                    }
                    if (Math.ceil((double) player.getNewLuck() / 5) > player
                            .getNewStatPoints()) {
                        Window.appendText("   ("
                                + (int) Math
                                        .ceil((double) player.getNewLuck() / 5)
                                + ")\n", Colors.STAT_POINT_CANT_AFFORD);
                    } else {
                        Window.appendText("   ("
                                + (int) Math
                                        .ceil((double) player.getNewLuck() / 5)
                                + ")\n");
                    }

                }

            }

        });
    }

    /**
     * Opens the debug menu.
     * 
     * @param back
     *            A Runnable that when run will return to the previous menu.
     */
    private static void openDebugMenu(Runnable back) {
        Window.clearSide();
        Window.appendSide(
                "0: Back \naddItem <itemName>\ngainExp <amount>\nstartEvent <eventName>\naddSkill <skillName>\naddSpell <spellName>\nstartCombat <enemyName>\n");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                String[] command = evt.getText().split(" ");
                switch (command[0]) {
                case "0":
                    window.removeInputObsever(this);
                    back.run();
                    break;
                case "addItem":

                    Item newItem = Item.buildItem(command[1]);
                    if (newItem != null) {
                        Player.getInstance().addItemToInventory(newItem);
                        Window.appendText(
                                command[1] + " added to inventory.\n");
                    } else {
                        Window.appendText(
                                "ERROR: Somthing went wrong adding an item to your inventory. See game.log for more information.\n");

                    }
                    break;
                case "gainExp":
                    if (isNumeric(command[1])) {
                        Player.getInstance()
                                .gainExp(Integer.parseInt(command[1]));
                        Window.appendText("You gained " + command[1] + "exp\n");
                    } else {
                        Window.appendText("That command needs a number.\n");
                    }
                    break;
                case "startEvent":
                    window.removeInputObsever(this);
                    EventReader.startEvent(command[1],
                            () -> openExplorationMenu());
                    break;
                case "addSkill":

                    Skill newSkill = Skill.buildSkill(command[1]);
                    if (newSkill != null) {
                        Player.getInstance().addInnateSkill(newSkill);
                        Window.appendText(
                                command[1] + " added to innate skill list.\n");
                    } else {
                        Window.appendText(
                                "ERROR: Somthing went wrong while creating a skill. See game.log for more information.\n");
                    }

                    break;
                case "addSpell":
                    Spell newSpell = Spell.buildSpell(command[1]);
                    if (newSpell != null) {
                        Player.getInstance().addKnownSpell(newSpell);
                        Window.appendText(
                                command[1] + " added to known spells list.\n");
                    } else {
                        Window.appendText(
                                "ERROR: Somthing went wrong while creating a spell. See game.log for more information.\n");
                    }
                    break;
                case "startCombat":
                    window.removeInputObsever(this);
                    Enemy newEnemy = Enemy.buildEnemy(command[1]);
                    if (newEnemy != null) {
                        new Combat(newEnemy, () -> openExplorationMenu());
                    } else {
                        Window.appendText(
                                "ERROR: Somthing went wrong while creating an enemy. See game.log for more info.\n");
                    }

                    break;
                case "time":
                    window.removeInputObsever(this);
                    Time.getInstance().passTime(1);
                    Window.appendText("Time passed.\n");
                    back.run();
                    break;
                default:
                    Window.appendText("Invalid debug command\n");
                    break;
                }

            }

        });
    }

    public static void openLootMenu(Entity entity, Runnable back) {
        Window.clearSide();
        Window.appendSide(
                capitalizeFirstLetter(entity.getUseName() + "'s inventory\n"));
        Window.appendSide(String.format("%-24s%10s%13s", "Name", "Weight",
                "Uses Left\n"));
        Window.appendSide("------------------------------------------------\n");
        Window.appendSide(String.format("0: Back\n"));
        int i = 1;
        for (Item item : entity.getInventory()) {
            if (i % 2 != 0) {
                Window.appendSideBackground(
                        i++ + ": " + item.getItemInfo() + "\n",
                        new Color(244, 244, 244));

            } else {
                Window.appendSide(i++ + ": " + item.getItemInfo());

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int itemIndex = -2;
                if (isNumeric(evt.getText())) {
                    itemIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (itemIndex >= 0
                        && itemIndex < entity.getInventory().size()) {
                    lootChoiceMenu(entity, itemIndex,
                            () -> openLootMenu(entity, back));
                    Window.getInstance().removeInputObsever(this);
                } else if (itemIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendText("Invalid Command\n");

                }
            }
        });
    }

    public static void lootChoiceMenu(Entity entity, int itemIndex,
            Runnable back) {
        Window.clearSide();
        Item item = entity.getInventoryItem(itemIndex);
        if (item != null) {
            Window.appendSide("0: Back\n1: Take\n2: Inspect\n");
            Window.getInstance().addInputObsever(new InputObserver() {
                @Override
                public void inputChanged(InputEvent evt) {
                    int command = -1;
                    if (isNumeric(evt.getText())) {
                        command = Integer.parseInt(evt.getText());
                    }

                    switch (command) {
                    case 0: // back
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                        break;
                    case 1: // take
                        entity.removeItemFromInventory(itemIndex);
                        Player.getInstance().addItemToInventory(item);
                        Window.appendText(capitalizeFirstLetter(item.getName())
                                + " taken.\n");
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                        break;
                    case 2: // inspect
                        Window.appendText(
                                entity.getItemDescription(itemIndex) + "\n");
                        break;
                    default:
                        Window.appendText("Invalid Command\n");
                        break;
                    }

                }
            });
        } else {
            LOG.error("Somehow you tried to access an item that dosn't exist");
        }
    }

    public static void openShopMenu(Runnable back, String fileName,
            String shopName) {
        Window.clearSide();
        Window.appendSide(shopName + "\n");
        Window.appendSide("0: Back\n1: Buy\n2: Sell\n");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    if (command >= 0 && command <= 2) {
                        switch (command) {
                        case 0:
                            Window.getInstance().removeInputObsever(this);
                            back.run();
                            break;
                        case 1:
                            Window.getInstance().removeInputObsever(this);
                            openBuyMenu(() -> openShopMenu(back, fileName,
                                    shopName), fileName, shopName);
                            break;
                        case 2:
                            Window.getInstance().removeInputObsever(this);
                            openSellMenu(() -> openShopMenu(back, fileName,
                                    shopName), shopName);
                            break;
                        default:
                            Window.appendText("Invalid command");
                            break;
                        }

                    }
                }
            }

        });
    }

    public static void openSellMenu(Runnable back, String shopName) {
        Window.clearSide();
        Window.appendSide(shopName + "\n");
        Window.appendSide(String.format("%-24s%10s%13s\n", "Name", "Weight",
                "Uses Left"));
        Window.appendSide("------------------------------------------------\n");
        Window.appendSide(String.format("0: Back\n"));
        int i = 1;
        for (Item item : Player.getInstance().getInventory()) {
            if (i % 2 != 0) {
                Window.appendSideBackground(
                        i++ + ": " + item.getItemInfo() + "\n",
                        new Color(244, 244, 244));

            } else {
                Window.appendSide(i++ + ": " + item.getItemInfo() + "\n");

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int itemIndex = -2;
                if (isNumeric(evt.getText())) {
                    itemIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (itemIndex >= 0 && itemIndex < Player.getInstance()
                        .getInventory().size()) {
                    sellChoiceMenu(itemIndex,
                            () -> openSellMenu(back, shopName));
                    Window.getInstance().removeInputObsever(this);
                } else if (itemIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendSide("Invalid Command\n");

                }
            }
        });
    }

    private static void openSellConfirm(Runnable back, int itemIndex,
            Item item) {
        Window.clearSide();
        Window.appendSide("Are you sure you would like to sell "
                + capitalizeFirstLetter(item.getName()) + "?\n1: Yes\n2: No\n");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {

                clearTextField();

                if (isNumeric(evt.getText())) {
                    int command = Integer.parseInt(evt.getText());
                    if (command >= 1 && command <= 2) {
                        if (command == 1) {

                            Player.getInstance()
                                    .removeItemFromInventory(itemIndex);
                            Player.getInstance()
                                    .addCurrency(item.getValue() / 2);
                            Window.appendText(
                                    capitalizeFirstLetter(item.getName())
                                            + " has been sold.\n");

                        }
                        window.removeInputObsever(this);
                        back.run();
                    }
                }
            }

        });
    }

    public static void openBuyMenu(Runnable back, String fileName,
            String shopName) {

        Window.clearSide();
        Window.appendSide(shopName + "\n");
        Window.appendSide(String.format("%-19s%5s%10s%13s", "Name", "Value",
                "Weight", "Uses Left\n"));
        Window.appendSide("------------------------------------------------\n");
        Window.appendSide(String.format("0: Back\n"));
        int i = 1;
        ArrayList<Item> items = new ArrayList<Item>();
        try {
            Scanner scanner = new Scanner(
                    new File("data/shops/" + fileName + ".txt"));
            while (scanner.hasNextLine()) {
                String tmp = scanner.nextLine();
                if (!tmp.substring(0, 1).equals("#")) {
                    items.add(Item.buildItem(tmp));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Item item : items) {
            if (item != null) {
                LOG.error(
                        "Somthing has gone wrong generating items from shop file \""
                                + fileName
                                + ".txt\". Please check that item name are spelt correctly and that all are in the items forlder");
            }
            if (i % 2 != 0) {
                Window.appendSideBackground(
                        i++ + ": " + item.getItemInfo() + "\n\t"
                                + item.getDescription() + "\n",
                        new Color(244, 244, 244));

            } else {
                Window.appendSide(i++ + ": " + item.getItemInfo() + "\n\t"
                        + item.getDescription() + "\n");

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int itemIndex = -2;
                if (isNumeric(evt.getText())) {
                    itemIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (itemIndex >= 0 && itemIndex < items.size()) {
                    buyChoiceMenu(Item.buildItem(items.get(itemIndex).getId()),
                            () -> openBuyMenu(back, fileName, shopName));
                    Window.getInstance().removeInputObsever(this);
                } else if (itemIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendText("Invalid Command\n");

                }
            }
        });
    }

    public static void buyChoiceMenu(Item item, Runnable back) {
        Window.clearSide();
        if (item != null) {
            Window.appendSide(capitalizeFirstLetter(item.getName()));
            Window.appendSide("0: Back\n1: Buy\n2: Inspect\n");
            Window.getInstance().addInputObsever(new InputObserver() {
                @Override
                public void inputChanged(InputEvent evt) {
                    int command = -1;
                    if (isNumeric(evt.getText())) {
                        command = Integer.parseInt(evt.getText());
                    }

                    switch (command) {
                    case 0: // back
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                        break;
                    case 1: // buy
                        if (Player.getInstance().canAfford(item.getValue())) {
                            Player.getInstance().addItemToInventory(item);
                            Player.getInstance()
                                    .removeCurrency(item.getValue());
                            Window.appendText(
                                    capitalizeFirstLetter(item.getName())
                                            + " bought.\n");
                            Window.getInstance().removeInputObsever(this);
                            back.run();
                        } else {
                            Window.appendText("You cant afford that item!\n");
                        }

                        break;
                    case 2: // inspect
                        Window.appendText(item.getDescription() + "\n");
                        break;
                    default:
                        Window.appendText("Invalid Command\n");
                        break;
                    }

                }
            });
        } else {
            LOG.error("Somehow you tried to access an item that dosn't exist");
        }
    }

    public static void sellChoiceMenu(int itemIndex, Runnable back) {
        Item item = Player.getInstance().getInventoryItem(itemIndex);
        Window.clearSide();
        if (item != null) {
            Window.appendSide(capitalizeFirstLetter(item.getName()));
            Window.appendSide("0: Back\n1: Sell\n2: Inspect\n");
            Window.getInstance().addInputObsever(new InputObserver() {
                @Override
                public void inputChanged(InputEvent evt) {
                    int command = -1;
                    if (isNumeric(evt.getText())) {
                        command = Integer.parseInt(evt.getText());
                    }

                    switch (command) {
                    case 0: // back
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                        break;
                    case 1: // sell
                        if (!item.isBound()) {
                            openSellConfirm(back, itemIndex, item);
                            Window.getInstance().removeInputObsever(this);
                        } else {
                            Window.appendText("You cant sell that item!\n");
                        }

                        break;
                    case 2: // inspect
                        Window.appendText(item.getDescription() + "\n");
                        break;
                    default:
                        Window.appendText("Invalid Command\n");
                        break;
                    }

                }
            });
        } else {
            LOG.error("Somehow you tried to access an item that dosn't exist");
        }
    }

    public static void openDeathMenu() {
        Window.clearSide();
        Window.appendSide("1: Load\n2: Return to Main Menu\n");
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int command = -1;
                if (isNumeric(evt.getText())) {
                    command = Integer.parseInt(evt.getText());
                }
                switch (command) {
                case 1: // Load
                    window.removeInputObsever(this);
                    openLoadMenu(() -> openDeathMenu());
                    break;
                case 2: // Main Menu
                    window.removeInputObsever(this);
                    openMainMenu();
                    break;
                default:
                    Window.appendText("Invalid Command\n");
                    break;
                }

            }
        });
    }

    /**
     * Returns true if the given string contains only numeric characters.
     * 
     * @param str
     *            the string to check
     * @return if the string contains only numeric characters
     */
    public static boolean isNumeric(String str) {
        return str.matches("\\d+");

    }

    /**
     * Returns true if the given string contains only numeric characters and a
     * decimal point.
     * 
     * @param str
     *            the string to check
     * @return if the string contains only numeric characters
     */
    public static boolean isDoubleNumeric(String str) {
        return str.matches("\\d+.\\d+");

    }

    /**
     * Accepts a string and return the string with the first letter capitalized.
     * 
     * @param string
     *            the string to capitalize the first letter of
     * @return the given string with the first letter capitalized
     */
    public static String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Clears the text windows textfield.
     */
    public static void clearTextField() {
        Window.clearTextField();
    }

    /**
     * Returns true if on the move menu.
     * 
     * @return the onMoveMenu
     */
    public static boolean isOnMoveMenu() {
        return onMoveMenu;
    }

}
