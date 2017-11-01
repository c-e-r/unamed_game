/**
 * 
 */
package unamedgame;

import java.awt.Color;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import unamedgame.entities.Enemy;
import unamedgame.entities.Player;
import unamedgame.events.EventReader;
import unamedgame.input.InputEvent;
import unamedgame.input.InputObserver;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.time.Time;
import unamedgame.ui.Window;

/**
 * Handles the games combat loop.
 * 
 * @author c-e-r
 * @version 0.0.1
 *
 */
public class Combat {

    /**
     * An int to be given to the combat constructor when starting combat that
     * should return to the exploration menu when done.
     */
    public static final int FROM_EXPLORATION = 1;
    /**
     * An int to be given to the combat constructor when starting combat that
     * should return to an event when done.
     */
    public static final int FROM_EVENT = 2;

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static boolean inCombat;

    private static final String OFFENSIVE = "offensive";
    private static final String DEBUFF = "debuff";
    private static final String BUFF = "buff";
    private static final String HEAL = "heal";

    private Player player;
    private Enemy enemy;
    private int returnPoint;
    private Runnable back;

    /**
     * Starts a combat loop between the player and given Enemy.
     * 
     * @param enemy
     *            the Enemy to start combat with
     * @param returnPoint
     *            the location to go after combat as an int
     */
    public Combat(Enemy enemy, Runnable back) {
        this.back = back;
        player = Player.getInstance();
        this.enemy = enemy;
        inCombat = true;

        Window.getInstance().swapToPlayerPane();
        Window.clearPane(Window.getInstance().getTextPane());
        Window.appendToPane(Window.getInstance().getTextPane(),
                enemy.getDescription());

        newTurn();
    }

    /**
     * Starts a new combat turn.
     */
    private void newTurn() {
        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getSidePane(),
                "1: Attack \n2: Skill \n3: Magic\n4: Item \n5: Escape");
        Window.clearPane(Window.getInstance().getPlayerPane());
        Window.appendToPane(Window.getInstance().getPlayerPane(),
                player.getStatus());

