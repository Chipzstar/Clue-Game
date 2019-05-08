/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;

/**
 * @author xxlig
 */
public class AI extends Player {

	public enum Difficulty {
		EASY("Easy"), MEDIUM("Medium"), HARD("Hard");
		private String representation;

		Difficulty(String s) {
			this.representation = s;
		}

		@Override
		public String toString() {
			return representation;
		}
	}

	private boolean isAccusation;
	private Difficulty level;

	/**
	 * @param name
	 * @param mCards
	 * @param t
	 * @param d
	 * @param l
	 */
	public AI(String name, HashSet<MurderCard> mCards, Tile t, Game g, DetectiveCard d, Difficulty l) {
		this.name = name;
		this.mCards = mCards;
		this.position = t;
		this.dCard = d;
		this.g = g;
		this.level = l;
		this.immuneToSuggestion = false;
		this.isAccusation = false;
	}

	public static void clearScreen() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void doTurn() {
		revealCards(new ArrayList<>(this.mCards));
		System.out.println();
		clearScreen();
		//g.showBoard();
		System.out.println(toString());
		System.out.println("\n" + this.dCard.toString());
		switch (level) {
			case EASY:
				rollDiceAndMove();
				if (this.position instanceof RoomTile) {
					ArrayList<MurderCard> suggestion = makeSuggestion();
					if (isAccusation) {
						makeAccusation(suggestion);
					}
				}
				break;
			//if AI is on a room tile, AI will check if that room is blank on d-card
			//if unmarked, AI will stay on roomTile and make another suggestion
			//else AI checks if room has shortcut and if shortcut room is unmarked on d-card
			//if so, AI uses shortcut
			//else AI rolls dice and moves
			case MEDIUM:
				ArrayList<MurderCard> suggestion;
				if (this.position instanceof RoomTile) {
					if (this.dCard.rooms.get(this.dCard.getRoomMCard(((RoomTile) this.position).getRoom().getName())).equals(DetectiveCard.Mark.BLANK)) {
						suggestion = makeSuggestion();
						if (isAccusation) {
							makeAccusation(suggestion);
						}
					} else if (((RoomTile) this.position).getRoom().getShortcut() != null) {
						if (this.dCard.rooms.get(this.dCard.getRoomMCard(((RoomTile) this.position).getRoom().getShortcut().getName())).equals(DetectiveCard.Mark.BLANK)) {
							useShortcut((RoomTile) this.position);
						}
					} else {
						rollDiceAndMove();
						if (this.position instanceof RoomTile) {
							suggestion = makeSuggestion();
							if (isAccusation) {
								makeAccusation(suggestion);
							}
						}
					}
				} else {
					rollDiceAndMove();
					if (this.position instanceof RoomTile) {
						suggestion = makeSuggestion();
						if (isAccusation) {
							makeAccusation(suggestion);
						}
					} else if (this.position instanceof SpecialTile) {
						this.drawIntrigue();
					}
				}
				break;
		}
		System.out.println("Turn End");
	}

	/**
	 * This method should ONLY be called after all players have finished
	 * revealing their cards in response to the current AI's suggestion
	 */
	@Override
	public void revealCards(ArrayList<MurderCard> revealed) {
		if (revealed.size() > 0) {
			System.out.println("Revealed Cards: " + revealed.size());
			System.out.println(Arrays.toString(revealed.toArray()));
			for (MurderCard m : revealed) {
				this.dCard.mark(m.toString());
			}
		}
	}

