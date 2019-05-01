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
    private RoomTile roomBuffer;
    private Difficulty level;

    /**
     *
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
        this.roomBuffer = null;
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
        revealCards(new ArrayList<MurderCard>(this.mCards));
        clearScreen();
        //g.showBoard();
        System.out.println(toString());
        System.out.println("\n"+this.dCard.toString());
        switch (level) {
            case EASY:
                rollDiceAndMove();
                if (this.position instanceof RoomTile) {
                    makeSuggestion();
                }
                else if(this.position instanceof SpecialTile){
                    this.drawIntrigue();
                }
                break;
            //if AI is on a room tile, AI will check if that room is blank on d-card
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
                        if (this.position instanceof RoomTile) {
                            makeSuggestion();
                        } else if(this.position instanceof SpecialTile){
                            this.drawIntrigue();
                        }
                    }
                } else {
                    rollDiceAndMove();
                    if (this.position instanceof RoomTile) {
                        makeSuggestion();
                    }
                    else if(this.position instanceof SpecialTile){
                        this.drawIntrigue();
                    }
                }
                break;
        }
        System.out.println("TurnEnd");
    }

    /**
     * This method should ONLY be called after all players have finished
     * revealing their cards in response to the current AI's suggestion
     */
    @Override
    public void revealCards(ArrayList<MurderCard> revealed){
        if(revealed.size() > 0){
            System.out.println(Arrays.toString(revealed.toArray()));
            System.out.println(revealed.size());
            for(MurderCard m: revealed){
                    if(m instanceof MurderCard){
                        System.out.println("returns card: " + m.getName());
                        this.dCard.mark(m.getName());
                }
            }
        }
    }

    @Override
    public void rollDiceAndMove() {
        int rand;
        int roll = this.g.d.roll();
        System.out.println(this.name + " rolled a "+roll);
        ArrayList<Tile> accessibleTiles = this.g.b.diceRoll(roll, this.position);
        System.out.println("Accessible Tiles:\n");
        for(int i = 0; i < accessibleTiles.size();i++)
            System.out.println((i+1) +": "+accessibleTiles.get(i).toString());

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
                    setPosition(accessibleRooms.get(rand).getRoom().getRoomIndex());
                    System.out.println(this.name + " has entered the "+((RoomTile) this.position).getRoom().getName());
                } else {
                    rand = randomGenerator.nextInt(accessibleTiles.size());
                    setPosition(accessibleTiles.get(rand));
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
                rand = randomGenerator.nextInt(accessibleTiles.size());
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

    public void makeSuggestion() {
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
            case HARD:
                //TODO
                break;
        }

        this.g.doSuggestion(suggestion);
    }

    public void makeAccusation() {
        //this.g.doAccusation(suggestion);
    }

    @Override
    public MurderCard answerSuggestion(ArrayList<MurderCard> suggestion) {
        clearScreen();
        System.out.println(toString());
        ArrayList<MurderCard> matches = new ArrayList<>();
        System.out.println("AI Suggestion");
        for (MurderCard m : suggestion) {
            System.out.println("Card: "+m.name);
            if (mCards.contains(m)) {
                matches.add(m);
            }
        }
        if (matches.isEmpty()) {
            System.out.println("no Matches");
            return null;
        } else {
            if (immuneToSuggestion) {
                immuneToSuggestion = false;
                System.out.println("suggestion immunity used");
                return null;
            } else {
                System.out.println("Cards matched with suggestion are:");
                for(int i = 0; i<matches.size(); i++){
                    System.out.println((i+1) +"  "+matches.get(i).name + " "+ matches.get(i).getClass().getName());
                }
                //return random match, can be choosen, but will be implemented later
                Collections.shuffle(matches);
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
            s += position.getClass().getName() + " (x:" + position.getCoords().x + "  y:" + position.getCoords().y+")";
        }
        return s;
    }
}

/*
class Easy extends AI{
    public Easy(Difficulty l) {
        super(name, mCards, t, g, d, l);
    }
}
 */
