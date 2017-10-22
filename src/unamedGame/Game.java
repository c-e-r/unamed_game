/**
 * 
 */
package unamedGame;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import unamedGame.entities.Enemy;
import unamedGame.entities.Player;
import unamedGame.entities.Entity.EquipmentIndex;
import unamedGame.events.EventReader;
import unamedGame.events.EventSelector;
import unamedGame.input.InputEvent;
import unamedGame.input.InputObserver;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;
import unamedGame.time.Time;
import unamedGame.ui.Window;
import unamedGame.util.Colors;
import unamedGame.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author c-e-r
 *
 */
public class Game {

	public static InputObserver observer;
	public static Window window = Window.getInstance();

	private static final Logger LOG = LogManager.getLogger(Game.class);

	public static boolean onMoveMenu = false;

	/**
	 * Starts the game.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Window.getInstance().getFrame().setVisible(true);
		openMainMenu();

	}

	public static void openMainMenu() {
		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(), "1: NEW GAME\n2: LOAD GAME");
		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {

				clearTextField();

				if (isNumeric(evt.getText())) {
					int command = Integer.parseInt(evt.getText());
					if (command >= 1 && command <= 2) {
						switch (command) {
						case 1:
							Window.getInstance().removeInputObsever(this);
							openExplorationMenu();
							break;
						case 2:
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

	public static void openLoadMenu(Runnable back) {
		Window.clearPane(window.getSidePane());
		Window.appendToPane(window.getSidePane(), "LOAD GAME");
		Window.appendToPane(window.getSidePane(), "0: Back");
		for (int i = 1; i < 10; i++) {
			boolean validChoices[] = new boolean[10];
			if (new File("saves/save" + i + ".sav").exists()) {
				validChoices[i] = true;
				try {
					ObjectInputStream in;
					in = new ObjectInputStream(new FileInputStream(
							new File("saves/save" + i + ".sav")));
					Save save = ((Save) in.readObject());
					Window.appendToPane(window.getSidePane(),
							i + ": " + save.getSaveText());
					Window.appendToPane(window.getSidePane(),
							"    " + save.getSaveNote());
					in.close();
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Window.appendToPane(window.getSidePane(),
						i + ": --Slot Empty--");
				Window.appendToPane(window.getSidePane(), "");
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
					if (command >= 1 && command <= 9) {
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
							Window.appendToPane(window.getTextPane(),
									"That slot is empty.");
						}

					}

				}
			}

		});
	}

	public static void openSaveMenu(Runnable back) {

		Window.clearPane(window.getSidePane());
		Window.appendToPane(window.getSidePane(), "SAVE GAME");
		Window.appendToPane(window.getSidePane(), "0: Back");
		for (int i = 1; i < 10; i++) {
			boolean validChoices[] = new boolean[10];

			if (new File("saves/save" + i + ".sav").exists()) {
				validChoices[i] = true;
				try {
					ObjectInputStream in;
					in = new ObjectInputStream(new FileInputStream(
							new File("saves/save" + i + ".sav")));
					Save save = ((Save) in.readObject());
					Window.appendToPane(window.getSidePane(),
							i + ": " + save.getSaveText());
					Window.appendToPane(window.getSidePane(),
							"    " + save.getSaveNote());
					in.close();
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Window.appendToPane(window.getSidePane(),
						i + ": --Slot Empty--");
				Window.appendToPane(window.getSidePane(), "");
			}
		}
		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {

				clearTextField();
				String[] commands = evt.getText().split(" ");
				String saveNote = "";
				System.out.println(Arrays.toString(commands));
				for (int j = 1; j < commands.length; j++) {
					saveNote += commands[j] + " ";
					System.out.println(saveNote);
				}
				if (isNumeric(commands[0])) {
					int command = Integer.parseInt(commands[0]);
					if (command == 0) {
						window.removeInputObsever(this);
						back.run();
					}
					if (command >= 1 && command <= 9) {
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
							openSaveMenu(back);
						}

					}

				}
			}

		});
	}

	private static void openSaveOverwriteConfirmMenu(File file,
			Runnable callerBack, String saveNote) {
		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(),
				"OVERWRITE SAVE?\n1: Yes\n2: No");

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
						openSaveMenu(callerBack);
					}
				}
			}

		});
	}

	/**
	 * Opens the main exploration menu and waits for player input
	 */
	public static void openExplorationMenu() {
		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(),
				"1: Explore \n2: Move\n3: Gather\n4: Inventory \n5: Status\n6: Save");

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
					debug();
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
				case 5: // status
					window.removeInputObsever(this);
					openStatusMenu(() -> openExplorationMenu());
					break;
				case 6: // save
					window.removeInputObsever(this);
					openSaveMenu(() -> openExplorationMenu());
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
		try {
			EventSelector.startRandomEventFromFileList(World.getInstance()
					.getTile(Player.getInstance().getLocation())
					.getEventFiles());
		} catch (FileNotFoundException e) {
			Window.appendToPane(Window.getInstance().getTextPane(),
					"ERROR: " + e.getMessage());
			Game.openExplorationMenu();
			LOG.error("Event list file not found.", e);
			e.printStackTrace();
		}
	}

	/*
	 * Opens a menu for the player to choose which direction to move.
	 */
	private static void openMoveMenu(Runnable back) {
		onMoveMenu = true;
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
				onMoveMenu = false;
				window.removeInputObsever(this);
				openExplorationMenu();
			}

		});
	}

	/*
	 * NOT YET IMPLEMENTED
	 */
	private static void gather() {
	}

	/**
	 * Opens the out of combat inventory and waits for the player to select an
	 * item or return to the exploration menu.
	 */
	public static void openInventoryMenu(Runnable back) {

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
					itemChoiceMenu(itemIndex, () -> openInventoryMenu(back));
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
	 * Shows the player choices for the current item and waits for player input
	 * 
	 * @param itemIndex
	 *            the index of the item being manipulated
	 */
	public static void itemChoiceMenu(int itemIndex, Runnable back) {
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
						back.run();
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
									back.run();
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

						back.run();
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
			LOG.error("Somehow you tried to access an item that dosn't exist");
		}
	}

	public static void getHandChoice(Item item, Runnable back) {
		Window.clearPane(Window.getInstance().getSidePane());
		Window.addToPane(Window.getInstance().getSidePane(),
				"0: Back\n1: Left");

		if (Player.getInstance().getEquipment()[EquipmentIndex.LEFT_HAND
				.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + Player.getInstance()
							.getEquipment()[EquipmentIndex.LEFT_HAND.getValue()]
									.getName());
		}
		Window.addToPane(Window.getInstance().getSidePane(), "\n2: Right");
		if (Player.getInstance().getEquipment()[EquipmentIndex.RIGHT_HAND
				.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + Player.getInstance()
							.getEquipment()[EquipmentIndex.RIGHT_HAND
									.getValue()].getName());
		}
		Window.appendToPane(Window.getInstance().getTextPane(),
				"Equip to which hand?");
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
						Window.appendToPane(window.getTextPane(),
								"Invalid Choice");
					}
				}
			}

		});
	}

	public static void getHeldChoice(Item item, Runnable back) {
		Window.clearPane(Window.getInstance().getSidePane());
		Window.addToPane(Window.getInstance().getSidePane(),
				"0: Back\n1: Left");

		if (Player.getInstance().getEquipment()[EquipmentIndex.LEFT_HELD
				.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + Player.getInstance()
							.getEquipment()[EquipmentIndex.LEFT_HELD.getValue()]
									.getName());
		}
		Window.addToPane(Window.getInstance().getSidePane(), "\n2: Right");
		if (Player.getInstance().getEquipment()[EquipmentIndex.RIGHT_HELD
				.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + Player.getInstance()
							.getEquipment()[EquipmentIndex.RIGHT_HELD
									.getValue()].getName());
		}
		Window.addToPane(Window.getInstance().getSidePane(), "\n3: Both");
		Window.appendToPane(Window.getInstance().getTextPane(),
				"Hold in which hand?");
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
						Window.appendToPane(window.getTextPane(),
								"Invalid Choice");
					}
				}
			}

		});
	}

	private static void openStatusMenu(Runnable back) {
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
					openStatPointMenu(() -> openStatusMenu(back));
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

	private static void openStatPointMenu(Runnable back) {
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

	public static void debug() {
		openDebugMenu();
	}

	private static void openDebugMenu() {
		Window.clearPane(window.getSidePane());
		Window.addToPane(window.getSidePane(),
				"0: Back \naddItem <itemName>\ngainExp <amount>\nstartEvent <eventName>\naddSkill <skillName>\naddSpell <spellName>\nstartCombat <enemyName>");

		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {
				String[] command = evt.getText().split(" ");
				switch (command[0]) {
				case "0":
					window.removeInputObsever(this);
					openExplorationMenu();
					break;
				case "addItem":

					Item newItem = Item.buildItem(command[1]);
					if (newItem != null) {
						Player.getInstance().addItemToInventory(newItem);
						Window.appendToPane(window.getTextPane(),
								command[1] + " added to inventory.");
					} else {
						Window.appendToPane(window.getTextPane(),
								"ERROR: Somthing went wrong adding an item to your inventory. See game.log for more information.");

					}
					break;
				case "gainExp":
					if (isNumeric(command[1])) {
						Player.getInstance()
								.gainExp(Integer.parseInt(command[1]));
						Window.appendToPane(window.getTextPane(),
								"You gained " + command[1] + "exp");
					} else {
						Window.appendToPane(window.getTextPane(),
								"That command needs a number.");
					}
					break;
				case "startEvent":
					window.removeInputObsever(this);
					EventReader.startEvent(command[1]);
					break;
				case "addSkill":

					Skill newSkill = Skill.buildSkill(command[1]);
					if (newSkill != null) {
						Player.getInstance().addInnateSkill(newSkill);
						Window.appendToPane(window.getTextPane(),
								command[1] + " added to innate skill list.");
					} else {
						Window.appendToPane(window.getTextPane(),
								"ERROR: Somthing went wrong while creating a skill. See game.log for more information.");
					}

					break;
				case "addSpell":
					Spell newSpell = Spell.buildSpell(command[1]);
					if (newSpell != null) {
						Player.getInstance().addKnownSpell(newSpell);
						Window.appendToPane(window.getTextPane(),
								command[1] + " added to known spells list.");
					} else {
						Window.appendToPane(Window.getInstance().getTextPane(),
								"ERROR: Somthing went wrong while creating a spell. See game.log for more information.");
					}
					break;
				case "startCombat":
					window.removeInputObsever(this);
					Enemy newEnemy = Enemy.buildEnemy(command[1]);
					if (newEnemy != null) {
						new Combat(newEnemy, Combat.FROM_EXPLORATION);
					} else {
						Window.appendToPane(Window.getInstance().getTextPane(),
								"ERROR: Somthing went wrong while creating an enemy. See game.log for more info.");
					}

					break;
				default:
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

}