	@Override
	public void rollDiceAndMove() {
		int roll = this.g.d.roll(), rand;
		System.out.println(this.name + " rolled a " + roll);
		ArrayList<Tile> accessibleTiles = this.g.b.diceRoll(roll, this.position);
		System.out.println("Accessible Tiles:\n");
		for (int i = 0; i < accessibleTiles.size(); i++)
			System.out.println((i + 1) + ": " + accessibleTiles.get(i).toString());

		ArrayList<RoomTile> accessibleRooms = this.g.b.getReachableRooms(roll, this.position);
		switch (this.level) {
			//Easy mode:
			//Summary:  This AI will prioritise rooms and specials over normal tiles.
			//          AI will never repeatedly go back to the same room twice
			//check if a room door is in list of reachable tiles
			//if so, AI chooses room door
			//if multiple rooms are accessible, AI randomly chooses 1 to move to
			//else AI moves to random accessible tile on board
			case EASY:
				if (!accessibleRooms.isEmpty()) {
					rand = randomGenerator.nextInt(accessibleRooms.size());
					setPosition(accessibleRooms.get(rand).getRoom().getRoomIndex()); // // ensures the room index tile is set
					System.out.println("\n" + this.name + " has entered the " + ((RoomTile) this.position).getRoom().getName());
				} else {
					rand = randomGenerator.nextInt(accessibleTiles.size());
					setPosition(accessibleTiles.get(rand));
				}
				break;
			//Medium mode:
			//check if a room door is in list of reachable tiles
			//if so, AI checks if room has already AI chooses room door
			//if multiple rooms are accessible:
			//AI uses detectiveCard to see if any unmarked rooms match an accessible room
			//if so AI chooses first matching room from list that appears in detective card
			//else if an accessible tile is a special tile:
			//AI moves to special tile
			//else AI moves to random accessible tile on board
			case MEDIUM:
				if (!accessibleRooms.isEmpty()) {
					if (accessibleRooms.size() >= 2) {
						for (int i = 0; i < accessibleRooms.size(); i++) {
							String roomName = accessibleRooms.get(i).getRoom().getName();
							MurderCard roomCard = this.dCard.getRoomMCard(roomName);
							if (this.dCard.rooms.get(roomCard).equals(DetectiveCard.Mark.BLANK)) {
								setPosition(accessibleRooms.get(i).getRoom().getRoomIndex());
								System.out.println("\n" + this.name + " has entered the " + ((RoomTile) this.position).getRoom().getName());
								break;
							}
						}
					} else {    // if there is only 1 room accessible after dice roll
						setPosition(accessibleRooms.get(0).getRoom().getRoomIndex());
						System.out.println("\n" + this.name + " has entered the " + ((RoomTile) this.position).getRoom().getName());
					}
				} else if (hasSpecialTile(accessibleTiles)) {    //if there are no rooms accessible
					for (Tile t : accessibleTiles) {
						if (t instanceof SpecialTile) {
							drawIntrigue();
							break;
						}
					}
					break;
				} else {
					//if no possible rooms accessible and no special tiles accessible
					rand = randomGenerator.nextInt(accessibleTiles.size());
					while (accessibleTiles.get(rand) instanceof RoomTile)    //chosen tile should not be a room tile
						rand = randomGenerator.nextInt(accessibleTiles.size());
					setPosition(accessibleTiles.get(rand));
				}
				break;
			//Hard Mode
			//
			//
			//
			case HARD:
				//TODO
				break;
		}
	}

	private void useShortcut(RoomTile position) {
		this.setPosition(position.getRoom().getShortcut().getRoomIndex());
	}