        getCombatInput();
    }

    /**
     * Displays player turn choices and waits for input.
     */
    private void getCombatInput() {
        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getSidePane(),
                "1: Attack\n2: Skill\n3: Magic\n4: Item\n5: Escape");

        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                String text = evt.getText();
                int command = -1;
                if (Game.isNumeric(text)) {
                    command = Integer.parseInt(text);
                    switch (command) {
                    case 1: // attack
                        combatTurn(1, -1);
                        Window.getInstance().removeInputObsever(this);
                        break;
                    case 2: // skill
                        skillSelection(() -> getCombatInput());
                        Window.getInstance().removeInputObsever(this);
                        break;
                    case 3: // magic
                        spellSelection(() -> getCombatInput());
                        Window.getInstance().removeInputObsever(this);
                        break;
                    case 4: // item
                        combatInventory(() -> getCombatInput());
                        Window.getInstance().removeInputObsever(this);
                        break;
                    case 5: // escape
                        Window.getInstance().removeInputObsever(this);
                        break;

                    default:
                        Window.appendToPane(Window.getInstance().getTextPane(),
                                "Invalid Command");
                        break;
                    }
                    if (command >= 1 && command <= 5) {

                    } else {

                    }
                } else {
                    Window.appendToPane(Window.getInstance().getTextPane(),
                            "Invalid Command");
                }

            }
        });
    }

    /**
     * Does a specific action depended on the command and index given to it.
     * 
     * @param command
     *            the command to execute
     * @param index
     *            the index for commands that require one
     */
    private void playerAction(int command, int index) {
        switch (command) {
        case 1: // attack
            player.attack(enemy);

            break;
        case 2: // skill
            player.useSkill(index, enemy);
            break;
        case 3: // spell
            player.useSpell(index, enemy);
            break;
        case 4: // item
            if (player.getInventoryItem(index).isOffensive()) {
                enemy.applyItemEffects(player.getInventoryItem(index), player);
            } else {
                player.applyItemEffects(player.getInventoryItem(index), player);
            }
            break;
        case 5: // escape
            break;
        default:
            Window.addToPane(Window.getInstance().getTextPane(),
                    "Invalid Command");
            break;
        }
    }

    /**
     * Does a specific action for the enemy depending on the command and index
     * given to it.
     * 
     * @param command
     *            the command to execute
     * @param index
     *            the index for commands that require one
     */
    private void enemyAction(int command, int index) {
        switch (command) {
        case 1: // attack
            enemy.attack(player);

            break;
        case 2: // skill
            enemy.useSkill(index, player);
            break;
        case 3: // spell
            enemy.useSpell(index, player);
            break;
        case 4: // item
            if (enemy.getInventoryItem(index).isOffensive()) {
                player.applyItemEffects(enemy.getInventoryItem(index), enemy);
            } else {
                enemy.applyItemEffects(enemy.getInventoryItem(index), enemy);
            }
            break;
        case 5: // escape
            break;
        default:
            Window.addToPane(Window.getInstance().getTextPane(),
                    "Invalid Command");
            break;
        }
    }

    /**
     * Prints choices and sets an observer to wait for player input.
     * 
     * @param back
     *            a Runnable that will open the menu to return to when run
     */
    private void skillSelection(Runnable back) {
        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getSidePane(),
                String.format("%-14s%6s%18s", "Name", "Cost", "Description"));
        Window.appendToPane(Window.getInstance().getSidePane(),
                "------------------------------------------------");
        Window.appendToPane(Window.getInstance().getSidePane(),
                String.format("0: Back"));
        int i = 1;
        for (Skill skill : Player.getInstance().getCombinedSkills()) {
            if (i % 2 != 0) {
                Window.appendToPaneBackground(
                        Window.getInstance().getSidePane(),
                        String.format("%-14s%5d%25s",
                                i++ + ": "
                                        + Game.capitalizeFirstLetter(
                                                skill.getName()),
                                skill.getStaminaCost(), skill.getDescription()),
                        new Color(244, 244, 244));

            } else {
                Window.appendToPane(Window.getInstance().getSidePane(),
                        String.format("%-14s%5d%25s",
                                i++ + ": "
                                        + Game.capitalizeFirstLetter(
                                                skill.getName()),
                                skill.getStaminaCost(),
                                skill.getDescription()));

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int skillIndex = -2;
                if (Game.isNumeric(evt.getText())) {
                    skillIndex = Integer.parseInt(evt.getText()) - 1;
                    System.out.println(skillIndex);
                }
                if (skillIndex >= 0 && skillIndex < Player.getInstance()
                        .getCombinedSkills().size()) {
                    if (player.getCurrentStamina() >= player.getCombinedSkills()
                            .get(skillIndex).getStaminaCost()) {

                        combatTurn(2, skillIndex);
                        Window.getInstance().removeInputObsever(this);
                    } else {

                        Window.appendToPane(Window.getInstance().getTextPane(),
                                "Not enough stamina");

                    }
                } else if (skillIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendToPane(Window.getInstance().getTextPane(),
                            "Invalid Attack");

                }
            }
        });
    }

    /**
     * Prints choices and sets an observer to wait for player input.
     * 
     * @param back
     *            a Runnable that will open the menu to return to when run
     */
    private void spellSelection(Runnable back) {
        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getSidePane(),
                String.format("%-14s%6s%18s", "Name", "Cost", "Description"));
        Window.appendToPane(Window.getInstance().getSidePane(),
                "------------------------------------------------");
        Window.appendToPane(Window.getInstance().getSidePane(),
                String.format("0: Back"));
        int i = 1;
        for (Spell spell : Player.getInstance().getKnownSpells()) {
            if (i % 2 != 0) {
                Window.appendToPaneBackground(
                        Window.getInstance().getSidePane(),
                        String.format("%-14s%5d%25s",
                                i++ + ": "
                                        + Game.capitalizeFirstLetter(
                                                spell.getName()),
                                spell.getManaCost(), spell.getDescription()),
                        new Color(244, 244, 244));

            } else {
                Window.appendToPane(Window.getInstance().getSidePane(),
                        String.format("%-14s%5d%25s",
                                i++ + ": "
                                        + Game.capitalizeFirstLetter(
                                                spell.getName()),
                                spell.getManaCost(), spell.getDescription()));

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int spellIndex = -2;
                if (Game.isNumeric(evt.getText())) {
                    spellIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (spellIndex >= 0 && spellIndex < Player.getInstance()
                        .getSpells().size()) {
                    if (player.getCurrentMana() >= player.getSpells()
                            .get(spellIndex).getManaCost()) {

                        combatTurn(3, spellIndex);
                        Window.getInstance().removeInputObsever(this);
                    } else {

                        Window.appendToPane(Window.getInstance().getTextPane(),
                                "Not enough mana");

                    }
                } else if (spellIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendToPane(Window.getInstance().getTextPane(),
                            "Invalid Input");

                }
            }
        });
    }

    /**
     * Decides on the order of player and enemy turns and calls methods to do
     * their actions.
     * 
     * @param command
     *            the command for the player to use
     * @param index
     *            the index for the command if it requires one
     */
    void combatTurn(int command, int index) {

        boolean playerLoss = false;
        boolean playerWin = false;

        int[] enemyDecision = decideEnemyAction();
        int enemyCommand = enemyDecision[0];
        int enemyIndex = enemyDecision[1];

        int playerSpeed = player.speedCheck(command, index);
        int enemySpeed = enemy.speedCheck(enemyCommand, enemyIndex);

        LOG.debug("Comparing speed: playerSpeed " + playerSpeed
                + " <><> enemySpeed  " + enemySpeed);
        if (playerSpeed > enemySpeed) {
            LOG.debug("player first");
            playerAction(command, index);
            playerLoss = player.isDead();
            playerWin = enemy.isDead();
            if (!playerWin && !playerLoss) {
                enemyAction(enemyCommand, enemyIndex);
                playerLoss = player.isDead();
                playerWin = enemy.isDead();
            }

        } else {
            LOG.debug("enemy first");
            enemyAction(enemyCommand, enemyIndex);
            playerLoss = player.isDead();
            playerWin = enemy.isDead();
            if (!playerWin && !playerLoss) {
                playerAction(command, index);
                playerLoss = player.isDead();
                playerWin = enemy.isDead();
            }
        }

        if (!playerWin && !playerLoss) {
            Time.getInstance().passTime(1);
        }

        playerLoss = player.isDead();
        playerWin = enemy.isDead();
        if (playerLoss) {
            endCombatLoss();
        } else if (playerWin) {
            endCombatWin();
        } else {
            newTurn();
        }

    }

    /**
     * Makes the enemy decide what action to take
     * 
     * @return an array containing the enemies combat choice
     */
    private int[] decideEnemyAction() {
        boolean failure = false;
        enemy.reloadSkills();

        int[] arr = new int[2];

        if (!failure) {
            int die = Dice.roll(Dice.ENEMY_AI);

            if (die <= enemy.getAttackChance()) {
                arr[0] = 1;
            } else if (die <= enemy.getSpellChance()) {
                arr[0] = 3;
                String type = chooseActionType();
                while (enemy.getSpellsOfType(type).size() == 0) {
                    type = chooseActionType();
                }
                List<Spell> spells = enemy.getSpellsOfType(type);
                if (spells != null && spells.size() > 0) {
                    arr[1] = Dice.roll(spells.size() - 1);
                } else {
                    failure = true;
                }
            } else if (die <= enemy.getSkillChance()) {
                arr[0] = 2;
                String type = chooseActionType();
                while (enemy.getSkillsOfType(type).size() == 0) {
                    type = chooseActionType();
                }
                List<Skill> skills = enemy.getSkillsOfType(type);
                if (skills != null && skills.size() > 0) {
                    arr[1] = Dice.roll(skills.size() - 1);
                } else {
                    failure = true;
                }
            } else if (die <= enemy.getItemChance()) {
                arr[0] = 4;
                String type = chooseActionType();
                while (enemy.getSkillsOfType(type).size() == 0) {
                    type = chooseActionType();
                }
                List<Item> items = enemy.getItemsOfType(type);
                if (items != null && items.size() > 0) {
                    arr[1] = Dice.roll(items.size() - 1);
                } else {
                    failure = true;
                }
            }
        }

        if (failure) {
            decideEnemyAction();
        }

        return arr;
    }

    /**
     * Choose a random action type.
     * 
     * @return a string representing an action type
     */
    public String chooseActionType() {
        int die = Dice.roll(Dice.ENEMY_AI);

        if (die <= enemy.getOffensiveChance()) {
            return OFFENSIVE;
        } else if (die <= enemy.getDebuffChance()) {
            return DEBUFF;
        } else if (die <= enemy.getBuffChance()) {
            return BUFF;
        } else if (die <= enemy.getHealChance() && !enemy.isAtFullHealth()) {
            return HEAL;
        }
        return null;
    }

    /**
     * Ends the combat as a loss.
     */
    private void endCombatLoss() {
        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getTextPane(),
                enemy.getKillDescription());
        inCombat = false;
    }

    /**
     * Ends the combat as a victory.
     */
    private void endCombatWin() {
        inCombat = false;
        player.gainExp(enemy.getExpValue());
        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getTextPane(),
                enemy.getDeathDescription());
        if (enemy.getInventory().size() != 0) {
            Window.appendToPane(Window.getInstance().getTextPane(),
                    Game.capitalizeFirstLetter(enemy.getUseName())
                            + " had some items. Take them?.");
            Game.openLootMenu(enemy, back);

        } else {
            Window.appendToPane(Window.getInstance().getTextPane(),
                    Game.capitalizeFirstLetter(enemy.getUseName())
                            + " did not have any items..");
            Window.getInstance().addInputObsever(new InputObserver() {
                @Override
                public void inputChanged(InputEvent evt) {
                    Window.getInstance().removeInputObsever(this);
                    backToEvent();
                    back.run();
                }
            });

        }
    }

    /**
     * Opens the combat version of the inventory menu. From here the player can
     * select an item to do something with.
     * 
     * @param back
     *            a Runnable that will open the menu to return to when run
     * 
     */
    public void combatInventory(Runnable back) {

        Window.clearPane(Window.getInstance().getSidePane());
        Window.appendToPane(Window.getInstance().getSidePane(),
                String.format("%-24s%10s%13s", "Name", "Weight", "Uses Left"));
        Window.appendToPane(Window.getInstance().getSidePane(),
                "------------------------------------------------");
        Window.appendToPane(Window.getInstance().getSidePane(),
                String.format("0: Back"));
        int i = 1;
        for (Item item : Player.getInstance().getInventory()) {
            if (i % 2 != 0) {
                Window.appendToPaneBackground(
                        Window.getInstance().getSidePane(),
                        i++ + ": " + item.getItemInfo(),
                        new Color(244, 244, 244));

            } else {
                Window.appendToPane(Window.getInstance().getSidePane(),
                        i++ + ": " + item.getItemInfo());

            }

        }
        Window.getInstance().addInputObsever(new InputObserver() {
            @Override
            public void inputChanged(InputEvent evt) {
                int itemIndex = -2;
                if (Game.isNumeric(evt.getText())) {
                    itemIndex = Integer.parseInt(evt.getText()) - 1;
                }
                if (itemIndex >= 0 && itemIndex < Player.getInstance()
                        .getInventory().size()) {
                    combatItemChoiceMenu(itemIndex,
                            () -> combatInventory(back));
                    Window.getInstance().removeInputObsever(this);
                } else if (itemIndex == -1) {
                    Window.getInstance().removeInputObsever(this);
                    back.run();
                } else {
                    Window.appendToPane(Window.getInstance().getTextPane(),
                            "Invalid Command");

                }
            }
        });
    }

    /**
     * Opens the combat version of the item choice menu. From here the player
     * can choose what to do with the selected item.
     * 
     * @param back
     *            a Runnable that will open the menu to return to when run
     * 
     */
    public void combatItemChoiceMenu(int itemIndex, Runnable back) {
        Window.clearPane(Window.getInstance().getSidePane());
        Item item = Player.getInstance().getInventoryItem(itemIndex);

        Window.appendToPane(Window.getInstance().getSidePane(),
                "0: Back\n1: Use\n2: Inspect\n3: Drop");

        if (item != null) {
            Window.getInstance().addInputObsever(new InputObserver() {
                @Override
                public void inputChanged(InputEvent evt) {
                    int command = -1;
                    if (Game.isNumeric(evt.getText())) {
                        command = Integer.parseInt(evt.getText());
                    }
                    switch (command) {
                    case 0: // back
                        Window.getInstance().removeInputObsever(this);
                        back.run();
                        break;
                    case 1: // use
                        Item item = Player.getInstance()
                                .getInventoryItem(itemIndex);
                        if (item.isBattleUse()) {
                            if (item.getUses() <= 0) {
                                Window.appendToPane(
                                        Window.getInstance().getTextPane(),
                                        "That item is out of uses.");

                            } else {
                                combatTurn(4, itemIndex);
                                Window.getInstance().removeInputObsever(this);
                            }
                        } else {
                            Window.appendToPane(
                                    Window.getInstance().getTextPane(),
                                    "You cant use that item in battle!");
                        }
                        break;
                    case 2: // inspect
                        Window.appendToPane(Window.getInstance().getTextPane(),
                                Player.getInstance()
                                        .getItemDescription(itemIndex));

                        break;
                    case 3: // drop
                        Window.appendToPane(Window.getInstance().getTextPane(),
                                "You threw away the " + Player.getInstance()
                                        .removeItemFromInventory(itemIndex)
                                        .getName());
                        back.run();
                        Window.getInstance().removeInputObsever(this);

                        break;
                    default:
                        Window.appendToPane(Window.getInstance().getTextPane(),
                                "Invalid Command");
                        break;
                    }
                }
            });
        }
    }

    /**
     * Returns true if the player is currently in combat and false otherwise.
     * 
     * @return if the player is in combat
     */
    public static boolean isInCombat() {
        return inCombat;
    }

    /**
     * Sends the player back to the event they came from.
     */
    public static void backToEvent() {
        EventReader.resumeEvent();
        Window.getInstance().swapToMapPane();
    }
}
