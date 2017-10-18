/**
 * 
 */
package unamedGame;

import java.awt.Color;
import java.util.List;

import unamedGame.entities.Enemy;
import unamedGame.entities.Player;
import unamedGame.events.EventReader;
import unamedGame.input.InputEvent;
import unamedGame.input.InputObserver;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;
import unamedGame.time.Time;
import unamedGame.ui.Window;

/**
 * Handles the games combat loop.
 * 
 * @author c-e-r
 *
 */
public class Combat {

	private Player player;
	private Enemy enemy;
	private static boolean inCombat;
	private int returnPoint;

	public static final int FROM_EXPLORATION = 1;
	public static final int FROM_EVENT = 2;

	private static final String OFFENSIVE = "offensive";
	private static final String DEBUFF = "debuff";
	private static final String BUFF = "buff";
	private static final String HEAL = "heal";

	/**
	 * Starts a combat loop between the player and given Enemy.
	 * 
	 * @param enemy
	 *            the Enemy to start combat with
	 */
	public Combat(Enemy enemy, int returnPoint) {
		this.returnPoint = returnPoint;
		player = Player.getInstance();
		this.enemy = enemy;
		inCombat = true;

		Window.getInstance().swapMapPane();
		Window.clearPane(Window.getInstance().getTextPane());
		Window.appendToPane(Window.getInstance().getTextPane(),
				enemy.getDescription());

		newTurn();
	}

	/*
	 * Starts a new combat turn
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

	/*
	 * Displays player turn choices and waits for input
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
						skillSelection();
						Window.getInstance().removeInputObsever(this);
						break;
					case 3: // magic
						spellSelection();
						Window.getInstance().removeInputObsever(this);
						break;
					case 4: // item
						combatInventory();
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

	/*
	 * Does a specific action depended on the command and index given to it.
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

	/*
	 * Does a specific action depended on the command and index given to it.
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

	/*
	 * Prints choices and sets an observer to wait for player input.
	 */
	private void skillSelection() {
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
				}
				if (skillIndex >= 0 && skillIndex < Player.getInstance()
						.getInventory().size()) {
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
					getCombatInput();
				} else {
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Attack");

				}
			}
		});
	}

	/*
	 * Prints choices and sets an observer to wait for player input.
	 */
	private void spellSelection() {
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
						.getInventory().size()) {
					if (player.getCurrentStamina() >= player.getCombinedSkills()
							.get(spellIndex).getStaminaCost()) {

						combatTurn(3, spellIndex);
						Window.getInstance().removeInputObsever(this);
					} else {

						Window.appendToPane(Window.getInstance().getTextPane(),
								"Not enough mana");

					}
				} else if (spellIndex == -1) {
					Window.getInstance().removeInputObsever(this);
					getCombatInput();
				} else {
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Input");

				}
			}
		});
	}

	/*
	 * Decides on the order of player and enemy turns and calls methods to do
	 * their actions.
	 */
	private void combatTurn(int command, int index) {

		boolean playerLoss = false;
		boolean playerWin = false;

		int[] arr = decideEnemyAction();
		int enemyCommand = arr[0];
		int enemyIndex = arr[1];

		if (player.speedCheck(command, index) > enemy.speedCheck(enemyCommand,
				enemyIndex)) {
			playerAction(command, index);
			playerLoss = player.isDead();
			playerWin = enemy.isDead();
			if (!playerWin && !playerLoss) {
				enemyAction(enemyCommand, enemyIndex);
				playerLoss = player.isDead();
				playerWin = enemy.isDead();
			}

		} else {
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
			player.recalculateStats();
			playerLoss = player.isDead();
			playerWin = enemy.isDead();
		}

		if (!playerWin && !playerLoss) {
			enemy.recalculateStats();
			playerLoss = player.isDead();
			playerWin = enemy.isDead();
		}
		Time.getInstance().passTime(1);
		if (playerLoss) {
			endCombatLoss();
		} else if (playerWin) {
			endCombatWin();
		} else {
			newTurn();
		}

	}

	/*
	 * Makes the enemy decide what action to take
	 */
	private int[] decideEnemyAction() {
		boolean failure = false;
		enemy.reloadSkills();

		int[] arr = new int[2];

		if (failure == false) {
			int die = Dice.roll(100);

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

		if (failure == true)

		{
			decideEnemyAction();
		}

		return arr;
	}

	/**
	 * Choose a random action type
	 * 
	 * @return a string representing an action type
	 */
	public String chooseActionType() {
		int die = Dice.roll(100);

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

	/*
	 * Ends the combat as a loss.
	 */
	private void endCombatLoss() {
		Window.clearPane(Window.getInstance().getSidePane());
		Window.appendToPane(Window.getInstance().getTextPane(),
				enemy.getKillDescription());
		inCombat = false;
	}

	/*
	 * Ends the combat as a victory.
	 */
	private void endCombatWin() {
		player.gainExp(enemy.getExpValue());
		Window.clearPane(Window.getInstance().getSidePane());
		Window.appendToPane(Window.getInstance().getTextPane(),
				enemy.getDeathDescription());
		switch (returnPoint) {
		case FROM_EXPLORATION:
			Game.openExplorationMenu();
			break;
		case FROM_EVENT:
			backToEvent();
			break;
		default:
			Game.openExplorationMenu();
		}
		inCombat = true;
	}

	/*
	 * Opens the combat version of the inventory menu. From here the player can
	 * select an item to do something with.
	 */
	public void combatInventory() {

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
					combatItemChoiceMenu(itemIndex);
					Window.getInstance().removeInputObsever(this);
				} else if (itemIndex == -1) {
					Window.getInstance().removeInputObsever(this);
					getCombatInput();
				} else {
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Command");

				}
			}
		});
	}

	/*
	 * Opens the combat version of the item choice menu. From here the player
	 * can choose what to do with the selected item.
	 */
	public void combatItemChoiceMenu(int itemIndex) {
		Window.clearPane(Window.getInstance().getSidePane());
		Window.appendToPane(Window.getInstance().getSidePane(),
				"0: Back \n1: Use\n2: Inspect\n3: Drop\n4: Equip");
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
					combatInventory();
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
						Window.appendToPane(Window.getInstance().getTextPane(),
								"You cant use that item in battle!");
					}
					break;
				case 2: // inspect
					Window.appendToPane(Window.getInstance().getTextPane(),
							Player.getInstance().getItemDescription(itemIndex));

					break;
				case 3: // drop
					Window.appendToPane(Window.getInstance().getTextPane(),
							"You threw away the " + Player.getInstance()
									.removeItemFromInventory(itemIndex)
									.getName());
					combatInventory();
					Window.getInstance().removeInputObsever(this);

					break;
				case 4: // equip
					Window.getInstance().removeInputObsever(this);

					break;
				case 5:
					Window.getInstance().removeInputObsever(this);

				default:
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Command");
					break;
				}
			}
		});
	}

	/**
	 * Returns true if the player is currently in combat and false otherwise.
	 * 
	 * @return if the player is in combat
	 */
	public static boolean isInCombat() {
		return inCombat;
	}

	/*
	 * Sends the player back to the event they came from.
	 */
	private void backToEvent() {

		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {
				EventReader.resumeEvent();
				Window.getInstance().swapMapPane();
				Window.getInstance().removeInputObsever(this);
			}
		});
	}
}