	public ArrayList<MurderCard> makeSuggestion() {
		ArrayList<MurderCard> suggestion = new ArrayList<>();
		System.out.println(this.name + " has made a suggestion...");
		System.out.println("****************************************");
		switch (this.level) {
			//Easy mode:
			//AI checks if a character in their detective card is unmarked (blank)
			//if so, AI selects first character that appears blank
			//same process applied to the weapon
			//chosen weapons and character cards are added to suggestion
			case EASY:
				for (MurderCard card : this.dCard.characters.keySet()) {
					if (this.dCard.characters.get(card).equals(DetectiveCard.Mark.BLANK) && !this.mCards.contains(card)) {
						suggestion.add(card);
						break;
					}
				}
				for (MurderCard card : this.dCard.weapons.keySet()) {
					if (this.dCard.weapons.get(card).equals(DetectiveCard.Mark.BLANK) && !this.mCards.contains(card)) {
						suggestion.add(card);
						break;
					}
				}
                /*
                if (suggestion.isEmpty()){
                    int rand = randomGenerator.nextInt(this.dCard.characters.keySet().size());
                    List<MurderCard> RoomCards = new ArrayList<>(this.dCard.characters.keySet());
                    suggestion.add(this.dCard.characters.get(RoomCards.get(rand)));
                }
                 */
				suggestion.add(dCard.getRoomMCard(((RoomTile) this.position).getRoom().getName()));
				System.out.println(Arrays.toString(suggestion.toArray()));
				break;
			//Medium mode:
			//AI checks if a character in dCard is unmarked
			//AI checks if any unmarked characters are not in AI's 'current hand'
			//AI selects character card that is unmarked and not in 'current hand'
			//Same two checks carried out for the weapons
			//selected weapon and character cards are added to suggestion
			case MEDIUM:
				ArrayList<MurderCard> blankCards = new ArrayList<>();
				for (MurderCard card : this.dCard.characters.keySet()) {
					if (this.dCard.characters.get(card).equals(DetectiveCard.Mark.BLANK)) {
						if (!this.mCards.contains(card)) {
							suggestion.add(card);
							break;
						}
						blankCards.add(card);
					}
					//if all characters that are unmarked are in the AI's holding hand....
					//AI picks a random character that is unmarked
					if(blankCards.isEmpty()){
						List<MurderCard> keysAsArray = new ArrayList<>(this.dCard.characters.keySet());
						suggestion.add(keysAsArray.get(randomGenerator.nextInt(keysAsArray.size())));
					} else {
						suggestion.add(blankCards.get(randomGenerator.nextInt(blankCards.size())));
					}
				}
				for (MurderCard card : this.dCard.weapons.keySet()) {
					blankCards = new ArrayList<>();
					if (this.dCard.weapons.get(card).equals(DetectiveCard.Mark.BLANK)) {
						if (!this.mCards.contains(card)) {
							suggestion.add(card);
							break;
						}
						blankCards.add(card);
					}
					//if all weapons that are unmarked are in the AI's holding hand....
					//AI picks a random weapon that is unmarked
					if(blankCards.isEmpty()){
						List<MurderCard> keysAsArray = new ArrayList<>(this.dCard.weapons.keySet());
						suggestion.add(keysAsArray.get(randomGenerator.nextInt(keysAsArray.size())));
					} else {
						suggestion.add(blankCards.get(randomGenerator.nextInt(blankCards.size())));
					}
				}
				suggestion.add(dCard.getRoomMCard(((RoomTile) this.position).getRoom().getName()));
				System.out.println(Arrays.toString(suggestion.toArray()));
				break;
			//Hard Mode:
			//
			case HARD:
				//TODO
				break;
		}
		this.isAccusation = this.g.doSuggestion(suggestion);
		return suggestion;
	}

	public void makeAccusation(ArrayList<MurderCard> accusation) {
		System.out.println(this.name + " has made an accusation!");
		System.out.println(Arrays.toString(accusation.toArray()));
		System.out.println("------------------------------------------------------1");
		this.g.doAccusation(accusation);
	}

	@Override
	public MurderCard answerSuggestion(ArrayList<MurderCard> suggestion) {
		clearScreen();
		System.out.println(toString());
		ArrayList<MurderCard> matches = new ArrayList<>();
		System.out.println("\nCalled Suggestion: "+Arrays.toString(suggestion.toArray()));

		for (MurderCard m : suggestion) {
			if (mCards.contains(m)) {
				matches.add(m);
			}
		}
		if (matches.isEmpty()) {
			System.out.println("No Matches\n");
			return null;
		} else {
			if (immuneToSuggestion) {
				immuneToSuggestion = false;
				System.out.println("suggestion immunity used");
				return null;
			} else {
				//return random match, can be chosen, but will be implemented later
				Collections.shuffle(matches);
				System.out.println(this.name + " revealed " + matches.get(0) + " " + matches.get(0).getClass());
				return matches.get(0);
			}
		}
	}

	@Override
	public String toString() {
		String s = "AI player: " + name + "\nDiff: " + this.level.toString() + "\n" + "Location: ";
		if (position instanceof RoomTile) {
			s += ((RoomTile) position).getRoom().getName();
		} else {
			s += position.getClass().getName() + " (x:" + position.getCoords().x + "  y:" + position.getCoords().y + ")";
		}
		return s;
	}

	private boolean hasSpecialTile(ArrayList<Tile> tiles) {
		for(Tile t : tiles)
			if(t instanceof SpecialTile)
				return true;
		return false;
	}
}

/*
class Easy extends AI{
    public Easy(Difficulty l) {
        super(name, mCards, t, g, d, l);
    }
}
 */
