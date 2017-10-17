/**
 * 
 */
package unamedGame;

import java.awt.Color;
import java.util.ArrayList;

import unamedGame.effects.StatIncreaseEffect;
import unamedGame.entities.Player;
import unamedGame.events.EventReader;
import unamedGame.events.EventSelector;
import unamedGame.input.InputEvent;
import unamedGame.input.InputObserver;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.time.Time;
import unamedGame.time.TimeObserver;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

/**
 * @author c-e-r
 *
 */
public class Game {

	public static InputObserver observer;
	public static Window window = Window.getInstance();

	/**
	 * Starts the game.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TimeObserver timeObserver = new TimeObserver();
		timeObserver.observe(Time.getInstance());
		Item item = new Item("testItem");
		Item item2 = new Item("testItem");
		Player.getInstance().addItemToInventory(item);
		Player.getInstance().addItemToInventory(item2);
		Player.getInstance().addItemToInventory(new Item("dagger"));
		Player.getInstance().addInnateSkill(new Skill("testSkill"));
		Window.getInstance().getFrame().setVisible(true);
		openExplorationMenu();

	}

	/**
	 * Opens the main exploration menu and waits for player input
	 */
	public static void openExplorationMenu() {
		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(),
				"1: Explore \n2: Move\n3: Gather\n4: Inventory \n5: Status");

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
				case 1: // explore
					window.removeInputObsever(this);
					explore();
					break;
				case 2: // move
					window.removeInputObsever(this);
					move();
					break;
				case 3: // gather?
					window.removeInputObsever(this);
					gather();
					break;
				case 4: // inventory
					window.removeInputObsever(this);
					inventory();
					break;
				case 5: // status
					window.removeInputObsever(this);
					openStatusMenu();
					break;
				default:
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Command");
					break;
				}

			}
		});
	}

	/*
	 * NOT YET IMPLEMENTED
	 */
	private static void explore() {
		EventSelector.startRandomEvent("forestEvents");
	}

	/*
	 * Opens a menu for the player to choose which direction to move.
	 */
	private static void move() {
		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(),
				"1: Northwest\n2: North\n3: Northeast\n4: Southeast\n5: South\n6: Southwest");
		Window.appendToPane(Window.getInstance().getTextPane(), "Move where?");

		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {

				clearTextField();

				if (isNumeric(evt.getText())) {
					int command = Integer.parseInt(evt.getText());
					if (command >= 1 && command <= 6) {
						Player.getInstance().move(command - 1);
						Window.getInstance().getMapPane().repaint();
					}

				}
				window.removeInputObsever(this);
				openExplorationMenu();
			}

		});
	}

	/*
	 * NOT YET IMPLEMENTED
	 */
	private static void gather() {
		System.out.println("gather");
	}

	/**
	 * Opens the out of combat inventory and waits for the player to select an
	 * item or return to the exploration menu.
	 */
	public static void inventory() {

		Window.clearPane(window.getSidePane());
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
				if (isNumeric(evt.getText())) {
					itemIndex = Integer.parseInt(evt.getText()) - 1;
				}
				if (itemIndex >= 0 && itemIndex < Player.getInstance()
						.getInventory().size()) {
					itemChoiceMenu(itemIndex);
					Window.getInstance().removeInputObsever(this);
				} else if (itemIndex == -1) {
					Window.getInstance().removeInputObsever(this);
					openExplorationMenu();
				} else {
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Command");

				}
			}
		});

	}

	/**
	 * Shows the player choices for the current item and waits for player input
	 * 
	 * @param itemIndex
	 *            the index of the item being manipulated
	 */
	public static void itemChoiceMenu(int itemIndex) {
		Window.clearPane(window.getSidePane());
		Item item = Player.getInstance().getInventoryItem(itemIndex);
		if (item != null) {
			if (item.isEquipped()) {
				Window.appendToPane(Window.getInstance().getSidePane(),
						"0: Back\n1: Use\n2: Inspect\n3: Drop\n4: Uneqip");

			} else {
				Window.appendToPane(Window.getInstance().getSidePane(),
						"0: Back\n1: Use\n2: Inspect\n3: Drop\n4: Equip");

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
						inventory();
						break;
					case 1: // use
						if (item.isFieldUse()) {
							if (item.getMaxUses() == 0) {
								Window.appendToPane(
										Window.getInstance().getTextPane(),
										"That item is not usable.");
							} else {
								if (item.getUses() <= 0) {
									Window.appendToPane(
											Window.getInstance().getTextPane(),
											"That item is out of uses.");

								} else {
									Player.getInstance().applyItemEffects(
											Player.getInstance()
													.getInventoryItem(
															itemIndex),
											Player.getInstance());
									Window.getInstance()
											.removeInputObsever(this);
									inventory();
								}

							}

						} else {
							Window.appendToPane(
									Window.getInstance().getTextPane(),
									"You cant use that item here.");
						}

						break;
					case 2: // inspect
						Window.appendToPane(Window.getInstance().getTextPane(),
								Player.getInstance()
										.getItemDescription(itemIndex));

						break;
					case 3: // drop
						if (item.isEquipped()) {
							Player.getInstance()
									.unequipInventoryItem(itemIndex);
							Window.appendToPane(
									Window.getInstance().getTextPane(),
									"You threw away the "
											+ Player.getInstance()
													.removeItemFromInventory(
															itemIndex)
													.getName()
											+ ".");
						} else {
							Window.appendToPane(
									Window.getInstance().getTextPane(),
									"You threw away the "
											+ Player.getInstance()
													.removeItemFromInventory(
															itemIndex)
													.getName()
											+ ".");
							Window.getInstance().removeInputObsever(this);
						}

						inventory();

						break;
					case 4: // equip
						if (item.isEquippable()) {
							if (item.isEquipped()) {
								Player.getInstance()
										.unequipInventoryItem(itemIndex);
								inventory();
							} else {
								Player.getInstance()
										.equipInventoryItem(itemIndex);

							}
							Window.getInstance().removeInputObsever(this);
						} else {
							Window.appendToPane(
									Window.getInstance().getTextPane(),
									"You cant equip that item!");
						}

						break;
					default:
						Window.appendToPane(Window.getInstance().getTextPane(),
								"Invalid Command");
						break;
					}

				}
			});
		} else {
			System.out.println(
					"Somehow you tried to access an item that dosn't exist");
		}

	}

	private static void openStatusMenu() {
		window.swapToPlayerPane();
		Window.clearPane(window.getPlayerPane());
		Window.appendToPane(Window.getInstance().getPlayerPane(),
				Player.getInstance().getStatus());

		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(),
				"0: Back\n1: View Perks\n2: Spend Stat Points\n3: Spend Perk Points");

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
					Window.appendToPane(Window.getInstance().getTextPane(),
							"This command is not implemented yet");
					break;
				case 2: // Spend Stat Points
					window.removeInputObsever(this);
					openStatPointMenu();
					break;
				case 3: // Spend Perk Points
					Window.appendToPane(Window.getInstance().getTextPane(),
							"This command is not implemented yet");
					break;
				default:
					Window.appendToPane(Window.getInstance().getTextPane(),
							"Invalid Command");
					break;
				}
			}

		});
	}

	private static void openStatPointMenu() {
		Window.clearPane(window.getSidePane());
		Window.clearPane(window.getTextPane());
		Window.addToPane(window.getSidePane(),
				"0: Cancel\n1: Finish\n2: + Vitality\n3: + Strength\n4: + Dexterity\n5: + Intellect\n6: + Spirit\n7: + Luck");
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

		Window.appendToPane(window.getTextPane(),
				"Points Remaining: " + player.getNewStatPoints());
		Window.appendToPane(window.getTextPane(), "STAT CURRENT NEW COST");

		Window.addToPane(window.getTextPane(),
				"VIT   " + curVitality + " -----> ");
		if (player.getNewVitality() > curVitality) {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewVitality()),
					Colors.STAT_POINT_INCREASE);
		} else {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewVitality()));
		}
		if (Math.ceil((double) player.getNewVitality() / 5) > player
				.getNewStatPoints()) {
			Window.appendToPane(window.getTextPane(),
					"   (" + (int) Math
							.ceil((double) player.getNewVitality() / 5) + ")",
					Colors.STAT_POINT_CANT_AFFORD);
		} else {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewVitality() / 5)
					+ ")");
		}

		Window.addToPane(window.getTextPane(),
				"STR   " + curStrength + " -----> ");
		if (player.getNewStrength() > curStrength) {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewStrength()),
					Colors.STAT_POINT_INCREASE);
		} else {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewStrength()));
		}
		if (Math.ceil((double) player.getNewStrength() / 5) > player
				.getNewStatPoints()) {
			Window.appendToPane(window.getTextPane(),
					"   (" + (int) Math
							.ceil((double) player.getNewStrength() / 5) + ")",
					Colors.STAT_POINT_CANT_AFFORD);
		} else {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewStrength() / 5)
					+ ")");
		}

		Window.addToPane(window.getTextPane(),
				"DEX   " + curDexterity + " -----> ");
		if (player.getNewDexterity() > curDexterity) {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewDexterity()),
					Colors.STAT_POINT_INCREASE);
		} else {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewDexterity()));
		}
		if (Math.ceil((double) player.getNewDexterity() / 5) > player
				.getNewStatPoints()) {
			Window.appendToPane(window.getTextPane(),
					"   (" + (int) Math
							.ceil((double) player.getNewDexterity() / 5) + ")",
					Colors.STAT_POINT_CANT_AFFORD);
		} else {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewDexterity() / 5)
					+ ")");
		}

		Window.addToPane(window.getTextPane(),
				"INT   " + curIntellect + " -----> ");
		if (player.getNewIntellect() > curIntellect) {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewIntellect()),
					Colors.STAT_POINT_INCREASE);
		} else {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewIntellect()));
		}
		if (Math.ceil((double) player.getNewIntellect() / 5) > player
				.getNewStatPoints()) {
			Window.appendToPane(window.getTextPane(),
					"   (" + (int) Math
							.ceil((double) player.getNewIntellect() / 5) + ")",
					Colors.STAT_POINT_CANT_AFFORD);
		} else {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewIntellect() / 5)
					+ ")");
		}

		Window.addToPane(window.getTextPane(),
				"SPR   " + curSpirit + " -----> ");
		if (player.getNewSpirit() > curSpirit) {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewSpirit()),
					Colors.STAT_POINT_INCREASE);
		} else {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewSpirit()));
		}
		if (Math.ceil((double) player.getNewSpirit() / 5) > player
				.getNewStatPoints()) {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewSpirit() / 5) + ")",
					Colors.STAT_POINT_CANT_AFFORD);
		} else {
			Window.appendToPane(window.getTextPane(),
					"   (" + (int) Math.ceil((double) player.getNewSpirit() / 5)
							+ ")");
		}

		Window.addToPane(window.getTextPane(), "LCK   " + curLuck + " -----> ");
		if (player.getNewLuck() > curLuck) {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewLuck()),
					Colors.STAT_POINT_INCREASE);
		} else {
			Window.addToPane(window.getTextPane(),
					Integer.toString(player.getNewLuck()));
		}
		if (Math.ceil((double) player.getNewLuck() / 5) > player
				.getNewStatPoints()) {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewLuck() / 5) + ")",
					Colors.STAT_POINT_CANT_AFFORD);
		} else {
			Window.appendToPane(window.getTextPane(), "   ("
					+ (int) Math.ceil((double) player.getNewLuck() / 5) + ")");
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
						openStatusMenu();
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
						
						openStatusMenu();
						break;
					case 2:
						cost = (int) Math
								.ceil((double) player.getNewVitality() / 5);
						System.out.println(cost);
						System.out.println(player.getNewStatPoints());
						if (player.getNewStatPoints() >= cost) {
							player.setNewVitality(player.getNewVitality() + 1);
							player.setNewStatPoints(
									player.getNewStatPoints() - cost);
						}
						break;
					case 3:
						cost = (int) Math
								.ceil((double) player.getNewStrength() / 5);
						System.out.println(cost);
						System.out.println(player.getNewStatPoints());
						if (player.getNewStatPoints() >= cost) {
							player.setNewStrength(player.getNewStrength() + 1);
							player.setNewStatPoints(
									player.getNewStatPoints() - cost);
						}
						break;
					case 4:
						cost = (int) Math
								.ceil((double) player.getNewDexterity() / 5);
						System.out.println(cost);
						System.out.println(player.getNewStatPoints());
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
						System.out.println(cost);
						System.out.println(player.getNewStatPoints());
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
						System.out.println(cost);
						System.out.println(player.getNewStatPoints());
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
				Window.clearPane(window.getTextPane());

				if (print) {
					Window.appendToPane(window.getTextPane(),
							"Points Remaining: " + player.getNewStatPoints());
					Window.appendToPane(window.getTextPane(),
							"STAT CURRENT NEW COST");
					Window.addToPane(window.getTextPane(),
							"VIT   " + curVitality + " -----> ");
					if (player.getNewVitality() > curVitality) {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewVitality()),
								Colors.STAT_POINT_INCREASE);
					} else {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewVitality()));
					}
					if (Math.ceil((double) player.getNewVitality() / 5) > player
							.getNewStatPoints()) {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewVitality() / 5)
										+ ")",
								Colors.STAT_POINT_CANT_AFFORD);
					} else {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewVitality() / 5)
										+ ")");
					}

					Window.addToPane(window.getTextPane(),
							"STR   " + curStrength + " -----> ");
					if (player.getNewStrength() > curStrength) {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewStrength()),
								Colors.STAT_POINT_INCREASE);
					} else {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewStrength()));
					}
					if (Math.ceil((double) player.getNewStrength() / 5) > player
							.getNewStatPoints()) {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewStrength() / 5)
										+ ")",
								Colors.STAT_POINT_CANT_AFFORD);
					} else {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewStrength() / 5)
										+ ")");
					}

					Window.addToPane(window.getTextPane(),
							"DEX   " + curDexterity + " -----> ");
					if (player.getNewDexterity() > curDexterity) {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewDexterity()),
								Colors.STAT_POINT_INCREASE);
					} else {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewDexterity()));
					}
					if (Math.ceil(
							(double) player.getNewDexterity() / 5) > player
									.getNewStatPoints()) {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewDexterity() / 5)
										+ ")",
								Colors.STAT_POINT_CANT_AFFORD);
					} else {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewDexterity() / 5)
										+ ")");
					}

					Window.addToPane(window.getTextPane(),
							"INT   " + curIntellect + " -----> ");
					if (player.getNewIntellect() > curIntellect) {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewIntellect()),
								Colors.STAT_POINT_INCREASE);
					} else {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewIntellect()));
					}
					if (Math.ceil(
							(double) player.getNewIntellect() / 5) > player
									.getNewStatPoints()) {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewIntellect() / 5)
										+ ")",
								Colors.STAT_POINT_CANT_AFFORD);
					} else {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewIntellect() / 5)
										+ ")");
					}

					Window.addToPane(window.getTextPane(),
							"SPR   " + curSpirit + " -----> ");
					if (player.getNewSpirit() > curSpirit) {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewSpirit()),
								Colors.STAT_POINT_INCREASE);
					} else {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewSpirit()));
					}
					if (Math.ceil((double) player.getNewSpirit() / 5) > player
							.getNewStatPoints()) {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewSpirit() / 5)
										+ ")",
								Colors.STAT_POINT_CANT_AFFORD);
					} else {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math.ceil(
										(double) player.getNewSpirit() / 5)
										+ ")");
					}

					Window.addToPane(window.getTextPane(),
							"LCK   " + curLuck + " -----> ");
					if (player.getNewLuck() > curLuck) {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewLuck()),
								Colors.STAT_POINT_INCREASE);
					} else {
						Window.addToPane(window.getTextPane(),
								Integer.toString(player.getNewLuck()));
					}
					if (Math.ceil((double) player.getNewLuck() / 5) > player
							.getNewStatPoints()) {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math
										.ceil((double) player.getNewLuck() / 5)
										+ ")",
								Colors.STAT_POINT_CANT_AFFORD);
					} else {
						Window.appendToPane(window.getTextPane(),
								"   (" + (int) Math
										.ceil((double) player.getNewLuck() / 5)
										+ ")");
					}

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
	 * Accepts a string and return the string with the first letter capitalized
	 * 
	 * @param string
	 *            the string to capitalize the first letter of
	 * @return the given string with the first letter capitalized
	 */
	public static String capitalizeFirstLetter(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * clears the text windows textfield
	 */
	public static void clearTextField() {
		Window.clearField(window.getTextField());
	}

	/*
	 * prints the status of the player to the textPane
	 */
	private static void printStatus() {
		Window.appendToPane(Window.getInstance().getTextPane(),
				Player.getInstance().getStatus());
	}
}
