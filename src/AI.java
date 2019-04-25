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
		EASY("easy"), MEDIUM("medium"), HARD("hard");
		private String representation;

		Difficulty(String s) {
			this.representation = s;
		}

		@Override
		public String toString() {
			return representation;
		}
	}

	private Difficulty level;

	/**
	 *
	 * @param name
	 * @param mCards
	 * @param t
	 * @param d
	 * @param l
	 */
	public AI(String name, HashSet<MurderCard> mCards, Tile t, Game g,DetectiveCard d, Difficulty l) {
		this.name = name;
		this.mCards = mCards;
		this.position = t;
		this.dCard = d;
		this.level = l;
		this.immuneToSuggestion = false;
	}


        @Override
	public void doTurn() {
		switch (level) {
			case EASY:
				rollDiceAndMove();
				if (this.position instanceof RoomTile) {
					makeSuggestion();
				}
				break;
			//if AI is on a room tile, AI will check if that room is not marked on d-card
			//if unmarked, AI will stay on roomTile and make another suggestion
			//else AI checks if room has shortcut and if shortcut room is unmarked on d-card
			//if so, AI uses shortcut
			//else AI rolls dice and moves
			case MEDIUM:
				if (this.position instanceof RoomTile) {
					if (this.dCard.rooms.get(this.dCard.getRoomMCard(((RoomTile) this.position).getRoom().getName())).equals(DetectiveCard.Mark.BLANK)) {
						makeSuggestion();
					} else if (((RoomTile) this.position).getRoom().getShortcut() != null) {
						if (this.dCard.rooms.get(this.dCard.getRoomMCard(((RoomTile) this.position).getRoom().getShortcut().getName())).equals(DetectiveCard.Mark.BLANK)) {
							useShortcut((RoomTile) this.position);
						}
					} else {
						rollDiceAndMove();
					}
				} else {
					rollDiceAndMove();
				}
				break;
		}
	}

	/**
	 * This method should ONLY be called after all players have finished revealing their cards in
	 * response to the current AI's suggestion
	 */
        
       
	@Override
        public void revealCards(ArrayList<MurderCard> revealed){
            for(MurderCard m: revealed){
                this.dCard.mark(m.getName());
            }
        }
        


	public void rollDiceAndMove() {
		int roll = this.g.d.roll();
		ArrayList<Tile> accessibleTiles = this.g.b.getReachableTiles(roll, this.position);
		ArrayList<RoomTile> accessibleRooms = this.g.b.getReachableRooms(roll, this.position);
		switch (this.level) {
			//Easy mode:
			//check if a room door is in list of reachable tiles
			//if so, AI chooses room door
			//if multiple rooms are accessible, AI randomly chooses 1 to move to
			//else AI moves to random accessible tile on board
			case EASY:
				if (!accessibleRooms.isEmpty()) {
					int rand = randomGenerator.nextInt(accessibleRooms.size());
					setPosition(accessibleRooms.get(rand));
				} else {
					int rand = randomGenerator.nextInt(accessibleTiles.size());
					setPosition(accessibleTiles.get(rand));
					if (this.position instanceof SpecialTile) {
						drawIntrigue();
					}
				}
				break;
			//Medium mode:
			//check if a room door is in list of reachable tiles
			//if so, AI chooses room door
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
								setPosition(accessibleRooms.get(i));
								return;
							}
						}
					} else {    // if there is only 1 room accessible after dice roll
						setPosition(accessibleRooms.get(0));
						return;
					}
				} else {    //if there are no rooms accessible
					for (Tile t : accessibleTiles) {
						if (t instanceof SpecialTile) {
							drawIntrigue();
							return;
						}
					}
				}
				//if no possible rooms accessible and no special tiles accessible
				int rand = randomGenerator.nextInt(accessibleTiles.size());
				setPosition(accessibleTiles.get(rand));
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

	private ArrayList<MurderCard> makeSuggestion() {
		ArrayList<MurderCard> suggestion = new ArrayList<>();
		switch (this.level) {
			//Easy mode:
			//AI checks if a character in their detective card is unmarked (blank)
			//if so, AI selects first character that appears blank
			//same process applied to the weapon
			//chosen weapons and character cards are added to suggestion
			case EASY:
				for (MurderCard card : this.dCard.characters.keySet()) {
					if (this.dCard.characters.get(card).equals(DetectiveCard.Mark.BLANK)) {
						suggestion.add(card);
						break;
					}
				}
				for (MurderCard card : this.dCard.weapons.keySet()) {
					if (this.dCard.weapons.get(card).equals(DetectiveCard.Mark.BLANK)) {
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
					suggestion.add(blankCards.get(randomGenerator.nextInt(blankCards.size())));
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
					suggestion.add(blankCards.get(randomGenerator.nextInt(blankCards.size())));
				}
				suggestion.add(dCard.getRoomMCard(((RoomTile) this.position).getRoom().getName()));
				break;
			//Hard Mode:
			//
			//
			//
			case HARD:
				//TODO
				break;
		}
		return suggestion;
	}

	private ArrayList<MurderCard> makeAccusation() {
		return this.makeSuggestion();
	}

        @Override
	public MurderCard answerSuggestion(ArrayList<MurderCard> suggestion) {
		ArrayList<MurderCard> matches = new ArrayList<>();
		for (MurderCard m : suggestion) {
			if (mCards.contains(m)) {
				matches.add(m);
			}
		}
		if (matches.isEmpty()) {
			return null;
		} else {
			if (immuneToSuggestion) {
				immuneToSuggestion = false;
				return null;
			} else {
				//return random match, can be choosen, but will be implemented later
				Collections.shuffle(matches);

				return (matches.get(0));
			}
		}
	}
        
            @Override
    public String toString(){
        String s = "AI player: " +name +"   Diff: "+this.level.toString()+"\n" +"Location: ";
        if(position instanceof RoomTile){
            s+= ((RoomTile)position).getRoom().getName();
        }
        else{
            s+= position.getClass().getName() + "  x:"+ position.getCoords().x+"  y: "+position.getCoords().y;
        }
        return s;
    }
}

